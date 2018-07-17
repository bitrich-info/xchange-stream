package info.bitrich.xchangestream.coindirect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoindirectAccountOrderEvent extends CoindirectEvent {
    public final CoindirectAccountOrder data;
    public CoindirectAccountOrderEvent(@JsonProperty("event") String event,
                                       @JsonProperty("data") CoindirectAccountOrder data) {
        super(event);
        this.data = data;
    }
}
