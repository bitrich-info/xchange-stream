package info.bitrich.xchangestream.kraken.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KrakenEvent {

    @JsonProperty("event")
    private String event;

    @JsonProperty("reqId")
    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reqId;

    @JsonProperty("pair")
    private List<String> pairs;

    @JsonProperty("subscription")
    private KrakenSubscription subscription;

    public KrakenEvent(String event, String reqId, List<String> pairs, KrakenSubscription subscription) {
        this.event = event;
        this.reqId = reqId;
        this.pairs = pairs;
        this.subscription = subscription;
    }

    public String getEvent() {
        return event;
    }

    public String getReqId() {
        return reqId;
    }

    public List<String> getPairs() {
        return pairs;
    }

    public KrakenSubscription getSubscription() {
        return subscription;
    }

    @Override
    public String toString() {
        return "KrakenEvent{" +
                "event='" + event + '\'' +
                ", reqId='" + reqId + '\'' +
                ", pairs=" + pairs +
                ", subscription=" + subscription +
                '}';
    }
}
