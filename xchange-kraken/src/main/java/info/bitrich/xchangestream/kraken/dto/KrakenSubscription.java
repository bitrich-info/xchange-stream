package info.bitrich.xchangestream.kraken.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KrakenSubscription {

    @JsonProperty("name")
    private String name;

    @JsonProperty("interval")
    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int interval;

    @JsonProperty("depth")
    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int depth;

    public KrakenSubscription(String name, int interval, int depth) {
        this.name = name;
        this.interval = interval;
        this.depth = depth;
    }

    public KrakenSubscription(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getInterval() {
        return interval;
    }

    public int getDepth() {
        return depth;
    }
}
