package info.bitrich.xchangestream.huobi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HuobiStreamingService extends JsonNettyStreamingService {

    private ObjectMapper mapper = new ObjectMapper();
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public HuobiStreamingService(String apiUrl) {
        super(apiUrl);
    }

    public HuobiStreamingService(String apiUrl, int maxFramePayloadLength) {
        super(apiUrl, maxFramePayloadLength);
    }

    @Override
    public void messageHandler(String message) {
        super.messageHandler(message);
    }

    @Override
    protected void handleMessage(JsonNode message) {
        super.handleMessage(message);
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        String status = null;
        String ch = null;
        Long ping = null;
        if (message.has("ch")) {
            ch = message.get("ch").textValue();
        }
        if (message.has("status")) {
            status = message.get("status").textValue();
        }
        if (message.has("ping")) {
            ping = message.get("ping").longValue();
        }
        if (status != null && status.equals("ok")) {
            String subbed = message.get("subbed").textValue();
            return "Subscribe [" + subbed + "] is ok";
        } else if (ping != null) { // if ping, we pong
            sendMessage("{\"pong\": " + ping + "}");
            return "ping";
        } else if (ch != null) { // if ch, get message
            return ch;
        } else {
            return "error: " + message.get("err-msg").textValue();
        }
    }

    /**
     * @param channelName market.$symbol.depth.$type
     * @param args        null
     * @return
     * @throws IOException
     */
    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        Map<String, Object> message = new HashMap<>();
        message.put("sub", channelName);
        message.put("id", System.currentTimeMillis());
        return mapper.writeValueAsString(message);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        return channelName;
    }
}
