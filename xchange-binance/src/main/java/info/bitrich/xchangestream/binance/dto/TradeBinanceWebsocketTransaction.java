package info.bitrich.xchangestream.binance.dto;

import java.math.BigDecimal;

import org.knowm.xchange.binance.dto.marketdata.BinanceAggTrades;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TradeBinanceWebsocketTransaction extends ProductBinanceWebSocketTransaction {

  BinanceAggTrades trade;

  public TradeBinanceWebsocketTransaction(
      @JsonProperty("e") String eventType,
      @JsonProperty("E") String eventTime,
      @JsonProperty("s") String symbol,
      @JsonProperty("a") long aggregateTradeId,
      @JsonProperty("p") BigDecimal price,
      @JsonProperty("q") BigDecimal quantity,
      @JsonProperty("f") long firstTradeId,
      @JsonProperty("l") long lastTradeId,
      @JsonProperty("T") long timestamp,
      @JsonProperty("m") boolean buyerMaker
      )
  {
    super(eventType, eventTime, symbol);
    trade = new BinanceAggTrades(
        aggregateTradeId,
        price,
        quantity,
        firstTradeId,
        lastTradeId,
        timestamp,
        buyerMaker,
        true); // deprecated
  }

  public BinanceAggTrades getTrade() {
    return trade;
  }
}
