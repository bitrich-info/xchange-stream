package info.bitrich.xchangestream.coinmate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.coinmate.dto.marketdata.CoinmateTransactionsEntry;
import org.knowm.xchange.currency.CurrencyPair;

import java.math.BigDecimal;

public class CoinmateWebSocketTrade {
    private final long timestamp;
    private final BigDecimal price;
    private final BigDecimal amount;
    private final String buyOrderId;
    private final String sellOrderId;

    public CoinmateWebSocketTrade(
            @JsonProperty("date") long timestamp,
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("buyOrderId") String buyOrderId,
            @JsonProperty("sellOrderId") String sellOrderId
    ) {
        this.timestamp = timestamp;
        this.price = price;
        this.amount = amount;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
    }

    public CoinmateTransactionsEntry toTransactionEntry(String currencyPair) {
        return new CoinmateTransactionsEntry(timestamp, "BUY"+buyOrderId+"SELL"+sellOrderId, price, amount, currencyPair);
    }
}
