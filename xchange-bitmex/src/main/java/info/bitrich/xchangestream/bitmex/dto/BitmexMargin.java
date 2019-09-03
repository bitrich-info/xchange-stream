package info.bitrich.xchangestream.bitmex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class BitmexMargin {

    @JsonProperty("account")
    private final long account;

    @JsonProperty("currency")
    private final String currency;

    @JsonProperty("grossMarkValue")
    private final long grossMarkValue;

    @JsonProperty("riskValue")
    private final long riskValue;

    @JsonProperty("maintMargin")
    private final long maintMargin;

    @JsonProperty("unrealisedPnl")
    private final long unrealisedPnl;

    @JsonProperty("marginBalance")
    private final long marginBalance;

    @JsonProperty("marginBalancePcnt")
    private final double marginBalancePcnt;

    @JsonProperty("marginLeverage")
    private final double marginLeverage;

    @JsonProperty("marginUsedPcnt")
    private final double marginUsedPcnt;

    @JsonProperty("timestamp")
    private final Date timestamp;

    @JsonProperty("grossLastValue")
    private final long grossLastValue;

    @JsonProperty("walletBalance")
    private final long walletBalance;

    public BitmexMargin(
            @JsonProperty("account") long account,
            @JsonProperty("currency") String currency,
            @JsonProperty("grossMarkValue") long grossMarkValue,
            @JsonProperty("riskValue") long riskValue,
            @JsonProperty("maintMargin") long maintMargin,
            @JsonProperty("unrealisedPnl") long unrealisedPnl,
            @JsonProperty("marginBalance") long marginBalance,
            @JsonProperty("marginBalancePcnt") double marginBalancePcnt,
            @JsonProperty("marginLeverage") double marginLeverage,
            @JsonProperty("marginUsedPcnt") double marginUsedPcnt,
            @JsonProperty("timestamp") Date timestamp,
            @JsonProperty("grossLastValue") long grossLastValue,
            @JsonProperty("walletBalance") long walletBalance) {
        this.account = account;
        this.currency = currency;
        this.grossMarkValue = grossMarkValue;
        this.riskValue = riskValue;
        this.maintMargin = maintMargin;
        this.unrealisedPnl = unrealisedPnl;
        this.marginBalance = marginBalance;
        this.marginBalancePcnt = marginBalancePcnt;
        this.marginLeverage = marginLeverage;
        this.marginUsedPcnt = marginUsedPcnt;
        this.timestamp = timestamp;
        this.grossLastValue = grossLastValue;
        this.walletBalance = walletBalance;
    }

    public long getAccount() {
        return account;
    }

    public String getCurrency() {
        return currency;
    }

    public long getGrossMarkValue() {
        return grossMarkValue;
    }

    public long getRiskValue() {
        return riskValue;
    }

    public long getMaintMargin() {
        return maintMargin;
    }

    public long getUnrealisedPnl() {
        return unrealisedPnl;
    }

    public long getMarginBalance() {
        return marginBalance;
    }

    public double getMarginBalancePcnt() {
        return marginBalancePcnt;
    }

    public double getMarginLeverage() {
        return marginLeverage;
    }

    public double getMarginUsedPcnt() {
        return marginUsedPcnt;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public long getGrossLastValue() {
        return grossLastValue;
    }

    public long getWalletBalance() {
        return walletBalance;
    }

    @Override
    public String toString() {
        return "BitmexMargin{" +
                "account=" + account +
                ", currency='" + currency + '\'' +
                ", grossMarkValue=" + grossMarkValue +
                ", riskValue=" + riskValue +
                ", maintMargin=" + maintMargin +
                ", unrealisedPnl=" + unrealisedPnl +
                ", marginBalance=" + marginBalance +
                ", marginBalancePcnt=" + marginBalancePcnt +
                ", marginLeverage=" + marginLeverage +
                ", marginUsedPcnt=" + marginUsedPcnt +
                ", timestamp=" + timestamp +
                ", grossLastValue=" + grossLastValue +
                ", walletBalance=" + walletBalance +
                '}';
    }
}
