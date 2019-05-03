package info.bitrich.xchangestream.kraken.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

public class KrakenStreamingOrder implements Serializable {

    @JsonProperty
    private final BigDecimal price;

    @JsonProperty
    private final BigDecimal volume;

    @JsonProperty
    private final String timestamp;

    @JsonCreator
    public KrakenStreamingOrder(ArrayList list) {
        this.price = BigDecimal.valueOf(Double.valueOf((String)list.get(0)));
        this.volume = BigDecimal.valueOf(Double.valueOf((String)list.get(1)));
        this.timestamp = (String)list.get(2);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "KrakenStreamingOrder{" +
                "price=" + price +
                ", volume=" + volume +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
