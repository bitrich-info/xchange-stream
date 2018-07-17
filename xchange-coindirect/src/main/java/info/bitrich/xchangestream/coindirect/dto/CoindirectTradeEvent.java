package info.bitrich.xchangestream.coindirect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class CoindirectTradeEvent {
    public final String market;
    public final BigDecimal amount;
    public final BigDecimal price;
    public final long timestamp;

    public CoindirectTradeEvent(@JsonProperty("market") String market,
                                @JsonProperty("amount") BigDecimal amount,
                                @JsonProperty("price") BigDecimal price,
                                @JsonProperty("timestamp") long timestamp) {
        this.market = market;
        this.amount = amount;
        this.price = price;
        this.timestamp = timestamp;
    }
}
