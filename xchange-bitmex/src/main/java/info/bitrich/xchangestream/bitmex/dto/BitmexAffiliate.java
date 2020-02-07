package info.bitrich.xchangestream.bitmex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BitmexAffiliate {
    private long account;
    private String currency;
    private BigDecimal prevPayout;
    private BigDecimal prevTurnover;
    private BigDecimal prevComm;
    private String prevTimestamp;
    private BigDecimal execTurnover;
    private BigDecimal execComm;
    private BigDecimal totalReferrals;
    private BigDecimal totalTurnover;
    private BigDecimal totalComm;
    private BigDecimal payoutPcnt;
    private BigDecimal pendingPayout;
    private String timestamp;
    private BigDecimal referrerAccount;
    private BigDecimal referralDiscount;
    private BigDecimal affiliatePayout;

    @JsonCreator
    public BitmexAffiliate(@JsonProperty("account") long account,
                           @JsonProperty("currency") String currency,
                           @JsonProperty("prevPayout") BigDecimal prevPayout,
                           @JsonProperty("prevTurnover") BigDecimal prevTurnover,
                           @JsonProperty("prevComm") BigDecimal prevComm,
                           @JsonProperty("prevTimestamp") String prevTimestamp,
                           @JsonProperty("execTurnover") BigDecimal execTurnover,
                           @JsonProperty("execComm") BigDecimal execComm,
                           @JsonProperty("totalReferrals") BigDecimal totalReferrals,
                           @JsonProperty("totalTurnover") BigDecimal totalTurnover,
                           @JsonProperty("totalComm") BigDecimal totalComm,
                           @JsonProperty("payoutPcnt") BigDecimal payoutPcnt,
                           @JsonProperty("pendingPayout") BigDecimal pendingPayout,
                           @JsonProperty("timestamp") String timestamp,
                           @JsonProperty("referrerAccount") BigDecimal referrerAccount,
                           @JsonProperty("referralDiscount") BigDecimal referralDiscount,
                           @JsonProperty("affiliatePayout") BigDecimal affiliatePayout) {
        this.account = account;
        this.currency = currency;
        this.prevPayout = prevPayout;
        this.prevTurnover = prevTurnover;
        this.prevComm = prevComm;
        this.prevTimestamp = prevTimestamp;
        this.execTurnover = execTurnover;
        this.execComm = execComm;
        this.totalReferrals = totalReferrals;
        this.totalTurnover = totalTurnover;
        this.totalComm = totalComm;
        this.payoutPcnt = payoutPcnt;
        this.pendingPayout = pendingPayout;
        this.timestamp = timestamp;
        this.referrerAccount = referrerAccount;
        this.referralDiscount = referralDiscount;
        this.affiliatePayout = affiliatePayout;
    }

    public long getAccount() {
        return account;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getPrevPayout() {
        return prevPayout;
    }

    public BigDecimal getPrevTurnover() {
        return prevTurnover;
    }

    public BigDecimal getPrevComm() {
        return prevComm;
    }

    public String getPrevTimestamp() {
        return prevTimestamp;
    }

    public BigDecimal getExecTurnover() {
        return execTurnover;
    }

    public BigDecimal getExecComm() {
        return execComm;
    }

    public BigDecimal getTotalReferrals() {
        return totalReferrals;
    }

    public BigDecimal getTotalTurnover() {
        return totalTurnover;
    }

    public BigDecimal getTotalComm() {
        return totalComm;
    }

    public BigDecimal getPayoutPcnt() {
        return payoutPcnt;
    }

    public BigDecimal getPendingPayout() {
        return pendingPayout;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public BigDecimal getReferrerAccount() {
        return referrerAccount;
    }

    public BigDecimal getReferralDiscount() {
        return referralDiscount;
    }

    public BigDecimal getAffiliatePayout() {
        return affiliatePayout;
    }
}
