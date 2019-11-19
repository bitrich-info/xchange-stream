package info.bitrich.xchangestream.bitstamp.v2;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.bitstamp.v2.dto.BitstampWebSocketData;
import info.bitrich.xchangestream.bitstamp.v2.dto.BitstampWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Bitstamp WebSocket V2 streaming service implementation
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class BitstampStreamingService extends JsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(BitstampStreamingService.class);

    private static final String JSON_CHANNEL = "channel";
    private static final String JSON_EVENT = "event";
    private static final String JSON_DATA = "data";
    private static final ArrayList<String> JSON_DATA2 = new ArrayList<String>();
    
    public BitstampStreamingService(String apiUrl) {
        super(apiUrl, Integer.MAX_VALUE);
        
        // add events
        JSON_DATA2.add("trade");
    }

    @Override
    protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
        return null;
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        JsonNode jsonNode = message.get(JSON_CHANNEL);
        if (jsonNode != null) {
            return jsonNode.asText();
        }
        throw new IOException("Channel name can't be evaluated from message");
    }

    @Override
    protected void handleMessage(JsonNode message) {
        JsonNode channelJsonNode = message.get(JSON_CHANNEL);
        JsonNode eventJsonNode = message.get(JSON_EVENT);

        if (channelJsonNode == null || eventJsonNode == null) {
            LOG.error("Received JSON message does not contain {} and {} fields. Skipped...", JSON_CHANNEL, JSON_EVENT);
            return;
        }

        String channel = channelJsonNode.asText();
        String event = eventJsonNode.asText();

        if (!channels.containsKey(channel)) {
            LOG.warn("The message has been received from disconnected channel '{}'. Skipped.", channel);
            return;
        }
        // event: trade
        //if (event.equals(JSON_DATA)) {
        if (JSON_DATA2.contains(event)) {
        	super.handleMessage(message);
        }
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        BitstampWebSocketSubscriptionMessage subscribeMessage = generateSubscribeMessage(channelName, "bts:subscribe");
        return objectMapper.writeValueAsString(subscribeMessage);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        BitstampWebSocketSubscriptionMessage subscribeMessage = generateSubscribeMessage(channelName, "bts:unsubscribe");
        return objectMapper.writeValueAsString(subscribeMessage);
    }

    private BitstampWebSocketSubscriptionMessage generateSubscribeMessage(String channelName, String channel) {
        return new BitstampWebSocketSubscriptionMessage(channel, new BitstampWebSocketData(channelName));
    }
}
