package info.bitrich.xchangestream.kraken;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;

import java.io.IOException;

public class KrakenStreamingService extends JsonNettyStreamingService {

    public KrakenStreamingService(String apiUrl) {
        super(apiUrl,Integer.MAX_VALUE);
    }

    @Override
    public void messageHandler(String message) {
        super.messageHandler(message);
    }

    @Override
    protected void sendObjectMessage(Object message) {
        super.sendObjectMessage(message);
    }

    @Override
    protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
        return super.getWebSocketClientExtensionHandler();
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        return null;
    }

    @Override
    public void sendMessage(String message) {
        super.sendMessage(message);
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        return null;
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        return null;
    }
}
