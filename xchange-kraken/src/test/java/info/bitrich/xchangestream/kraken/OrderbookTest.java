package info.bitrich.xchangestream.kraken;

import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;

public class OrderbookTest {

    KrakenStreamingExchange exchange = new KrakenStreamingExchange();

    @Test
    public void testOrderBook(){
        exchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_EUR).subscribe();
    }
}
