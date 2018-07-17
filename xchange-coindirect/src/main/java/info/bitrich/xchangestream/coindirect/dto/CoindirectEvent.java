package info.bitrich.xchangestream.coindirect.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoindirectEvent {
    public final String event;

    public CoindirectEvent(@JsonProperty("event") String event) {
        this.event = event;
    }

}
