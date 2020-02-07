package info.bitrich.xchangestream.bitmex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BitmexWallet {
    private BigDecimal account;
    private String currency;
    private BigDecimal prevDeposited;
    private BigDecimal prevWithdrawn;
    private BigDecimal prevTransferIn;
    private BigDecimal prevTransferOut;
    private BigDecimal prevAmount;
    private String prevTimestamp;
    private BigDecimal deltaDeposited;
    private BigDecimal deltaWithdrawn;
    private BigDecimal deltaTransferIn;
    private BigDecimal deltaTransferOut;
    private BigDecimal deltaAmount;
    private BigDecimal deposited;
    private BigDecimal withdrawn;
    private BigDecimal transferIn;
    private BigDecimal transferOut;
    private BigDecimal amount;
    private BigDecimal pendingCredit;
    private BigDecimal pendingDebit;
    private BigDecimal confirmedDebit;
    private String timestamp;
    private String addr;
    private String script;

    @JsonCreator
    public BitmexWallet(@JsonProperty("account") BigDecimal account,
                        @JsonProperty("currency") String currency,
                        @JsonProperty("prevDeposited") BigDecimal prevDeposited,
                        @JsonProperty("prevWithdrawn") BigDecimal prevWithdrawn,
                        @JsonProperty("prevTransferIn") BigDecimal prevTransferIn,
                        @JsonProperty("prevTransferOut") BigDecimal prevTransferOut,
                        @JsonProperty("prevAmount") BigDecimal prevAmount,
                        @JsonProperty("prevTimestamp") String prevTimestamp,
                        @JsonProperty("deltaDeposited") BigDecimal deltaDeposited,
                        @JsonProperty("deltaWithdrawn") BigDecimal deltaWithdrawn,
                        @JsonProperty("deltaTransferIn") BigDecimal deltaTransferIn,
                        @JsonProperty("deltaTransferOut") BigDecimal deltaTransferOut,
                        @JsonProperty("deltaAmount") BigDecimal deltaAmount,
                        @JsonProperty("deposited") BigDecimal deposited,
                        @JsonProperty("withdrawn") BigDecimal withdrawn,
                        @JsonProperty("transferIn") BigDecimal transferIn,
                        @JsonProperty("transferOut") BigDecimal transferOut,
                        @JsonProperty("amount") BigDecimal amount,
                        @JsonProperty("pendingCredit") BigDecimal pendingCredit,
                        @JsonProperty("pendingDebit") BigDecimal pendingDebit,
                        @JsonProperty("confirmedDebit") BigDecimal confirmedDebit,
                        @JsonProperty("timestamp") String timestamp,
                        @JsonProperty("addr") String addr,
                        @JsonProperty("script") String script) {
        this.account = account;
        this.currency = currency;
        this.prevDeposited = prevDeposited;
        this.prevWithdrawn = prevWithdrawn;
        this.prevTransferIn = prevTransferIn;
        this.prevTransferOut = prevTransferOut;
        this.prevAmount = prevAmount;
        this.prevTimestamp = prevTimestamp;
        this.deltaDeposited = deltaDeposited;
        this.deltaWithdrawn = deltaWithdrawn;
        this.deltaTransferIn = deltaTransferIn;
        this.deltaTransferOut = deltaTransferOut;
        this.deltaAmount = deltaAmount;
        this.deposited = deposited;
        this.withdrawn = withdrawn;
        this.transferIn = transferIn;
        this.transferOut = transferOut;
        this.amount = amount;
        this.pendingCredit = pendingCredit;
        this.pendingDebit = pendingDebit;
        this.confirmedDebit = confirmedDebit;
        this.timestamp = timestamp;
        this.addr = addr;
        this.script = script;
    }

    public BigDecimal getAccount() {
        return account;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getPrevDeposited() {
        return prevDeposited;
    }

    public BigDecimal getPrevWithdrawn() {
        return prevWithdrawn;
    }

    public BigDecimal getPrevTransferIn() {
        return prevTransferIn;
    }

    public BigDecimal getPrevTransferOut() {
        return prevTransferOut;
    }

    public BigDecimal getPrevAmount() {
        return prevAmount;
    }

    public String getPrevTimestamp() {
        return prevTimestamp;
    }

    public BigDecimal getDeltaDeposited() {
        return deltaDeposited;
    }

    public BigDecimal getDeltaWithdrawn() {
        return deltaWithdrawn;
    }

    public BigDecimal getDeltaTransferIn() {
        return deltaTransferIn;
    }

    public BigDecimal getDeltaTransferOut() {
        return deltaTransferOut;
    }

    public BigDecimal getDeltaAmount() {
        return deltaAmount;
    }

    public BigDecimal getDeposited() {
        return deposited;
    }

    public BigDecimal getWithdrawn() {
        return withdrawn;
    }

    public BigDecimal getTransferIn() {
        return transferIn;
    }

    public BigDecimal getTransferOut() {
        return transferOut;
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

    public String getTimestamp() {
        return timestamp;
    }

    public String getAddr() {
        return addr;
    }

    public String getScript() {
        return script;
    }
}
