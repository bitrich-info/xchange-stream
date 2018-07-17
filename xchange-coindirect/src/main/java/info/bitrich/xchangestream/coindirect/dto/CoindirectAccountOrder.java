package info.bitrich.xchangestream.coindirect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.coindirect.dto.trade.CoindirectOrder;

import java.math.BigDecimal;

public class CoindirectAccountOrder {
    public final long id;
    public final String uuid;
    public final CoindirectOrder.Status status;
    public final BigDecimal amount;
    public final BigDecimal executedAmount;
    public final BigDecimal price;
    public final BigDecimal executedPrice;
    public final BigDecimal executedFees;
    public final CoindirectOrder.Side side;
    public final long timestamp;
    public final String symbol;

    public CoindirectAccountOrder(@JsonProperty("id") long id,
                                       @JsonProperty("uuid") String uuid,
                                       @JsonProperty("status") CoindirectOrder.Status status,
                                       @JsonProperty("amount") BigDecimal amount,
                                       @JsonProperty("executedAmount") BigDecimal executedAmount,
                                       @JsonProperty("price") BigDecimal price,
                                       @JsonProperty("executedPrice") BigDecimal executedPrice,
                                       @JsonProperty("executedFees") BigDecimal executedFees,
                                       @JsonProperty("side") CoindirectOrder.Side side,
                                       @JsonProperty("timestamp") long timestamp,
                                       @JsonProperty("symbol") String symbol) {
        this.id = id;
        this.uuid = uuid;
        this.status = status;
        this.amount = amount;
        this.executedAmount = executedAmount;
        this.price = price;
        this.executedPrice = executedPrice;
        this.executedFees = executedFees;
        this.side = side;
        this.timestamp = timestamp;
        this.symbol = symbol;
    }
}
