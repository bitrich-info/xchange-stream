package info.bitrich.xchangestream.coindirect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.coindirect.dto.trade.CoindirectOrder;

import java.math.BigDecimal;

public class CoindirectOrderBookEvent {
    public final String market;
    public final long sequence;
    public final CoindirectOrder.Side side;
    public final BigDecimal size;
    public final BigDecimal price;


    public CoindirectOrderBookEvent(@JsonProperty("market") String market,
                                    @JsonProperty("sequence") long sequence,
                                    @JsonProperty("side") CoindirectOrder.Side side,
                                    @JsonProperty("size") BigDecimal size,
                                    @JsonProperty("price") BigDecimal price) {
        this.market = market;
        this.sequence = sequence;
        this.side = side;
        this.size = size;
        this.price = price;
    }

    @Override
    public String toString() {
        return "CoindirectOrderBookEvent{" +
                "market='" + market + '\'' +
                ", sequence=" + sequence +
                ", side=" + side +
                ", size=" + size +
                ", price=" + price +
                '}';
    }
}
