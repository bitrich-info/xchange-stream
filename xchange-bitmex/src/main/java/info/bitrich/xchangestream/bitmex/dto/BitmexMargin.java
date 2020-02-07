package info.bitrich.xchangestream.bitmex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

public class BitmexMargin {
    private long account;
    private String currency;
    private BigDecimal riskLimit;
    private String prevState;
    private String state;
    private String action;
    private BigDecimal amount;
    private BigDecimal pendingCredit;
    private BigDecimal pendingDebit;
    private BigDecimal confirmedDebit;
    private BigDecimal prevRealisedPnl;
    private BigDecimal prevUnrealisedPnl;
    private BigDecimal grossComm;
    private BigDecimal grossOpenCost;
    private BigDecimal grossOpenPremium;
    private BigDecimal grossExecCost;
    private BigDecimal grossMarkValue;
    private BigDecimal riskValue;
    private BigDecimal taxableMargin;
    private BigDecimal initMargin;
    private BigDecimal maintMargin;
    private BigDecimal sessionMargin;
    private BigDecimal targetExcessMargin;
    private BigDecimal varMargin;
    private BigDecimal realisedPnl;
    private BigDecimal unrealisedPnl;
    private BigDecimal indicativeTax;
    private BigDecimal unrealisedProfit;
    private BigDecimal syntheticMargin;
    private BigDecimal walletBalance;
    private BigDecimal marginBalance;
    private BigDecimal marginBalancePcnt;
    private BigDecimal marginLeverage;
    private BigDecimal marginUsedPcnt;
    private BigDecimal excessMargin;
    private BigDecimal excessMarginPcnt;
    private BigDecimal availableMargin;
    private BigDecimal withdrawableMargin;
    private Date timestamp;
    private BigDecimal commission;

    @JsonCreator
    public BitmexMargin(@JsonProperty("account") long account,
                        @JsonProperty("currency") String currency,
                        @JsonProperty("riskLimit") BigDecimal riskLimit,
                        @JsonProperty("prevState") String prevState,
                        @JsonProperty("state") String state,
                        @JsonProperty("action") String action,
                        @JsonProperty("amount") BigDecimal amount,
                        @JsonProperty("pendingCredit") BigDecimal pendingCredit,
                        @JsonProperty("pendingDebit") BigDecimal pendingDebit,
                        @JsonProperty("confirmedDebit") BigDecimal confirmedDebit,
                        @JsonProperty("prevRealisedPnl") BigDecimal prevRealisedPnl,
                        @JsonProperty("prevUnrealisedPnl") BigDecimal prevUnrealisedPnl,
                        @JsonProperty("grossComm") BigDecimal grossComm,
                        @JsonProperty("grossOpenCost") BigDecimal grossOpenCost,
                        @JsonProperty("grossOpenPremium") BigDecimal grossOpenPremium,
                        @JsonProperty("grossExecCost") BigDecimal grossExecCost,
                        @JsonProperty("grossMarkValue") BigDecimal grossMarkValue,
                        @JsonProperty("riskValue") BigDecimal riskValue,
                        @JsonProperty("taxableMargin") BigDecimal taxableMargin,
                        @JsonProperty("initMargin") BigDecimal initMargin,
                        @JsonProperty("maintMargin") BigDecimal maintMargin,
                        @JsonProperty("sessionMargin") BigDecimal sessionMargin,
                        @JsonProperty("targetExcessMargin") BigDecimal targetExcessMargin,
                        @JsonProperty("varMargin") BigDecimal varMargin,
                        @JsonProperty("realisedPnl") BigDecimal realisedPnl,
                        @JsonProperty("unrealisedPnl") BigDecimal unrealisedPnl,
                        @JsonProperty("indicativeTax") BigDecimal indicativeTax,
                        @JsonProperty("unrealisedProfit") BigDecimal unrealisedProfit,
                        @JsonProperty("syntheticMargin") BigDecimal syntheticMargin,
                        @JsonProperty("walletBalance") BigDecimal walletBalance,
                        @JsonProperty("marginBalance") BigDecimal marginBalance,
                        @JsonProperty("marginBalancePcnt") BigDecimal marginBalancePcnt,
                        @JsonProperty("marginLeverage") BigDecimal marginLeverage,
                        @JsonProperty("marginUsedPcnt") BigDecimal marginUsedPcnt,
                        @JsonProperty("excessMargin") BigDecimal excessMargin,
                        @JsonProperty("excessMarginPcnt") BigDecimal excessMarginPcnt,
                        @JsonProperty("availableMargin") BigDecimal availableMargin,
                        @JsonProperty("withdrawableMargin") BigDecimal withdrawableMargin,
                        @JsonProperty("timestamp") Date timestamp,
                        @JsonProperty("commission") BigDecimal commission) {
        this.account = account;
        this.currency = currency;
        this.riskLimit = riskLimit;
        this.prevState = prevState;
        this.state = state;
        this.action = action;
        this.amount = amount;
        this.pendingCredit = pendingCredit;
        this.pendingDebit = pendingDebit;
        this.confirmedDebit = confirmedDebit;
        this.prevRealisedPnl = prevRealisedPnl;
        this.prevUnrealisedPnl = prevUnrealisedPnl;
        this.grossComm = grossComm;
        this.grossOpenCost = grossOpenCost;
        this.grossOpenPremium = grossOpenPremium;
        this.grossExecCost = grossExecCost;
        this.grossMarkValue = grossMarkValue;
        this.riskValue = riskValue;
        this.taxableMargin = taxableMargin;
        this.initMargin = initMargin;
        this.maintMargin = maintMargin;
        this.sessionMargin = sessionMargin;
        this.targetExcessMargin = targetExcessMargin;
        this.varMargin = varMargin;
        this.realisedPnl = realisedPnl;
        this.unrealisedPnl = unrealisedPnl;
        this.indicativeTax = indicativeTax;
        this.unrealisedProfit = unrealisedProfit;
        this.syntheticMargin = syntheticMargin;
        this.walletBalance = walletBalance;
        this.marginBalance = marginBalance;
        this.marginBalancePcnt = marginBalancePcnt;
        this.marginLeverage = marginLeverage;
        this.marginUsedPcnt = marginUsedPcnt;
        this.excessMargin = excessMargin;
        this.excessMarginPcnt = excessMarginPcnt;
        this.availableMargin = availableMargin;
        this.withdrawableMargin = withdrawableMargin;
        this.timestamp = timestamp;
        this.commission = commission;
    }

    public BigDecimal getTotal() {
        return availableMargin.add(initMargin);
    }

    public long getAccount() {
        return account;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getRiskLimit() {
        return riskLimit;
    }

    public String getPrevState() {
        return prevState;
    }

    public String getState() {
        return state;
    }

    public String getAction() {
        return action;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPendingCredit() {
        return pendingCredit;
    }

    public BigDecimal getPendingDebit() {
        return pendingDebit;
    }

    public BigDecimal getConfirmedDebit() {
        return confirmedDebit;
    }

    public BigDecimal getPrevRealisedPnl() {
        return prevRealisedPnl;
    }

    public BigDecimal getPrevUnrealisedPnl() {
        return prevUnrealisedPnl;
    }

    public BigDecimal getGrossComm() {
        return grossComm;
    }

    public BigDecimal getGrossOpenCost() {
        return grossOpenCost;
    }

    public BigDecimal getGrossOpenPremium() {
        return grossOpenPremium;
    }

    public BigDecimal getGrossExecCost() {
        return grossExecCost;
    }

    public BigDecimal getGrossMarkValue() {
        return grossMarkValue;
    }

    public BigDecimal getRiskValue() {
        return riskValue;
    }

    public BigDecimal getTaxableMargin() {
        return taxableMargin;
    }

    public BigDecimal getInitMargin() {
        return initMargin;
    }

    public BigDecimal getMaintMargin() {
        return maintMargin;
    }

    public BigDecimal getSessionMargin() {
        return sessionMargin;
    }

    public BigDecimal getTargetExcessMargin() {
        return targetExcessMargin;
    }

    public BigDecimal getVarMargin() {
        return varMargin;
    }

    public BigDecimal getRealisedPnl() {
        return realisedPnl;
    }

    public BigDecimal getUnrealisedPnl() {
        return unrealisedPnl;
    }

    public BigDecimal getIndicativeTax() {
        return indicativeTax;
    }

    public BigDecimal getUnrealisedProfit() {
        return unrealisedProfit;
    }

    public BigDecimal getSyntheticMargin() {
        return syntheticMargin;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public BigDecimal getMarginBalance() {
        return marginBalance;
    }

    public BigDecimal getMarginBalancePcnt() {
        return marginBalancePcnt;
    }

    public BigDecimal getMarginLeverage() {
        return marginLeverage;
    }

    public BigDecimal getMarginUsedPcnt() {
        return marginUsedPcnt;
    }

    public BigDecimal getExcessMargin() {
        return excessMargin;
    }

    public BigDecimal getExcessMarginPcnt() {
        return excessMarginPcnt;
    }

    public BigDecimal getAvailableMargin() {
        return availableMargin;
    }

    public BigDecimal getWithdrawableMargin() {
        return withdrawableMargin;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public BigDecimal getCommission() {
        return commission;
    }
}
