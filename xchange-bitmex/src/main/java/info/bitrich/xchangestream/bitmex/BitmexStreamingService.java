package info.bitrich.xchangestream.bitmex;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.bitmex.dto.BitmexWebSocketSubscriptionMessage;
import info.bitrich.xchangestream.bitmex.dto.BitmexWebSocketTransaction;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.reactivex.Observable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitmex.service.BitmexDigest;
import org.knowm.xchange.utils.nonce.ExpirationTimeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.SynchronizedValueFactory;

import java.io.IOException;

/**
 * Created by Lukas Zaoralek on 13.11.17.
 */
public class BitmexStreamingService extends JsonNettyStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(BitmexStreamingService.class);
    private ExchangeSpecification exchangeSpecification;

    public BitmexStreamingService(String apiUrl) {
        super(apiUrl, Integer.MAX_VALUE);
        this.exchangeSpecification = null;
    }
    public BitmexStreamingService(String apiUrl, ExchangeSpecification exchangeSpecification) {
        super(apiUrl, Integer.MAX_VALUE);
        this.exchangeSpecification = exchangeSpecification;
    }

    @Override
    protected void handleMessage(JsonNode message) {
//        if (message.has("info") && message.get("info").asText().startsWith("Welcome ")) {
//            if (exchangeSpecification.getApiKey() != null && exchangeSpecification.getSecretKey() != null) {
//                try {
                    //TODO: send this onConnect
//                    sendMessage(getAuthenticateMessage());
//                } catch (Exception e) {
//                    handleError(message, e);
//                }
//            }
//            return;
//        }
        if (message.has("info") || message.has("success")) {
            return;
        }
        if (message.has("error")) {
            String error = message.get("error").asText();
            LOG.error("Error with message: " + error);
            LOG.debug("Error with message: " + message.toString());
            return;
        }

        super.handleMessage(message);
    }

    @Override
    protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
        return null;
    }

    public Observable<BitmexWebSocketTransaction> subscribeBitmexChannel(String channelName) {
        return subscribeChannel(channelName).map(s -> {
            BitmexWebSocketTransaction transaction = objectMapper.treeToValue(s, BitmexWebSocketTransaction.class);
            return transaction;
        })
        .share();
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        final JsonNode data0 = message.get("data").get(0);
        final JsonNode symbolNode = data0 == null ? null : data0.get("symbol");
        final JsonNode tableNode = message.get("table");

        if (symbolNode == null)
            return tableNode.asText();
        else
            return String.format("%s:%s", tableNode.asText(), symbolNode.asText());
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        BitmexWebSocketSubscriptionMessage subscribeMessage = new BitmexWebSocketSubscriptionMessage("subscribe", new String[]{channelName});
        return objectMapper.writeValueAsString(subscribeMessage);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        BitmexWebSocketSubscriptionMessage subscribeMessage = new BitmexWebSocketSubscriptionMessage("unsubscribe", new String[]{});
        return objectMapper.writeValueAsString(subscribeMessage);
    }

    @Override  // called by NettyStreamingService.resubscribeChannels
    public String getAuthenticateMessage() throws IOException {
//        connect().blockingAwait();

        BitmexDigest bitmexDigest = BitmexDigest.createInstance(exchangeSpecification.getSecretKey(), exchangeSpecification.getApiKey() );
        SynchronizedValueFactory<Long> nonceFactory = new ExpirationTimeFactory(30);

        long nonce = nonceFactory.createValue();
        String payload = "GET/realtime" + nonce;
        String digestString = bitmexDigest.digestString(payload);

        BitmexWebSocketSubscriptionMessage subscribeMessage =
                new BitmexWebSocketSubscriptionMessage("authKeyExpires",
                new Object[]{exchangeSpecification.getApiKey(), nonce, digestString});

        //sendMessage( );

        return objectMapper.writeValueAsString(subscribeMessage);

        //streamingService.sendAuthKeyExpires(new Object[]{apiKey, nonce, digestString});
        //Thread.sleep(1500);
    }
}
