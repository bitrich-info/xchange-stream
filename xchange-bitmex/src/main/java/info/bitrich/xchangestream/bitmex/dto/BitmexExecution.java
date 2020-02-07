package info.bitrich.xchangestream.bitmex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.bitmex.dto.marketdata.BitmexPrivateOrder;
import org.knowm.xchange.bitmex.dto.trade.BitmexSide;

import java.math.BigDecimal;
import java.util.Date;

public class BitmexExecution {
    private String execID;
     private String orderID;
     private String clOrdID;
     private String clOrdLinkID;
     private long account;
     private String symbol;
     private String side;
     private Long lastQty;
     private BigDecimal lastPx;
     private BigDecimal underlyingLastPx;
     private String lastMkt;
     private String lastLiquidityInd;
     private BigDecimal simpleOrderQty;
     private Long orderQty;
     private BigDecimal price;
     private Long displayQty;
     private BigDecimal stopPx;
     private BigDecimal pegOffsetValue;
     private String pegPriceType;
     private String currency;
     private String settlCurrency;
     private String execType;
     private String ordType;
     private String timeInForce;
     private String execInst;
     private String contingencyType;
     private String exDestination;
     private BitmexPrivateOrder.OrderStatus ordStatus;
     private String triggered;
     private Boolean workingIndicator;
     private String ordRejReason;
     private BigDecimal simpleLeavesQty;
     private Long leavesQty;
     private BigDecimal simpleCumQty;
     private BigDecimal cumQty;
     private BigDecimal avgPx;
     private BigDecimal commission;
     private String tradePublishIndicator;
     private String multiLegReportingType;
     private String text;
     private String trdMatchID;
     private Long execCost;
     private Long execComm;
     private BigDecimal homeNotional;
     private BigDecimal foreignNotional;
     private Date transactTime;
     private Date timestamp;

    @JsonCreator
    public BitmexExecution(@JsonProperty("execID") String execID,
                           @JsonProperty("orderID") String orderID,
                           @JsonProperty("clOrdID") String clOrdID,
                           @JsonProperty("clOrdLinkID") String clOrdLinkID,
                           @JsonProperty("account") int account,
                           @JsonProperty("symbol") String symbol,
                           @JsonProperty("side") String side,
                           @JsonProperty("lastQty") Long lastQty,
                           @JsonProperty("lastPx") BigDecimal lastPx,
                           @JsonProperty("underlyingLastPx") BigDecimal underlyingLastPx,
                           @JsonProperty("lastMkt") String lastMkt,
                           @JsonProperty("lastLiquidityInd") String lastLiquidityInd,
                           @JsonProperty("simpleOrderQty") BigDecimal simpleOrderQty,
                           @JsonProperty("orderQty") long orderQty,
                           @JsonProperty("price") BigDecimal price,
                           @JsonProperty("displayQty") Long displayQty, @JsonProperty("stopPx") BigDecimal stopPx,
                           @JsonProperty("pegOffsetValue") BigDecimal pegOffsetValue,
                           @JsonProperty("pegPriceType") String pegPriceType,
                           @JsonProperty("currency") String currency,
                           @JsonProperty("settlCurrency") String settlCurrency,
                           @JsonProperty("execType") String execType,
                           @JsonProperty("ordType") String ordType,
                           @JsonProperty("timeInForce") String timeInForce,
                           @JsonProperty("execInst") String execInst,
                           @JsonProperty("contingencyType") String contingencyType,
                           @JsonProperty("exDestination") String exDestination,
                           @JsonProperty("ordStatus") BitmexPrivateOrder.OrderStatus ordStatus,
                           @JsonProperty("triggered") String triggered,
                           @JsonProperty("workingIndicator") boolean workingIndicator,
                           @JsonProperty("ordRejReason") String ordRejReason,
                           @JsonProperty("simpleLeavesQty") BigDecimal simpleLeavesQty,
                           @JsonProperty("leavesQty") Long leavesQty,
                           @JsonProperty("simpleCumQty") BigDecimal simpleCumQty,
                           @JsonProperty("cumQty") BigDecimal cumQty,
                           @JsonProperty("avgPx") BigDecimal avgPx,
                           @JsonProperty("commission") BigDecimal commission,
                           @JsonProperty("tradePublishIndicator") String tradePublishIndicator,
                           @JsonProperty("multiLegReportingType") String multiLegReportingType,
                           @JsonProperty("text") String text,
                           @JsonProperty("trdMatchID") String trdMatchID,
                           @JsonProperty("execCost") Long execCost,
                           @JsonProperty("execComm") Long execComm,
                           @JsonProperty("homeNotional") BigDecimal homeNotional,
                           @JsonProperty("foreignNotional") BigDecimal foreignNotional,
                           @JsonProperty("transactTime") Date transactTime,
                           @JsonProperty("timestamp") Date timestamp) {
        this.execID = execID;
        this.orderID = orderID;
        this.clOrdID = clOrdID;
        this.clOrdLinkID = clOrdLinkID;
        this.account = account;
        this.symbol = symbol;
        this.side = side;
        this.lastQty = lastQty;
        this.lastPx = lastPx;
        this.underlyingLastPx = underlyingLastPx;
        this.lastMkt = lastMkt;
        this.lastLiquidityInd = lastLiquidityInd;
        this.simpleOrderQty = simpleOrderQty;
        this.orderQty = orderQty;
        this.price = price;
        this.displayQty = displayQty;
        this.stopPx = stopPx;
        this.pegOffsetValue = pegOffsetValue;
        this.pegPriceType = pegPriceType;
        this.currency = currency;
        this.settlCurrency = settlCurrency;
        this.execType = execType;
        this.ordType = ordType;
        this.timeInForce = timeInForce;
        this.execInst = execInst;
        this.contingencyType = contingencyType;
        this.exDestination = exDestination;
        this.ordStatus = ordStatus;
        this.triggered = triggered;
        this.workingIndicator = workingIndicator;
        this.ordRejReason = ordRejReason;
        this.simpleLeavesQty = simpleLeavesQty;
        this.leavesQty = leavesQty;
        this.simpleCumQty = simpleCumQty;
        this.cumQty = cumQty;
        this.avgPx = avgPx;
        this.commission = commission;
        this.tradePublishIndicator = tradePublishIndicator;
        this.multiLegReportingType = multiLegReportingType;
        this.text = text;
        this.trdMatchID = trdMatchID;
        this.execCost = execCost;
        this.execComm = execComm;
        this.homeNotional = homeNotional;
        this.foreignNotional = foreignNotional;
        this.transactTime = transactTime;
        this.timestamp = timestamp;
    }

    public String getExecID() {
        return execID;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getClOrdID() {
        return clOrdID;
    }

    public String getClOrdLinkID() {
        return clOrdLinkID;
    }

    public long getAccount() {
        return account;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSide() {
        return side;
    }

    public Long getLastQty() {
        return lastQty;
    }

    public BigDecimal getLastPx() {
        return lastPx;
    }

    public BigDecimal getUnderlyingLastPx() {
        return underlyingLastPx;
    }

    public String getLastMkt() {
        return lastMkt;
    }

    public String getLastLiquidityInd() {
        return lastLiquidityInd;
    }

    public BigDecimal getSimpleOrderQty() {
        return simpleOrderQty;
    }

    public Long getOrderQty() {
        return orderQty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getDisplayQty() {
        return displayQty;
    }

    public BigDecimal getStopPx() {
        return stopPx;
    }

    public BigDecimal getPegOffsetValue() {
        return pegOffsetValue;
    }

    public String getPegPriceType() {
        return pegPriceType;
    }

    public String getCurrency() {
        return currency;
    }

    public String getSettlCurrency() {
        return settlCurrency;
    }

    public String getExecType() {
        return execType;
    }

    public String getOrdType() {
        return ordType;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public String getExecInst() {
        return execInst;
    }

    public String getContingencyType() {
        return contingencyType;
    }

    public String getExDestination() {
        return exDestination;
    }

    public BitmexPrivateOrder.OrderStatus getOrdStatus() {
        return ordStatus;
    }

    public String getTriggered() {
        return triggered;
    }

    public Boolean getWorkingIndicator() {
        return workingIndicator;
    }

    public String getOrdRejReason() {
        return ordRejReason;
    }

    public BigDecimal getSimpleLeavesQty() {
        return simpleLeavesQty;
    }

    public Long getLeavesQty() {
        return leavesQty;
    }

    public BigDecimal getSimpleCumQty() {
        return simpleCumQty;
    }

    public BigDecimal getCumQty() {
        return cumQty;
    }

    public BigDecimal getAvgPx() {
        return avgPx;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public String getTradePublishIndicator() {
        return tradePublishIndicator;
    }

    public String getMultiLegReportingType() {
        return multiLegReportingType;
    }

    public String getText() {
        return text;
    }

    public String getTrdMatchID() {
        return trdMatchID;
    }

    public Long getExecCost() {
        return execCost;
    }

    public Long getExecComm() {
        return execComm;
    }

    public BigDecimal getHomeNotional() {
        return homeNotional;
    }

    public BigDecimal getForeignNotional() {
        return foreignNotional;
    }

    public Date getTransactTime() {
        return transactTime;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
