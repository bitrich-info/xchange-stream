package info.bitrich.xchangestream.coinmate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.coinmate.dto.marketdata.CoinmateTransactionsEntry;
import org.knowm.xchange.dto.Order;

public class CoinmateWebSocketUserTrade {

    @JsonProperty("transactionID")
    private final String transactionId;

    @JsonProperty("date")
    private final long timestamp;

    @JsonProperty("amount")
    private final double amount;

    @JsonProperty("price")
    private final double price;

    @JsonProperty("buyOrderId")
    private final String buyOrderId;

    @JsonProperty("sellOrderId")
    private final String sellOrderId;

    @JsonProperty("orderType")
    private final Order.OrderType userOrderType;

    @JsonProperty("type")
    private final Order.OrderType takerOrderType;

    @JsonProperty("fee")
    private final double fee;

    @JsonProperty("tradeFeeType")
    private final String userFeeType;

    public CoinmateWebSocketUserTrade(
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("date") long timestamp,
            @JsonProperty("price") double price,
            @JsonProperty("amount") double amount,
            @JsonProperty("buyOrderId") String buyOrderId,
            @JsonProperty("sellOrderId") String sellOrderId,
            @JsonProperty("orderType") Order.OrderType userOrderType,
            @JsonProperty("type") Order.OrderType takerOrderType,
            @JsonProperty("fee") double fee,
            @JsonProperty("tradeFeeType") String userFeeType) {
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.amount = amount;
        this.price = price;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.userOrderType = userOrderType;
        this.takerOrderType = takerOrderType;
        this.fee = fee;
        this.userFeeType = userFeeType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public Order.OrderType getUserOrderType() {
        return userOrderType;
    }

    public Order.OrderType getTakerOrderType() {
        return takerOrderType;
    }

    public double getFee() {
        return fee;
    }

    public String getUserFeeType() {
        return userFeeType;
    }

    @Override
    public String toString() {
        return "CoinmateWebSocketUserTrade{" +
                "transactionId='" + transactionId + '\'' +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                ", price=" + price +
                ", buyOrderId='" + buyOrderId + '\'' +
                ", sellOrderId='" + sellOrderId + '\'' +
                ", userOrderType=" + userOrderType +
                ", takerOrderType=" + takerOrderType +
                ", fee=" + fee +
                ", userFeeType='" + userFeeType + '\'' +
                '}';
    }
}
