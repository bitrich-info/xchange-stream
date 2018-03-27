package info.bitrich.xchangestream.cexio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.cexio.dto.*;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import org.knowm.xchange.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

public class CexioStreamingOrderDataService extends JsonNettyStreamingService {

    private static final Logger LOG = LoggerFactory.getLogger(CexioStreamingOrderDataService.class);

    public static final String CONNECTED = "connected";
    public static final String AUTH = "auth";
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final String ORDER = "order";

    private String apiKey;
    private String apiSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private PublishSubject<Order> subject = PublishSubject.create();

    public CexioStreamingOrderDataService(String apiUrl) {
        super(apiUrl, Integer.MAX_VALUE);
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        return null;
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        return null;
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        return null;
    }

    @Override
    public void messageHandler(String message) {
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(message);
        } catch (IOException e) {
            LOG.error("Error parsing incoming message to JSON: {}", message);
            subject.onError(e);
            return;
        }
        handleMessage(jsonNode);
    }

    @Override
    protected void handleMessage(JsonNode message) {
        LOG.debug("Receiving message: {}", message);
        JsonNode cexioMessage = message.get("e");

        if (cexioMessage != null) {
            switch (cexioMessage.textValue()) {
                case CONNECTED:
                    auth();
                    break;
                case AUTH:
                    CexioWebSocketAuthResponse response = deserialize(message, CexioWebSocketAuthResponse.class);
                    if (response != null && !response.isSuccess()) {
                        LOG.error("Authentication error: {}", response.getData().getError());
                    }
                    break;
                case PING:
                    pong();
                    break;
                case ORDER:
                    CexioWebSocketOrderMessage order = deserialize(message, CexioWebSocketOrderMessage.class);
                    if (order != null) {
                        if (order.getData().isCancel()) {
                            LOG.debug(String.format("Order is cancelled: %s", order.getData()));
                        } else if (order.getData().getRemains().compareTo(BigDecimal.ZERO) == 0) {
                            LOG.debug(String.format("Order is fully filled: %s", order.getData()));
                        } else {
                            LOG.debug(String.format("Order is partially filled: %s", order.getData()));
                        }

                        subject.onNext(CexioAdapters.adaptOrder(order.getData()));
                    }
                    break;
            }
        }
    }

    private void auth() {
        long timestamp = System.currentTimeMillis() / 1000;
        CexioDigest cexioDigest = CexioDigest.createInstance(apiSecret);
        String signature = cexioDigest.createSignature(timestamp, apiKey);
        CexioWebSocketAuthMessage message = new CexioWebSocketAuthMessage(
                new CexioWebSocketAuth(apiKey, signature, timestamp));
        sendMessage(message);
    }

    private void pong() {
        CexioWebSocketPongMessage message = new CexioWebSocketPongMessage();
        sendMessage(message);
    }

    private void sendMessage(Object message) {
        try {
            sendMessage(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            LOG.error("Error creating json message: {}", e.getMessage());
        }
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    private <T> T deserialize(JsonNode message, Class<T> valueType) {
        T result = null;
        try {
            result = objectMapper.treeToValue(message, valueType);
        } catch (JsonProcessingException e) {
            LOG.error("Json parsing error: {}", e.getMessage());
        }
        return result;
    }

    public Observable<Order> getOrderExecution() {
        return subject.share();
    }
}
