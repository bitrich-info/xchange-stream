package info.bitrich.xchangestream.kraken;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import info.bitrich.xchangestream.kraken.dto.KrakenEvent;
import info.bitrich.xchangestream.kraken.dto.KrakenSubscription;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.reactivex.CompletableEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @author makarid */
public class KrakenStreamingService extends JsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(KrakenStreamingService.class);
    private String channelName;
    private final Map<String,String> channels = new HashMap<>();


    public KrakenStreamingService(String apiUrl) {
        super(apiUrl,Integer.MAX_VALUE);
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void messageHandler(String message) {
        LOG.debug("Received message: {}", message);
        JsonNode jsonNode;

        // Parse incoming message to JSON
        try {
            jsonNode = objectMapper.readTree(message);
        } catch (IOException e) {
            LOG.error("Error parsing incoming message to JSON: {}", message);
            return;
        }

        handleMessage(jsonNode);

    }

    @Override
    protected void handleMessage(JsonNode message) {
        if (channelName == null) {
            LOG.error("You have to specify channelName first!");
            return;
        }
        if (message.has("errorMessage")) {
            String error = message.get("errorMessage").asText();
            LOG.error("Error with message: " + error);
            return;
        }

        super.handleMessage(message);
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        String channelName = getChannelName();
        if(message.has("channelID")){
            JsonNode subscription = message.get("subscription");
            channels.put(message.get("channelID").toString(),
                    subscription.get("name").textValue()
                            .concat("|")
                            .concat((subscription.has("interval"))?subscription.get("interval").toString():"0")
                            .concat("|")
                            .concat((subscription.has("depth"))?subscription.get("depth").toString():"0")
                            .concat(":")
                            .concat(message.get("pair").textValue())
            );
            channelName = channels.get(message.get("channelID").toString());
            LOG.debug("Inserting new channel: " + channelName);
        }

        if(message.isArray() && message.get(0).isInt()){
            channelName = channels.get(message.get(0).toString());
        }

        LOG.debug("ChannelName: "+ channelName);
        return channelName;
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        return objectMapper.writeValueAsString(args[0]);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {

        KrakenSubscription subscription =
                new KrakenSubscription(
                        channelName.substring(0,channelName.indexOf("|")),
                        Integer.valueOf(channelName.substring(channelName.indexOf("|")+1,channelName.lastIndexOf("|"))),
                        Integer.valueOf(channelName.substring(channelName.lastIndexOf("|")+1,channelName.indexOf(":")))
                );
        List<String> pairs = new ArrayList<>();
        pairs.add(channelName.substring(channelName.indexOf(":")+1));

        KrakenEvent krakenEvent =
                new KrakenEvent("unsubscribe",null,pairs,subscription);

        return objectMapper.writeValueAsString(krakenEvent);
    }
}
