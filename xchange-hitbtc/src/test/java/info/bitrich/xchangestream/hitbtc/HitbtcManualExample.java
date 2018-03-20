package info.bitrich.xchangestream.hitbtc;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Pavel Chertalev on 15.03.2018.
 */
public class HitbtcManualExample {
    private static final Logger LOG = LoggerFactory.getLogger(HitbtcManualExample.class);

    public static void main(String[] args) {
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(HitbtcStreamingExchange.class
                .getName());

        Completable connect = exchange.connect();
        connect.blockingAwait();
        Observable<OrderBook> orderBook = exchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_USD);
        Disposable observer = orderBook.subscribe(orderBookObserver -> {
            LOG.info("First ask: {}", orderBookObserver.getAsks().get(0));
            LOG.info("First bid: {}", orderBookObserver.getBids().get(0));
        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));



/*
        exchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.LTC_BTC).subscribe(orderBook -> {
            LOG.info("First ask: {}", orderBook.getAsks().get(0));
            LOG.info("First bid: {}", orderBook.getBids().get(0));
        }, throwable -> LOG.error("ERROR in getting order book: ", throwable));

        exchange.getStreamingMarketDataService().getTrades(CurrencyPair.BTC_USD)
                .subscribe(trade -> {
                    LOG.info("TRADE: {}", trade);
                }, throwable -> LOG.error("ERROR in getting trade: ", throwable));

        Disposable observer = exchange.getStreamingMarketDataService().getTicker(CurrencyPair.ETH_BTC).subscribe(ticker -> {
            LOG.info("TICKER: {}", ticker);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
*/
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        observer.dispose();

/*

        orderBook.doOnComplete(() -> {
            System.out.println("Test");
        });
        orderBook.doOnDispose(() -> {
            System.out.println("Test");
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
        Disposable e = exchange.disconnect().subscribe(() -> System.out.println("Done"));

    }
}
