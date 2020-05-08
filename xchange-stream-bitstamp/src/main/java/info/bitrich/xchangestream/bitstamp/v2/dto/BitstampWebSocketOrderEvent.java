package info.bitrich.xchangestream.bitstamp.v2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BitstampWebSocketOrderEvent {

  private static final String DATA = "data";
  private static final String EVENT = "event";
  private static final String CHANNEL = "channel";

  private final BitstampWebSocketOrderData data;
  private final String event;
  private final String channel;

  @JsonCreator
  public BitstampWebSocketOrderEvent(
          @JsonProperty(DATA) BitstampWebSocketOrderData data,
          @JsonProperty(EVENT) String event,
          @JsonProperty(CHANNEL) String channel) {
    this.data = data;
    this.event = event;
    this.channel = channel;
  }

  public BitstampWebSocketOrderData getData() {
    return data;
  }

  public String getEvent() {
    return event;
  }

  public String getChannel() {
    return channel;
  }

  @Override
  public String toString() {
    return "{\"data\":" + data + ",\"event\":\"" + event + "\",\"channel\":\"" + channel + "\"}";
  }

}
