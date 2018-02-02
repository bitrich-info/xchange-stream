package info.bitrich.xchangestream.poloniex2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.poloniex2.dto.PoloniexWebSocketEvent;
import info.bitrich.xchangestream.poloniex2.dto.PoloniexWebSocketEventsTransaction;
import info.bitrich.xchangestream.poloniex2.dto.PoloniexWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.netty.WebSocketClientHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lukas Zaoralek on 10.11.17.
 */
public class PoloniexStreamingService extends JsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(PoloniexStreamingService.class);

    private static final String HEARTBEAT = "1010";

    private final Map<String, String> subscribedChannels = new HashMap<>();
    private final Map<String, Observable<JsonNode>> subscriptions = new HashMap<>();
    private boolean isManualDisconnect = false;

    private Instant lastHeartBeat = null;
    private boolean isWebsocketWatcherRunning = false;
    private boolean isReconnectingWebsocket = false;

    public PoloniexStreamingService(String apiUrl) {
        super(apiUrl, Integer.MAX_VALUE);
    }

    private synchronized Instant getLastHeartBeat() {
        return lastHeartBeat;
    }

    private synchronized void setLastHeartBeat(Instant lastHeartBeat) {
        this.lastHeartBeat = lastHeartBeat;
    }

    @Override
    protected void handleMessage(JsonNode message) {
        if (message.isArray()) {
            Integer channelId = new Integer(message.get(0).toString());
            if (channelId > 0 && channelId < 1000) {
                if (!message.has(2)) {
                    LOG.warn("Websocket message doesn't contain events: {}", message);
                    return;
                }
                JsonNode events = message.get(2);
                if (events.isArray()) {
                    JsonNode event = events.get(0);
                    if (event.get(0).toString().equals("\"i\"")) {
                        if (event.get(1).has("orderBook")) {
                            String currencyPair = event.get(1).get("currencyPair").asText();
                            LOG.info("Register {} as {}", String.valueOf(channelId), currencyPair);
                            subscribedChannels.put(String.valueOf(channelId), currencyPair);
                        }
                    }
                }
            }
        }
        if (message.has("error")) {
            LOG.error("Error with message: " + message.get("error").asText());
            return;
        }
        super.handleMessage(message);
    }

    @Override
    public void messageHandler(String message) {
        LOG.debug("Received message: {}", message);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        // Parse incoming message to JSON
        try {
            jsonNode = objectMapper.readTree(message);
        } catch (IOException e) {
            LOG.error("Error parsing incoming message to JSON: {}", message);
            return;
        }

        setLastHeartBeat(Instant.now());

        if (jsonNode.isArray() && jsonNode.size() < 3) {
            if (jsonNode.get(0).asText().equals(HEARTBEAT) || jsonNode.get(0).asText().equals("1002")) {
                return;
            }
        }

        handleMessage(jsonNode);
    }

    @Override
    public Observable<JsonNode> subscribeChannel(String channelName, Object... args) {
        if (!channels.containsKey(channelName)) {
            Observable<JsonNode> subscription = super.subscribeChannel(channelName, args);
            subscriptions.put(channelName, subscription);
        }

        return subscriptions.get(channelName);
    }

    public Observable<PoloniexWebSocketEvent> subscribeCurrencyPairChannel(CurrencyPair currencyPair) {
        String channelName = currencyPair.counter.toString() + "_" + currencyPair.base.toString();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return subscribeChannel(channelName)
                .flatMapIterable(s -> {
                    PoloniexWebSocketEventsTransaction transaction = mapper.readValue(s.toString(), PoloniexWebSocketEventsTransaction.class);
                    return Arrays.asList(transaction.getEvents());
                }).share();
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        String strChannelId = message.get(0).asText();
        Integer channelId = new Integer(strChannelId);
        if (channelId >= 1000) return strChannelId;
        else return subscribedChannels.get(message.get(0).asText());
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        PoloniexWebSocketSubscriptionMessage subscribeMessage = new PoloniexWebSocketSubscriptionMessage("subscribe",
                channelName);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(subscribeMessage);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        PoloniexWebSocketSubscriptionMessage subscribeMessage = new PoloniexWebSocketSubscriptionMessage("unsubscribe",
                channelName);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(subscribeMessage);
    }

    @Override
    public Completable disconnect() {
        isManualDisconnect = true;
        return super.disconnect();
    }

    private void startWebsocketHealthWatcher() {
        Duration maxLag = Duration.ofSeconds(12);

        // prevent it from being started several times
        if (!isWebsocketWatcherRunning) {
            isWebsocketWatcherRunning = true;
            LOG.info("Starting websocket health watcher for poloniex2");
            new Thread(() -> {
                while (true) {
                    LOG.trace("Websocket watcher thread: isReconnectingWebsocket: {}, lastHeartbeat: {},", isReconnectingWebsocket, getLastHeartBeat());
                    if (!isReconnectingWebsocket && getLastHeartBeat() != null && getLastHeartBeat().plus(maxLag).isBefore(Instant.now())) {
                        isReconnectingWebsocket = true;
                        LOG.warn("Websocket is lagging 12 seconds behind, reconnecting ...");
                        try {
                            // resubscribe will fail if the websocket isn't open and return if it was unsuccessful
                            if (isWebSocketOpen() || connect().blockingAwait(10, TimeUnit.SECONDS)) {
                                // this subscription will cause a reconnect if the websocket was closed
                                resubscribeChannels();

                                // reset heartbeat to prevent redundant reconnects
                                setLastHeartBeat(Instant.now().plus(maxLag));
                            }
                        } catch (Exception e) {
                            LOG.warn("Exception while socket resubscribe! Message: " + e.getMessage());
                        } finally {
                            isReconnectingWebsocket = false;
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }).start();
        }
    }

    @Override
    protected WebSocketClientHandler getWebSocketClientHandler(WebSocketClientHandshaker handshaker,
                                                               WebSocketClientHandler.WebSocketMessageHandler handler) {
        LOG.info("Registering Poloniex2WebSocketClientHandler");
        return new Poloniex2WebSocketClientHandler(handshaker, handler);
    }

    private class Poloniex2WebSocketClientHandler extends WebSocketClientHandler {
        Poloniex2WebSocketClientHandler(WebSocketClientHandshaker handshaker, WebSocketMessageHandler handler) {
            super(handshaker, handler);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            LOG.trace("Poloniex2WebSocketClientHandler: isManualDisconnect: {}, isReconnectingWebsocket: {},", isManualDisconnect, isReconnectingWebsocket);
            if (isManualDisconnect) {
                isManualDisconnect = false;
            } else if (!isReconnectingWebsocket) {
                try {
                    isReconnectingWebsocket = true;
                    super.channelInactive(ctx);
                    LOG.info("Reopening websocket because it was closed by the host");
                    if (isWebSocketOpen() || connect().blockingAwait(10, TimeUnit.SECONDS)) {
                        LOG.info("Resubscribing channels");
                        resubscribeChannels();
                    }
                } catch (Exception ignored) {
                } finally {
                    isReconnectingWebsocket = false;
                }
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            super.channelActive(ctx);
            startWebsocketHealthWatcher();
        }
    }
}
