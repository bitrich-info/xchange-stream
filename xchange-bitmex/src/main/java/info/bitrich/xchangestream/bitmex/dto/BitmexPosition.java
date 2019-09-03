package info.bitrich.xchangestream.bitmex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class BitmexPosition {

    @JsonProperty("account")
    private final long account;

    @JsonProperty("symbol")
    private final String symbol;

    @JsonProperty("currency")
    private final String currency;

    @JsonProperty("currentTimestamp")
    private final Date currentTimestamp;

    @JsonProperty("currentQty")
    private final long currentQty;

    @JsonProperty("markPrice")
    private final double markPrice;

    @JsonProperty("markValue")
    private final double markValue;

    @JsonProperty("riskValue")
    private final double riskValue;

    @JsonProperty("homeNotional")
    private final double homeNotional;

    @JsonProperty("posState")
    private final String posState;

    @JsonProperty("maintMargin")
    private final double maintMargin;

    @JsonProperty("unrealisedGrossPnl")
    private final long unrealisedGrossPnl;

    @JsonProperty("unrealisedPnl")
    private final long unrealisedPnl;

    @JsonProperty("unrealisedPnlPcnt")
    private final double unrealisedPnlPcnt;

    @JsonProperty("unrealisedRoePcnt")
    private final double unrealisedRoePcnt;

    @JsonProperty("simpleQty")
    private final double simpleQty;

    @JsonProperty("liquidationPrice")
    private final double liquidationPrice;

    @JsonProperty("timestamp")
    private final Date timestamp;

    @JsonCreator
    public BitmexPosition(
            @JsonProperty("account") long account,
            @JsonProperty("symbol") String symbol,
            @JsonProperty("currency") String currency,
            @JsonProperty("currentTimestamp") Date currentTimestamp,
            @JsonProperty("currentQty") long currentQty,
            @JsonProperty("markPrice") double markPrice,
            @JsonProperty("markValue") double markValue,
            @JsonProperty("riskValue") double riskValue,
            @JsonProperty("homeNotional") double homeNotional,
            @JsonProperty("posState") String posState,
            @JsonProperty("maintMargin") double maintMargin,
            @JsonProperty("unrealisedGrossPnl") long unrealisedGrossPnl,
            @JsonProperty("unrealisedPnl") long unrealisedPnl,
            @JsonProperty("unrealisedPnlPcnt") double unrealisedPnlPcnt,
            @JsonProperty("unrealisedRoePcnt") double unrealisedRoePcnt,
            @JsonProperty("simpleQty") double simpleQty,
            @JsonProperty("liquidationPrice") double liquidationPrice,
            @JsonProperty("timestamp") Date timestamp) {
        this.account = account;
        this.symbol = symbol;
        this.currency = currency;
        this.currentTimestamp = currentTimestamp;
        this.currentQty = currentQty;
        this.markPrice = markPrice;
        this.markValue = markValue;
        this.riskValue = riskValue;
        this.homeNotional = homeNotional;
        this.posState = posState;
        this.maintMargin = maintMargin;
        this.unrealisedGrossPnl = unrealisedGrossPnl;
        this.unrealisedPnl = unrealisedPnl;
        this.unrealisedPnlPcnt = unrealisedPnlPcnt;
        this.unrealisedRoePcnt = unrealisedRoePcnt;
        this.simpleQty = simpleQty;
        this.liquidationPrice = liquidationPrice;
        this.timestamp = timestamp;
    }

    public long getAccount() {
        return account;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCurrency() {
        return currency;
    }

    public Date getCurrentTimestamp() {
        return currentTimestamp;
    }

    public long getCurrentQty() {
        return currentQty;
    }

    public double getMarkPrice() {
        return markPrice;
    }

    public double getMarkValue() {
        return markValue;
    }

    public double getRiskValue() {
        return riskValue;
    }

    public double getHomeNotional() {
        return homeNotional;
    }

    public String getPosState() {
        return posState;
    }

    public double getMaintMargin() {
        return maintMargin;
    }

    public long getUnrealisedGrossPnl() {
        return unrealisedGrossPnl;
    }

    public long getUnrealisedPnl() {
        return unrealisedPnl;
    }

    public double getUnrealisedPnlPcnt() {
        return unrealisedPnlPcnt;
    }

    public double getUnrealisedRoePcnt() {
        return unrealisedRoePcnt;
    }

    public double getSimpleQty() {
        return simpleQty;
    }

    public double getLiquidationPrice() {
        return liquidationPrice;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "BitmexPosition{" +
                "account=" + account +
                ", symbol='" + symbol + '\'' +
                ", currency='" + currency + '\'' +
                ", currentTimestamp=" + currentTimestamp +
                ", currentQty=" + currentQty +
                ", markPrice=" + markPrice +
                ", markValue=" + markValue +
                ", riskValue=" + riskValue +
                ", homeNotional=" + homeNotional +
                ", posState='" + posState + '\'' +
                ", maintMargin=" + maintMargin +
                ", unrealisedGrossPnl=" + unrealisedGrossPnl +
                ", unrealisedPnl=" + unrealisedPnl +
                ", unrealisedPnlPcnt=" + unrealisedPnlPcnt +
                ", unrealisedRoePcnt=" + unrealisedRoePcnt +
                ", simpleQty=" + simpleQty +
                ", liquidationPrice=" + liquidationPrice +
                ", timestamp=" + timestamp +
                '}';
    }
}
