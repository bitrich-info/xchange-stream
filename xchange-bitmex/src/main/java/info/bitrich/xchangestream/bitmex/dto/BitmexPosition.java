package info.bitrich.xchangestream.bitmex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

public class BitmexPosition {
    private long account;
    private String symbol;
    private String currency;
    private String underlying;
    private String quoteCurrency;
    private BigDecimal commission;
    private BigDecimal initMarginReq;
    private BigDecimal maintMarginReq;
    private BigDecimal riskLimit;
    private BigDecimal leverage;
    private boolean crossMargin;
    private BigDecimal deleveragePercentile;
    private BigDecimal rebalancedPnl;
    private BigDecimal prevRealisedPnl;
    private BigDecimal prevUnrealisedPnl;
    private BigDecimal prevClosePrice;
    private Date openingTimestamp;
    private BigDecimal openingQty;
    private BigDecimal openingCost;
    private BigDecimal openingComm;
    private BigDecimal openOrderBuyQty;
    private BigDecimal openOrderBuyCost;
    private BigDecimal openOrderBuyPremium;
    private BigDecimal openOrderSellQty;
    private BigDecimal openOrderSellCost;
    private BigDecimal openOrderSellPremium;
    private BigDecimal execBuyQty;
    private BigDecimal execBuyCost;
    private BigDecimal execSellQty;
    private BigDecimal execSellCost;
    private BigDecimal execQty;
    private BigDecimal execCost;
    private BigDecimal execComm;
    private Date currentTimestamp;
    private BigDecimal currentQty;
    private BigDecimal currentCost;
    private BigDecimal currentComm;
    private BigDecimal realisedCost;
    private BigDecimal unrealisedCost;
    private BigDecimal grossOpenCost;
    private BigDecimal grossOpenPremium;
    private BigDecimal grossExecCost;
    private boolean isOpen;
    private BigDecimal markPrice;
    private BigDecimal markValue;
    private BigDecimal riskValue;
    private BigDecimal homeNotional;
    private BigDecimal foreignNotional;
    private String posState;
    private BigDecimal posCost;
    private BigDecimal posCost2;
    private BigDecimal posCross;
    private BigDecimal posInit;
    private BigDecimal posComm;
    private BigDecimal posLoss;
    private BigDecimal posMargin;
    private BigDecimal posMaint;
    private BigDecimal posAllowance;
    private BigDecimal taxableMargin;
    private BigDecimal initMargin;
    private BigDecimal maintMargin;
    private BigDecimal sessionMargin;
    private BigDecimal targetExcessMargin;
    private BigDecimal varMargin;
    private BigDecimal realisedGrossPnl;
    private BigDecimal realisedTax;
    private BigDecimal realisedPnl;
    private BigDecimal unrealisedGrossPnl;
    private BigDecimal longBankrupt;
    private BigDecimal shortBankrupt;
    private BigDecimal taxBase;
    private BigDecimal indicativeTaxRate;
    private BigDecimal indicativeTax;
    private BigDecimal unrealisedTax;
    private BigDecimal unrealisedPnl;
    private BigDecimal unrealisedPnlPcnt;
    private BigDecimal unrealisedRoePcnt;
    private BigDecimal simpleQty;
    private BigDecimal simpleCost;
    private BigDecimal simpleValue;
    private BigDecimal simplePnl;
    private BigDecimal simplePnlPcnt;
    private BigDecimal avgCostPrice;
    private BigDecimal avgEntryPrice;
    private BigDecimal breakEvenPrice;
    private BigDecimal marginCallPrice;
    private BigDecimal liquidationPrice;
    private BigDecimal bankruptPrice;
    private Date timestamp;
    private BigDecimal lastPrice;
    private BigDecimal lastValue;

    @JsonCreator
    public BitmexPosition(@JsonProperty("account") long account,
                          @JsonProperty("symbol") String symbol,
                          @JsonProperty("currency") String currency,
                          @JsonProperty("underlying") String underlying,
                          @JsonProperty("quoteCurrency") String quoteCurrency,
                          @JsonProperty("commission") BigDecimal commission,
                          @JsonProperty("initMarginReq") BigDecimal initMarginReq,
                          @JsonProperty("maintMarginReq") BigDecimal maintMarginReq,
                          @JsonProperty("riskLimit") BigDecimal riskLimit,
                          @JsonProperty("leverage") BigDecimal leverage,
                          @JsonProperty("crossMargin") boolean crossMargin,
                          @JsonProperty("deleveragePercentile") BigDecimal deleveragePercentile,
                          @JsonProperty("rebalancedPnl") BigDecimal rebalancedPnl,
                          @JsonProperty("prevRealisedPnl") BigDecimal prevRealisedPnl,
                          @JsonProperty("prevUnrealisedPnl") BigDecimal prevUnrealisedPnl,
                          @JsonProperty("prevClosePrice") BigDecimal prevClosePrice,
                          @JsonProperty("openingTimestamp") Date openingTimestamp,
                          @JsonProperty("openingQty") BigDecimal openingQty,
                          @JsonProperty("openingCost") BigDecimal openingCost,
                          @JsonProperty("openingComm") BigDecimal openingComm,
                          @JsonProperty("openOrderBuyQty") BigDecimal openOrderBuyQty,
                          @JsonProperty("openOrderBuyCost") BigDecimal openOrderBuyCost,
                          @JsonProperty("openOrderBuyPremium") BigDecimal openOrderBuyPremium,
                          @JsonProperty("openOrderSellQty") BigDecimal openOrderSellQty,
                          @JsonProperty("openOrderSellCost") BigDecimal openOrderSellCost,
                          @JsonProperty("openOrderSellPremium") BigDecimal openOrderSellPremium,
                          @JsonProperty("execBuyQty") BigDecimal execBuyQty,
                          @JsonProperty("execBuyCost") BigDecimal execBuyCost,
                          @JsonProperty("execSellQty") BigDecimal execSellQty,
                          @JsonProperty("execSellCost") BigDecimal execSellCost,
                          @JsonProperty("execQty") BigDecimal execQty,
                          @JsonProperty("execCost") BigDecimal execCost,
                          @JsonProperty("execComm") BigDecimal execComm,
                          @JsonProperty("currentTimestamp") Date currentTimestamp,
                          @JsonProperty("currentQty") BigDecimal currentQty,
                          @JsonProperty("currentCost") BigDecimal currentCost,
                          @JsonProperty("currentComm") BigDecimal currentComm,
                          @JsonProperty("realisedCost") BigDecimal realisedCost,
                          @JsonProperty("unrealisedCost") BigDecimal unrealisedCost,
                          @JsonProperty("grossOpenCost") BigDecimal grossOpenCost,
                          @JsonProperty("grossOpenPremium") BigDecimal grossOpenPremium,
                          @JsonProperty("grossExecCost") BigDecimal grossExecCost,
                          @JsonProperty("isOpen") boolean isOpen,
                          @JsonProperty("markPrice") BigDecimal markPrice,
                          @JsonProperty("markValue") BigDecimal markValue,
                          @JsonProperty("riskValue") BigDecimal riskValue,
                          @JsonProperty("homeNotional") BigDecimal homeNotional,
                          @JsonProperty("foreignNotional") BigDecimal foreignNotional,
                          @JsonProperty("posState") String posState,
                          @JsonProperty("posCost") BigDecimal posCost,
                          @JsonProperty("posCost2") BigDecimal posCost2,
                          @JsonProperty("posCross") BigDecimal posCross,
                          @JsonProperty("posInit") BigDecimal posInit,
                          @JsonProperty("posComm") BigDecimal posComm,
                          @JsonProperty("posLoss") BigDecimal posLoss,
                          @JsonProperty("posMargin") BigDecimal posMargin,
                          @JsonProperty("posMaint") BigDecimal posMaint,
                          @JsonProperty("posAllowance") BigDecimal posAllowance,
                          @JsonProperty("taxableMargin") BigDecimal taxableMargin,
                          @JsonProperty("initMargin") BigDecimal initMargin,
                          @JsonProperty("maintMargin") BigDecimal maintMargin,
                          @JsonProperty("sessionMargin") BigDecimal sessionMargin,
                          @JsonProperty("targetExcessMargin") BigDecimal targetExcessMargin,
                          @JsonProperty("varMargin") BigDecimal varMargin,
                          @JsonProperty("realisedGrossPnl") BigDecimal realisedGrossPnl,
                          @JsonProperty("realisedTax") BigDecimal realisedTax,
                          @JsonProperty("realisedPnl") BigDecimal realisedPnl,
                          @JsonProperty("unrealisedGrossPnl") BigDecimal unrealisedGrossPnl,
                          @JsonProperty("longBankrupt") BigDecimal longBankrupt,
                          @JsonProperty("shortBankrupt") BigDecimal shortBankrupt,
                          @JsonProperty("taxBase") BigDecimal taxBase,
                          @JsonProperty("indicativeTaxRate") BigDecimal indicativeTaxRate,
                          @JsonProperty("indicativeTax") BigDecimal indicativeTax,
                          @JsonProperty("unrealisedTax") BigDecimal unrealisedTax,
                          @JsonProperty("unrealisedPnl") BigDecimal unrealisedPnl,
                          @JsonProperty("unrealisedPnlPcnt") BigDecimal unrealisedPnlPcnt,
                          @JsonProperty("unrealisedRoePcnt") BigDecimal unrealisedRoePcnt,
                          @JsonProperty("simpleQty") BigDecimal simpleQty,
                          @JsonProperty("simpleCost") BigDecimal simpleCost,
                          @JsonProperty("simpleValue") BigDecimal simpleValue,
                          @JsonProperty("simplePnl") BigDecimal simplePnl,
                          @JsonProperty("simplePnlPcnt") BigDecimal simplePnlPcnt,
                          @JsonProperty("avgCostPrice") BigDecimal avgCostPrice,
                          @JsonProperty("avgEntryPrice") BigDecimal avgEntryPrice,
                          @JsonProperty("breakEvenPrice") BigDecimal breakEvenPrice,
                          @JsonProperty("marginCallPrice") BigDecimal marginCallPrice,
                          @JsonProperty("liquidationPrice") BigDecimal liquidationPrice,
                          @JsonProperty("bankruptPrice") BigDecimal bankruptPrice,
                          @JsonProperty("timestamp") Date timestamp,
                          @JsonProperty("lastPrice") BigDecimal lastPrice,
                          @JsonProperty("lastValue") BigDecimal lastValue) {
        this.account = account;
        this.symbol = symbol;
        this.currency = currency;
        this.underlying = underlying;
        this.quoteCurrency = quoteCurrency;
        this.commission = commission;
        this.initMarginReq = initMarginReq;
        this.maintMarginReq = maintMarginReq;
        this.riskLimit = riskLimit;
        this.leverage = leverage;
        this.crossMargin = crossMargin;
        this.deleveragePercentile = deleveragePercentile;
        this.rebalancedPnl = rebalancedPnl;
        this.prevRealisedPnl = prevRealisedPnl;
        this.prevUnrealisedPnl = prevUnrealisedPnl;
        this.prevClosePrice = prevClosePrice;
        this.openingTimestamp = openingTimestamp;
        this.openingQty = openingQty;
        this.openingCost = openingCost;
        this.openingComm = openingComm;
        this.openOrderBuyQty = openOrderBuyQty;
        this.openOrderBuyCost = openOrderBuyCost;
        this.openOrderBuyPremium = openOrderBuyPremium;
        this.openOrderSellQty = openOrderSellQty;
        this.openOrderSellCost = openOrderSellCost;
        this.openOrderSellPremium = openOrderSellPremium;
        this.execBuyQty = execBuyQty;
        this.execBuyCost = execBuyCost;
        this.execSellQty = execSellQty;
        this.execSellCost = execSellCost;
        this.execQty = execQty;
        this.execCost = execCost;
        this.execComm = execComm;
        this.currentTimestamp = currentTimestamp;
        this.currentQty = currentQty;
        this.currentCost = currentCost;
        this.currentComm = currentComm;
        this.realisedCost = realisedCost;
        this.unrealisedCost = unrealisedCost;
        this.grossOpenCost = grossOpenCost;
        this.grossOpenPremium = grossOpenPremium;
        this.grossExecCost = grossExecCost;
        this.isOpen = isOpen;
        this.markPrice = markPrice;
        this.markValue = markValue;
        this.riskValue = riskValue;
        this.homeNotional = homeNotional;
        this.foreignNotional = foreignNotional;
        this.posState = posState;
        this.posCost = posCost;
        this.posCost2 = posCost2;
        this.posCross = posCross;
        this.posInit = posInit;
        this.posComm = posComm;
        this.posLoss = posLoss;
        this.posMargin = posMargin;
        this.posMaint = posMaint;
        this.posAllowance = posAllowance;
        this.taxableMargin = taxableMargin;
        this.initMargin = initMargin;
        this.maintMargin = maintMargin;
        this.sessionMargin = sessionMargin;
        this.targetExcessMargin = targetExcessMargin;
        this.varMargin = varMargin;
        this.realisedGrossPnl = realisedGrossPnl;
        this.realisedTax = realisedTax;
        this.realisedPnl = realisedPnl;
        this.unrealisedGrossPnl = unrealisedGrossPnl;
        this.longBankrupt = longBankrupt;
        this.shortBankrupt = shortBankrupt;
        this.taxBase = taxBase;
        this.indicativeTaxRate = indicativeTaxRate;
        this.indicativeTax = indicativeTax;
        this.unrealisedTax = unrealisedTax;
        this.unrealisedPnl = unrealisedPnl;
        this.unrealisedPnlPcnt = unrealisedPnlPcnt;
        this.unrealisedRoePcnt = unrealisedRoePcnt;
        this.simpleQty = simpleQty;
        this.simpleCost = simpleCost;
        this.simpleValue = simpleValue;
        this.simplePnl = simplePnl;
        this.simplePnlPcnt = simplePnlPcnt;
        this.avgCostPrice = avgCostPrice;
        this.avgEntryPrice = avgEntryPrice;
        this.breakEvenPrice = breakEvenPrice;
        this.marginCallPrice = marginCallPrice;
        this.liquidationPrice = liquidationPrice;
        this.bankruptPrice = bankruptPrice;
        this.timestamp = timestamp;
        this.lastPrice = lastPrice;
        this.lastValue = lastValue;
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

    public String getUnderlying() {
        return underlying;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public BigDecimal getInitMarginReq() {
        return initMarginReq;
    }

    public BigDecimal getMaintMarginReq() {
        return maintMarginReq;
    }

    public BigDecimal getRiskLimit() {
        return riskLimit;
    }

    public BigDecimal getLeverage() {
        return leverage;
    }

    public boolean isCrossMargin() {
        return crossMargin;
    }

    public BigDecimal getDeleveragePercentile() {
        return deleveragePercentile;
    }

    public BigDecimal getRebalancedPnl() {
        return rebalancedPnl;
    }

    public BigDecimal getPrevRealisedPnl() {
        return prevRealisedPnl;
    }

    public BigDecimal getPrevUnrealisedPnl() {
        return prevUnrealisedPnl;
    }

    public BigDecimal getPrevClosePrice() {
        return prevClosePrice;
    }

    public Date getOpeningTimestamp() {
        return openingTimestamp;
    }

    public BigDecimal getOpeningQty() {
        return openingQty;
    }

    public BigDecimal getOpeningCost() {
        return openingCost;
    }

    public BigDecimal getOpeningComm() {
        return openingComm;
    }

    public BigDecimal getOpenOrderBuyQty() {
        return openOrderBuyQty;
    }

    public BigDecimal getOpenOrderBuyCost() {
        return openOrderBuyCost;
    }

    public BigDecimal getOpenOrderBuyPremium() {
        return openOrderBuyPremium;
    }

    public BigDecimal getOpenOrderSellQty() {
        return openOrderSellQty;
    }

    public BigDecimal getOpenOrderSellCost() {
        return openOrderSellCost;
    }

    public BigDecimal getOpenOrderSellPremium() {
        return openOrderSellPremium;
    }

    public BigDecimal getExecBuyQty() {
        return execBuyQty;
    }

    public BigDecimal getExecBuyCost() {
        return execBuyCost;
    }

    public BigDecimal getExecSellQty() {
        return execSellQty;
    }

    public BigDecimal getExecSellCost() {
        return execSellCost;
    }

    public BigDecimal getExecQty() {
        return execQty;
    }

    public BigDecimal getExecCost() {
        return execCost;
    }

    public BigDecimal getExecComm() {
        return execComm;
    }

    public Date getCurrentTimestamp() {
        return currentTimestamp;
    }

    public BigDecimal getCurrentQty() {
        return currentQty;
    }

    public BigDecimal getCurrentCost() {
        return currentCost;
    }

    public BigDecimal getCurrentComm() {
        return currentComm;
    }

    public BigDecimal getRealisedCost() {
        return realisedCost;
    }

    public BigDecimal getUnrealisedCost() {
        return unrealisedCost;
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

    public boolean isOpen() {
        return isOpen;
    }

    public BigDecimal getMarkPrice() {
        return markPrice;
    }

    public BigDecimal getMarkValue() {
        return markValue;
    }

    public BigDecimal getRiskValue() {
        return riskValue;
    }

    public BigDecimal getHomeNotional() {
        return homeNotional;
    }

    public BigDecimal getForeignNotional() {
        return foreignNotional;
    }

    public String getPosState() {
        return posState;
    }

    public BigDecimal getPosCost() {
        return posCost;
    }

    public BigDecimal getPosCost2() {
        return posCost2;
    }

    public BigDecimal getPosCross() {
        return posCross;
    }

    public BigDecimal getPosInit() {
        return posInit;
    }

    public BigDecimal getPosComm() {
        return posComm;
    }

    public BigDecimal getPosLoss() {
        return posLoss;
    }

    public BigDecimal getPosMargin() {
        return posMargin;
    }

    public BigDecimal getPosMaint() {
        return posMaint;
    }

    public BigDecimal getPosAllowance() {
        return posAllowance;
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

    public BigDecimal getRealisedGrossPnl() {
        return realisedGrossPnl;
    }

    public BigDecimal getRealisedTax() {
        return realisedTax;
    }

    public BigDecimal getRealisedPnl() {
        return realisedPnl;
    }

    public BigDecimal getUnrealisedGrossPnl() {
        return unrealisedGrossPnl;
    }

    public BigDecimal getLongBankrupt() {
        return longBankrupt;
    }

    public BigDecimal getShortBankrupt() {
        return shortBankrupt;
    }

    public BigDecimal getTaxBase() {
        return taxBase;
    }

    public BigDecimal getIndicativeTaxRate() {
        return indicativeTaxRate;
    }

    public BigDecimal getIndicativeTax() {
        return indicativeTax;
    }

    public BigDecimal getUnrealisedTax() {
        return unrealisedTax;
    }

    public BigDecimal getUnrealisedPnl() {
        return unrealisedPnl;
    }

    public BigDecimal getUnrealisedPnlPcnt() {
        return unrealisedPnlPcnt;
    }

    public BigDecimal getUnrealisedRoePcnt() {
        return unrealisedRoePcnt;
    }

    public BigDecimal getSimpleQty() {
        return simpleQty;
    }

    public BigDecimal getSimpleCost() {
        return simpleCost;
    }

    public BigDecimal getSimpleValue() {
        return simpleValue;
    }

    public BigDecimal getSimplePnl() {
        return simplePnl;
    }

    public BigDecimal getSimplePnlPcnt() {
        return simplePnlPcnt;
    }

    public BigDecimal getAvgCostPrice() {
        return avgCostPrice;
    }

    public BigDecimal getAvgEntryPrice() {
        return avgEntryPrice;
    }

    public BigDecimal getBreakEvenPrice() {
        return breakEvenPrice;
    }

    public BigDecimal getMarginCallPrice() {
        return marginCallPrice;
    }

    public BigDecimal getLiquidationPrice() {
        return liquidationPrice;
    }

    public BigDecimal getBankruptPrice() {
        return bankruptPrice;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public BigDecimal getLastValue() {
        return lastValue;
    }
}
