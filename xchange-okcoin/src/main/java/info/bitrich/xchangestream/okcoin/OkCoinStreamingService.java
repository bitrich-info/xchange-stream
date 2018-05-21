package info.bitrich.xchangestream.okcoin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Collections2;
import info.bitrich.xchangestream.okcoin.dto.WebSocketMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.exceptions.ExchangeException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OkCoinStreamingService extends JsonNettyStreamingService {

    protected ExchangeSpecification exchangeSpecification;

    public OkCoinStreamingService(String apiUrl) {
        super(apiUrl);
    }

    public void setExchangeSpecification(ExchangeSpecification exchangeSpecification) {
        this.exchangeSpecification = exchangeSpecification;
    }

    private void login() throws IOException {
        String apiKey = this.exchangeSpecification.getApiKey();
        String apiSecret = this.exchangeSpecification.getSecretKey();
        Map<String, String> keyAndSecret = new HashMap<>();
        keyAndSecret.put("api_key", apiKey);
        keyAndSecret.put("secret_key", apiSecret);
        Collection<NameValuePair> formData = Collections2.transform(keyAndSecret.entrySet(),
                e -> new BasicNameValuePair(e.getKey(), e.getValue()));
        String data = URLEncodedUtils.format(formData, Charset.forName("UTF-8"));
        String sign = OkCoinAuthenticator.getMD5String(data);

        Map<String, String> params = new HashMap<>();
        params.put("api_key", apiKey);
        params.put("sign", sign);
        Map<String, Object> cmd = new HashMap<>();
        cmd.put("event", "login");
        cmd.put("parameters", params);

        ObjectMapper objectMapper = new ObjectMapper();
        this.sendMessage(objectMapper.writeValueAsString(cmd));
    }

    @Override
    public Completable connect() {
        // Note that we must override connect method in streaming service instead of streaming exchange, because of the auto reconnect feature of NettyStreamingService.
        // We must ensure the authentication message is also resend when the connection is rebuilt.
        Completable conn = super.connect();
        if (this.exchangeSpecification.getApiKey() == null) {
            return conn;
        }
        return conn.andThen((CompletableSource)(completable) -> {
            try {
                login();
                completable.onComplete();
            } catch (IOException e) {
                completable.onError(e);
            }
        });
    }

    @Override
    protected String getChannelNameFromMessage(JsonNode message) throws IOException {
        return message.get("channel").asText();
    }

    @Override
    public String getSubscribeMessage(String channelName, Object... args) throws IOException {
        WebSocketMessage webSocketMessage = new WebSocketMessage("addChannel", channelName);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(webSocketMessage);
    }

    @Override
    public String getUnsubscribeMessage(String channelName) throws IOException {
        WebSocketMessage webSocketMessage = new WebSocketMessage("removeChannel", channelName);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(webSocketMessage);
    }

    @Override
    protected void handleMessage(JsonNode message) {
        if (message.get("data") != null) {
            if (message.get("data").has("result")) {
                boolean success = message.get("data").get("result").asBoolean();
                if (!success) {
                    super.handleError(message, new ExchangeException("Error code: " + message.get("errorcode").asText()));
                }
                return;
            }
        }
        super.handleMessage(message);
    }
}
