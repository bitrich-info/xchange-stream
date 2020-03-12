package info.bitrich.xchangestream.kraken;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import info.bitrich.xchangestream.kraken.dto.KrakenSubscriptionConfig;
import info.bitrich.xchangestream.kraken.dto.KrakenSubscriptionMessage;
import info.bitrich.xchangestream.kraken.dto.KrakenSubscriptionStatusMessage;
import info.bitrich.xchangestream.kraken.dto.KrakenSystemStatus;
import info.bitrich.xchangestream.kraken.dto.enums.KrakenEventType;
import info.bitrich.xchangestream.kraken.dto.enums.KrakenSubscriptionName;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import info.bitrich.xchangestream.service.netty.WebSocketClientHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.kraken.KrakenAdapters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static info.bitrich.xchangestream.kraken.KrakenStreamingMarketDataService.KRAKEN_CHANNEL_DELIMITER;
import static info.bitrich.xchangestream.kraken.dto.enums.KrakenEventType.subscribe;

/** @author makarid, pchertalev */
public class KrakenStreamingService extends JsonNettyStreamingService {
  private static final Logger LOG = LoggerFactory.getLogger(KrakenStreamingService.class);
  private static final String EVENT = "event";
  private final Map<Integer, String> channelIds = new ConcurrentHashMap<>();
  private ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
  private final boolean isPrivate;

  private final Map<Integer, Set<String>> subscriptionRequestMap = new ConcurrentHashMap<>();

  public KrakenStreamingService(boolean isPrivate, String uri) {
    super(uri, Integer.MAX_VALUE);
    this.isPrivate = isPrivate;
  }

  @Override
  public boolean processArrayMassageSeparately() {
    return false;
  }

  @Override
  protected void handleMessage(JsonNode message) {
    String channelName = getChannel(message);

    try {
      JsonNode event = message.get(EVENT);
      KrakenEventType krakenEvent;
      if (event != null && (krakenEvent = KrakenEventType.getEvent(event.textValue())) != null) {
        switch (krakenEvent) {
          case pong:
            LOG.debug("Pong received");
            break;
          case heartbeat:
            LOG.debug("Heartbeat received");
            break;
          case systemStatus:
            KrakenSystemStatus systemStatus = mapper.treeToValue(message, KrakenSystemStatus.class);
            LOG.info("System status: {}", systemStatus);
            break;
          case subscriptionStatus:
            KrakenSubscriptionStatusMessage statusMessage =
                mapper.treeToValue(message, KrakenSubscriptionStatusMessage.class);
            Integer reqid = statusMessage.getReqid();
            String currencyPair =
                KrakenAdapters.adaptCurrencyPair(statusMessage.getPair().replace("/", ""))
                    .toString();
            if (!isPrivate && reqid != null) {
              Set<String> channelsList = subscriptionRequestMap.get(reqid);
              channelName =
                  statusMessage.getKrakenSubscriptionConfig().getName()
                      + KRAKEN_CHANNEL_DELIMITER
                      + currencyPair;
              channelsList.remove(channelName);
              if (channelsList.isEmpty()) {
                subscriptionRequestMap.remove(reqid);
              }
            }
            switch (statusMessage.getStatus()) {
              case subscribed:
                LOG.info("Channel {} has been subscribed", channelName);
                if (statusMessage.getChannelID() != null) {
                  channelIds.put(statusMessage.getChannelID(), channelName);
                }
                break;
              case unsubscribed:
                LOG.info("Channel {} has been unsubscribed", channelName);
                channelIds.remove(statusMessage.getChannelID());
                break;
              case error:
                LOG.error(
                    "Channel {} has been failed: {}", channelName, statusMessage.getErrorMessage());
            }
            break;
          case error:
            LOG.error(
                "Error received: {}",
                message.has("errorMessage")
                    ? message.get("errorMessage").asText()
                    : message.toString());
            break;
          default:
            LOG.warn("Unexpected event type has been received: {}", krakenEvent);
        }
        return;
      }
    } catch (JsonProcessingException e) {
      LOG.error("Error reading message: {}", e.getMessage(), e);
    }

    if (!message.isArray() || channelName == null) {
      LOG.error("Unknown message: {}", message.toString());
      return;
    }

    super.handleMessage(message);
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) throws IOException {
    String channelName = null;
    if (message.has("channelID")) {
      channelName = channelIds.get(message.get("channelID").asInt());
    }
    if (message.has("channelName")) {
      channelName = message.get("channelName").asText();
    }

    if (message.isArray()) {
      if (message.get(0).isInt()) {
        channelName = channelIds.get(message.get(0).asInt());
      }
      if (message.get(1).isTextual()) {
        channelName = message.get(1).asText();
      }
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("ChannelName {}", StringUtils.isBlank(channelName) ? "not defined" : channelName);
    }
    return channelName;
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    int reqID = Math.abs(UUID.randomUUID().hashCode());
    String[] channelData = channelName.split(KRAKEN_CHANNEL_DELIMITER);
    KrakenSubscriptionName subscriptionName = KrakenSubscriptionName.valueOf(channelData[0]);

    if (isPrivate) {
      String token = (String) args[0];

      KrakenSubscriptionMessage subscriptionMessage =
          new KrakenSubscriptionMessage(
              reqID, subscribe, null, new KrakenSubscriptionConfig(subscriptionName, null, token));

      return objectMapper.writeValueAsString(subscriptionMessage);
    } else {
      String pair = channelData[1];

      Integer depth = null;
      if (args.length > 0 && args[0] != null) {
        depth = (Integer) args[0];
      }
      subscriptionRequestMap.put(reqID, Sets.newHashSet(channelName));

      KrakenSubscriptionMessage subscriptionMessage =
          new KrakenSubscriptionMessage(
              reqID,
              subscribe,
              Collections.singletonList(pair),
              new KrakenSubscriptionConfig(subscriptionName, depth, null));
      return objectMapper.writeValueAsString(subscriptionMessage);
    }
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    int reqID = Math.abs(UUID.randomUUID().hashCode());
    String[] channelData = channelName.split(KRAKEN_CHANNEL_DELIMITER);
    KrakenSubscriptionName subscriptionName = KrakenSubscriptionName.valueOf(channelData[0]);

    if (isPrivate) {
      KrakenSubscriptionMessage subscriptionMessage =
          new KrakenSubscriptionMessage(
              reqID,
              KrakenEventType.unsubscribe,
              null,
              new KrakenSubscriptionConfig(subscriptionName, null, null));
      return objectMapper.writeValueAsString(subscriptionMessage);
    } else {
      String pair = channelData[1];

      subscriptionRequestMap.put(reqID, Sets.newHashSet(channelName));
      KrakenSubscriptionMessage subscriptionMessage =
          new KrakenSubscriptionMessage(
              reqID,
              KrakenEventType.unsubscribe,
              Collections.singletonList(pair),
              new KrakenSubscriptionConfig(subscriptionName));
      return objectMapper.writeValueAsString(subscriptionMessage);
    }
  }

  @Override
  protected WebSocketClientHandler getWebSocketClientHandler(
      WebSocketClientHandshaker handshaker,
      WebSocketClientHandler.WebSocketMessageHandler handler) {
    LOG.info("Registering KrakenWebSocketClientHandler");
    return new KrakenWebSocketClientHandler(handshaker, handler);
  }

  @Override
  protected Completable openConnection() {

    KrakenSubscriptionMessage ping =
        new KrakenSubscriptionMessage(null, KrakenEventType.ping, null, null);

    subscribeConnectionSuccess()
        .subscribe(
            o ->
                Observable.interval(30, TimeUnit.SECONDS)
                    .takeWhile(t -> isSocketOpen())
                    .subscribe(t -> sendObjectMessage(ping)));

    return super.openConnection();
  }

  private WebSocketClientHandler.WebSocketMessageHandler channelInactiveHandler = null;

  /**
   * Custom client handler in order to execute an external, user-provided handler on channel events.
   * This is useful because it seems Kraken unexpectedly closes the web socket connection.
   */
  class KrakenWebSocketClientHandler extends NettyWebSocketClientHandler {

    public KrakenWebSocketClientHandler(
        WebSocketClientHandshaker handshaker, WebSocketMessageHandler handler) {
      super(handshaker, handler);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
      super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
      super.channelInactive(ctx);
      if (channelInactiveHandler != null) {
        channelInactiveHandler.onMessage("WebSocket Client disconnected!");
      }
    }
  }

  @Override
  public void resubscribeChannels() {
    if (isPrivate) {
      super.resubscribeChannels();
    } else {
      subscriptionRequestMap.clear();
      channelIds.clear();
      HashMap<String, KrakenSubscriptionMessage> messages = new HashMap<>();
      for (Map.Entry<String, Subscription> entry : super.channels.entrySet()) {

        String[] channelData = entry.getKey().split(KRAKEN_CHANNEL_DELIMITER);
        KrakenSubscriptionName subscriptionName = KrakenSubscriptionName.valueOf(channelData[0]);

        String pair = channelData[1];
        Object[] args = entry.getValue().getArgs();
        Integer depth = null;
        if (args.length > 0 && args[0] != null) {
          depth = (Integer) args[0];
        }

        Integer finalDepth = depth;
        KrakenSubscriptionMessage toSend =
            messages.computeIfAbsent(
                subscriptionName + (depth == null ? "" : (KRAKEN_CHANNEL_DELIMITER + depth)),
                d ->
                    new KrakenSubscriptionMessage(
                        Math.abs(UUID.randomUUID().hashCode()),
                        subscribe,
                        new ArrayList<>(),
                        new KrakenSubscriptionConfig(subscriptionName, finalDepth, null)));
        toSend.getPairs().add(pair);
        Set<String> channelsSet =
            subscriptionRequestMap.computeIfAbsent(toSend.getReqid(), rid -> new HashSet<>());
        channelsSet.add(entry.getKey());
      }

      for (KrakenSubscriptionMessage message : messages.values()) {
        try {
          sendMessage(objectMapper.writeValueAsString(message));
        } catch (IOException e) {
          LOG.error("Failed to reconnect channel: {}", message.getPairs());
        }
      }
    }
  }
}
