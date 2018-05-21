package info.bitrich.xchangestream.okcoin.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.util.Date;

public class OkCoinFuturesOrder {

    public enum OrderStatus {
        PENDING,
        PARTIAL_FILLED,
        FULL_FILLED,
        CANCELLED,
        CANCELLING;

        public static OrderStatus fromInt(int value) {
            if (value == -1) {
                return CANCELLED;
            } else {
                return OrderStatus.values()[value];
            }
        }
    }

    public enum OrderType {
        OPEN_LONG,
        OPEN_SHORT,
        SETTLE_LONG,
        SETTLE_SHORT
    }

    private BigDecimal contractId;

    private String contractName;

    private String contractType;

    private Date createDate;

    private BigDecimal amount;

    private BigDecimal dealAmount;

    private BigDecimal fee;

    private String orderId;

    private BigDecimal price;

    private BigDecimal priceAvg;

    private OrderStatus status;

    private OrderType type;

    private String symbol;

    @JsonCreator
    public OkCoinFuturesOrder(@JsonProperty("contract_id") BigDecimal contractId,
                              @JsonProperty("contract_name") String contractName,
                              @JsonProperty("contract_type") String contractType,
                              @JsonProperty("create_date") long createDate,
                              @JsonProperty("amount") BigDecimal amount,
                              @JsonProperty("deal_amount") BigDecimal dealAmount,
                              @JsonProperty("fee") BigDecimal fee,
                              @JsonProperty("order_id") String orderId,
                              @JsonProperty("price") BigDecimal price,
                              @JsonProperty("price_avg") BigDecimal priceAvg,
                              @JsonProperty("status") int status,
                              @JsonProperty("type") int type,
                              @JsonProperty("symbol") String symbol) {
        this.contractId = contractId;
        this.contractName = contractName;
        this.contractType = contractType;
        this.createDate = new Date(createDate * 1000L);
        this.amount = amount;
        this.dealAmount = dealAmount;
        this.fee = fee;
        this.orderId = orderId;
        this.price = price;
        this.priceAvg = priceAvg;
        this.status = OrderStatus.fromInt(status);
        this.type = OrderType.values()[type-1];
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getContractType() {
        return contractType;
    }

    public Order toOrder() {
        Order.OrderType otype = null;
        switch (type) {
            case OPEN_LONG:
                otype = Order.OrderType.BID;
                break;
            case OPEN_SHORT:
                otype = Order.OrderType.ASK;
                break;
            case SETTLE_LONG:
                otype = Order.OrderType.EXIT_BID;
                break;
            case SETTLE_SHORT:
                otype = Order.OrderType.EXIT_ASK;
        }
        String [] currencies = symbol.split("_");
        CurrencyPair currencyPair = new CurrencyPair(new Currency(currencies[0].toUpperCase()), new Currency(currencies[1].toUpperCase()));
        LimitOrder.Builder builder = new LimitOrder.Builder(otype, currencyPair);
        builder.limitPrice(price);
        builder.averagePrice(priceAvg);
        builder.originalAmount(amount);
        builder.cumulativeAmount(dealAmount);
        switch (status) {
            case PENDING:
                builder.orderStatus(Order.OrderStatus.PENDING_NEW);
                break;
            case PARTIAL_FILLED:
                builder.orderStatus(Order.OrderStatus.PARTIALLY_FILLED);
                break;
            case FULL_FILLED:
                builder.orderStatus(Order.OrderStatus.FILLED);
                break;
            case CANCELLED:
                builder.orderStatus(Order.OrderStatus.CANCELED);
                break;
            case CANCELLING:
                builder.orderStatus(Order.OrderStatus.PENDING_CANCEL);
                break;
            default:
                builder.orderStatus(Order.OrderStatus.UNKNOWN);
                break;
        }
        return builder.build();
    }
}
