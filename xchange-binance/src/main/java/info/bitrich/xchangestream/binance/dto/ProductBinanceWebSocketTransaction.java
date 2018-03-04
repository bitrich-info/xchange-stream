package info.bitrich.xchangestream.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.currency.CurrencyPair;

public class ProductBinanceWebSocketTransaction extends BaseBinanceWebSocketTransaction {

    protected final CurrencyPair currencyPair;

    public ProductBinanceWebSocketTransaction(
            @JsonProperty("e") String eventType,
            @JsonProperty("E") String eventTime,
            @JsonProperty("s") String symbol) {
        super(eventType, eventTime);
        
        //TODO: some symbols are 2 chars some are 4+ . See open issue: https://github.com/bitrich-info/xchange-stream/issues/100
        currencyPair = new CurrencyPair(symbol.substring(0, 3), symbol.substring(3, 6));
        
        
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }
}
