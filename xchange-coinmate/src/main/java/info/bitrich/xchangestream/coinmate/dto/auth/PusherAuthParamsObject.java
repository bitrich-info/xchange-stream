package info.bitrich.xchangestream.coinmate.dto.auth;

import org.knowm.xchange.utils.nonce.AtomicLongCurrentTimeIncrementalNonceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.SynchronizedValueFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class PusherAuthParamsObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(PusherAuthParamsObject.class);
    private Map<String, String> params = new HashMap<>();

    private final String secret;
    private final String apiKey;
    private final String userId;
    private SynchronizedValueFactory<Long> nonce;

    public PusherAuthParamsObject(String secret, String apiKey, String userId,SynchronizedValueFactory<Long> nonce) {
        this.secret = secret;
        this.apiKey = apiKey;
        this.userId = userId;
        this.nonce = nonce;
    }

    public Map<String, String> getParams() {
        params = new HashMap<>();
        Long nonce1 = nonce.createValue();
        this.params.put("clientId", userId);
        this.params.put("nonce", String.valueOf(nonce1));
        this.params.put("signature", signature(nonce1, userId, apiKey, secret));
        this.params.put("publicKey", apiKey);

        return params;
    }

    private String signature(Long nonce, String userId, String apiKey, String apiSecret) {
        try {
            Mac mac256 = Mac.getInstance("HmacSHA256");
            SecretKey secretKey = new SecretKeySpec(apiSecret.getBytes("UTF-8"), "HmacSHA256");
            mac256.init(secretKey);
            mac256.update(String.valueOf(nonce).getBytes());
            mac256.update(userId.getBytes());
            mac256.update(apiKey.getBytes());
            return String.format("%064x", new BigInteger(1, mac256.doFinal())).toUpperCase();
        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public String toString() {
        return "PusherAuthParamsObject{" +
                "params=" + params +
                '}';
    }
}

