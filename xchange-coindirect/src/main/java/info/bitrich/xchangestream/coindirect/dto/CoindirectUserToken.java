package info.bitrich.xchangestream.coindirect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.MalformedURLException;
import java.net.URL;

public class CoindirectUserToken {
    public final String url;
    public final String user;
    public final String timestamp;
    public final String token;

    public CoindirectUserToken(@JsonProperty("url") String url,
                               @JsonProperty("user") String user,
                               @JsonProperty("timestamp") String timestamp,
                               @JsonProperty("token") String token) {

        this.url = url;
        this.user = user;
        this.timestamp = timestamp;
        this.token = token;
    }

    @Override
    public String toString() {
        return "CoindirectUserToken{" +
                "url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
