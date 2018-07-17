package info.bitrich.xchangestream.service.centrifugo;

import java.net.MalformedURLException;
import java.net.URL;

public class CentrifugoToken {
    public String user;
    public String timestamp;
    public String token;
    public String client;
    public String url;

    public CentrifugoToken(String user,
                               String timestamp,
                               String token) {

        this.user = user;
        this.timestamp = timestamp;
        this.token = token;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getWsUrl() {
        String result = this.url;
        try {
            URL parsedUrl = new URL(url);
            if(parsedUrl.getProtocol().equals("https")) {
                StringBuilder wssUrl = new StringBuilder();
                wssUrl.append("wss");
                wssUrl.append("://");
                wssUrl.append(parsedUrl.getHost());
                wssUrl.append("/connection/websocket");
                result = wssUrl.toString();
            }
        } catch(MalformedURLException ignored) {
        }

        return result;
    }


}
