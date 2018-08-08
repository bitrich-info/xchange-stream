package info.bitrich.xchangestream.binance;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.utils.CertHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Lukas Zaoralek on 15.11.17.
 */
public class BinanceManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(BinanceManualExample.class);

    public static void main(String[] args) throws Exception {
//        CertHelper.trustAllCerts();

        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(BinanceStreamingExchange.class.getName());

//        ExchangeSpecification defaultExchangeSpecification = exchange.getDefaultExchangeSpecification();
//        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.SOCKS_PROXY_HOST, "localhost");
//        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.SOCKS_PROXY_PORT, 8889);
//
//        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.USE_SANDBOX, true);
//        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.ACCEPT_ALL_CERITICATES, true);
//        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.ENABLE_LOGGING_HANDLER, true);
//        exchange.applySpecification(defaultExchangeSpecification);

        ProductSubscription subscription = ProductSubscription.create()
                .addTicker(CurrencyPair.ETH_BTC)
                .addTicker(CurrencyPair.LTC_BTC)
                .addOrderbook(CurrencyPair.LTC_BTC)
                .addTrades(CurrencyPair.BTC_USDT)
                .build();

        exchange.connect(subscription).blockingAwait();

        exchange.getStreamingMarketDataService()
                .getTicker(CurrencyPair.ETH_BTC)
                .subscribe(ticker -> {
                    LOG.info("Ticker: {}", ticker);
                }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));

        exchange.getStreamingMarketDataService()
                .getOrderBook(CurrencyPair.LTC_BTC)
                .subscribe(orderBook -> {
                    LOG.info("Order Book: {}", orderBook);
                }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

        exchange.getStreamingMarketDataService()
                .getTrades(CurrencyPair.BTC_USDT)
                .subscribe(trade -> {
                    LOG.info("Trade: {}", trade);
                });
    }
}
