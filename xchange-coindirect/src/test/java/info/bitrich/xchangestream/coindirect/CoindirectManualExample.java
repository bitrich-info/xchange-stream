package info.bitrich.xchangestream.coindirect;

import info.bitrich.xchangestream.coindirect.service.CoindirectStreamingMarketDataService;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coindirect.CoindirectExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoindirectManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(CoindirectManualExample.class);

    public static void main(String[] args) {
        ProductSubscription productSubscription = ProductSubscription.create().addAll(CurrencyPair.BTC_ZAR)
                .addAll(CurrencyPair.ETH_BTC).addTicker(CurrencyPair.BTC_USDT).addOrderbook(CurrencyPair.ETH_BTC).build();


        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(CoindirectStreamingExchange.class);
//        exchangeSpecification.setApiKey("your-api-key"); // use this if you want to access account data streams
//        exchangeSpecification.setSecretKey("your-api-secret");

        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
        exchange.connect(productSubscription).blockingAwait();


        exchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_ZAR).subscribe(orderBook -> {
            LOG.info("{} OrderBook is now {}", CurrencyPair.BTC_ZAR, orderBook);

        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

        exchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.ETH_BTC).subscribe(orderBook -> {
            LOG.info("{} OrderBook is now {}", CurrencyPair.ETH_BTC, orderBook);

        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

        CoindirectStreamingMarketDataService coindirectStreamingMarketDataService = (CoindirectStreamingMarketDataService) exchange.getStreamingMarketDataService();

        coindirectStreamingMarketDataService.getAccountTrades().subscribe(trade -> {
            LOG.info("Got trade {}", trade);
        });

//
//        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BTC_ZAR).subscribe(ticker -> {
//            LOG.info("TICKER: {}", ticker);
//        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
//
        exchange.getStreamingMarketDataService().getTrades(CurrencyPair.ETH_BTC)
                .subscribe(trade -> {
                    LOG.info("TRADE: {}", trade);
                }, throwable -> LOG.error("ERROR in getting trades: ", throwable));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
