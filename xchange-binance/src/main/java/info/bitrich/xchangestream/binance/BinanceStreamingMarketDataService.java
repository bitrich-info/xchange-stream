package info.bitrich.xchangestream.binance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.bitrich.xchangestream.binance.dto.BaseBinanceWebSocketTransaction;
import info.bitrich.xchangestream.binance.dto.BinanceRawTrade;
import info.bitrich.xchangestream.binance.dto.BinanceWebsocketTransaction;
import info.bitrich.xchangestream.binance.dto.DepthBinanceWebSocketTransaction;
import info.bitrich.xchangestream.binance.dto.ExecutionReportBinanceUserTransaction;
import info.bitrich.xchangestream.binance.dto.ExecutionReportBinanceUserTransaction.ExecutionType;
import info.bitrich.xchangestream.binance.dto.TickerBinanceWebsocketTransaction;
import info.bitrich.xchangestream.binance.dto.TradeBinanceWebsocketTransaction;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.dto.marketdata.BinanceOrderbook;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.OrderBookUpdate;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.exceptions.ExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static info.bitrich.xchangestream.binance.dto.BaseBinanceWebSocketTransaction.BinanceWebSocketTypes.DEPTH_UPDATE;
import static info.bitrich.xchangestream.binance.dto.BaseBinanceWebSocketTransaction.BinanceWebSocketTypes.TICKER_24_HR;
import static info.bitrich.xchangestream.binance.dto.BaseBinanceWebSocketTransaction.BinanceWebSocketTypes.TRADE;

public class BinanceStreamingMarketDataService implements StreamingMarketDataService {
    private static final Logger LOG = LoggerFactory.getLogger(BinanceStreamingMarketDataService.class);

    private final BinanceStreamingService service;

    private final Map<CurrencyPair, OrderbookSubscription> orderbooks = new HashMap<>();
    private final Map<CurrencyPair, Observable<BinanceTicker24h>> tickerSubscriptions = new HashMap<>();
    private final Map<CurrencyPair, Observable<OrderBook>> orderbookSubscriptions = new HashMap<>();
    private final Map<CurrencyPair, Observable<BinanceRawTrade>> tradeSubscriptions = new HashMap<>();

    private final PublishSubject<ExecutionReportBinanceUserTransaction> executionReportsPublisher = PublishSubject.create();
    private volatile Disposable executionReports;
    private volatile BinanceUserDataStreamingService binanceUserDataStreamingService;

    private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
    private final BinanceMarketDataService marketDataService;

    public BinanceStreamingMarketDataService(BinanceStreamingService service, BinanceMarketDataService marketDataService, BinanceUserDataStreamingService binanceUserDataStreamingService) {
        this.service = service;
        this.marketDataService = marketDataService;
        this.binanceUserDataStreamingService = binanceUserDataStreamingService;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        if (!service.getProductSubscription().getOrderBook().contains(currencyPair)) {
            throw new UnsupportedOperationException("Binance exchange only supports up front subscriptions - subscribe at connect time");
        }
        return orderbookSubscriptions.get(currencyPair);
    }

    public Observable<BinanceTicker24h> getRawTicker(CurrencyPair currencyPair, Object... args) {
        if (!service.getProductSubscription().getTicker().contains(currencyPair)) {
            throw new UnsupportedOperationException("Binance exchange only supports up front subscriptions - subscribe at connect time");
        }
        return tickerSubscriptions.get(currencyPair);
    }

    public Observable<BinanceRawTrade> getRawTrades(CurrencyPair currencyPair, Object... args) {
        if (!service.getProductSubscription().getTrades().contains(currencyPair)) {
            throw new UnsupportedOperationException("Binance exchange only supports up front subscriptions - subscribe at connect time");
        }
        return tradeSubscriptions.get(currencyPair);
    }

    public Observable<ExecutionReportBinanceUserTransaction> getRawExecutionReports() {
        if (executionReportsPublisher == null) {
            throw new UnsupportedOperationException("Binance exchange only supports up front subscriptions - subscribe at connect time");
        }
        return executionReportsPublisher;
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        return getRawTicker(currencyPair)
                .map(BinanceTicker24h::toTicker);
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        Observable<Trade> publicTrades = getRawTrades(currencyPair, args)
            .map(rawTrade -> new Trade(
                BinanceAdapters.convertType(rawTrade.isBuyerMarketMaker()),
                rawTrade.getQuantity(),
                currencyPair,
                rawTrade.getPrice(),
                new Date(rawTrade.getTimestamp()),
                String.valueOf(rawTrade.getTradeId())
            ));
        if (binanceUserDataStreamingService != null) {
            return publicTrades.mergeWith(getUserTrades(currencyPair, args));
        } else {
            return publicTrades;
        }
    }

    public Observable<UserTrade> getUserTrades() {
        return getRawExecutionReports()
        		.filter(r -> r.getExecutionType().equals(ExecutionType.TRADE))
    			  .map(ExecutionReportBinanceUserTransaction::toUserTrade);
    }

    public Observable<UserTrade> getUserTrades(CurrencyPair currencyPair, Object... args) {
        return getUserTrades().filter(t -> t.getCurrencyPair().equals(currencyPair));
    }

    private Observable<ExecutionReportBinanceUserTransaction> rawExecutionReports() {
        return binanceUserDataStreamingService
            .subscribeChannel(BaseBinanceWebSocketTransaction.BinanceWebSocketTypes.EXECUTION_REPORT)
            .map((JsonNode s) -> executionReport(s.toString()));
    }

    private static String channelFromCurrency(CurrencyPair currencyPair, String subscriptionType) {
        String currency = String.join("", currencyPair.toString().split("/")).toLowerCase();
        return currency + "@" + subscriptionType;
    }

    /**
     * Registers subsriptions with the streaming service for the given products.
     *
     * As we receive messages as soon as the connection is open, we need to register subscribers to handle these before the
     * first messages arrive.
     */
    public void openSubscriptions(ProductSubscription productSubscription) {
        productSubscription.getTicker()
                .forEach(currencyPair ->
                        tickerSubscriptions.put(currencyPair, triggerObservableBody(rawTickerStream(currencyPair).share())));
        productSubscription.getOrderBook()
                .forEach(currencyPair ->
                        orderbookSubscriptions.put(currencyPair, triggerObservableBody(orderBookStream(currencyPair).share())));
        productSubscription.getTrades()
                .forEach(currencyPair ->
                        tradeSubscriptions.put(currencyPair, triggerObservableBody(rawTradeStream(currencyPair).share())));
        connectUserSubscriptions();
    }

    /**
     * User data subscriptions may have to persist across multiple socket connections to different
     * URLs and therefore must act in a publisher fashion so that subscribers get an uninterrupted
     * stream.
     */
    void setUserDataStreamingService(BinanceUserDataStreamingService binanceUserDataStreamingService) {
        if (executionReports != null && !executionReports.isDisposed())
            executionReports.dispose();
        this.binanceUserDataStreamingService = binanceUserDataStreamingService;
        connectUserSubscriptions();
    }

    private void connectUserSubscriptions() {
        if (binanceUserDataStreamingService != null) {
            executionReports = rawExecutionReports().subscribe(executionReportsPublisher::onNext);
        }
    }

    private Observable<BinanceTicker24h> rawTickerStream(CurrencyPair currencyPair) {
        return service.subscribeChannel(channelFromCurrency(currencyPair, "ticker"))
                .map((JsonNode s) -> tickerTransaction(s.toString()))
                .filter(transaction ->
                        transaction.getData().getCurrencyPair().equals(currencyPair) &&
                            transaction.getData().getEventType() == TICKER_24_HR)
                .map(transaction -> transaction.getData().getTicker());
    }

    private final class OrderbookSubscription {
        long snapshotlastUpdateId;
        AtomicLong lastUpdateId = new AtomicLong(0L);
        OrderBook orderBook;
        Observable<BinanceWebsocketTransaction<DepthBinanceWebSocketTransaction>> stream;
        AtomicLong lastSyncTime = new AtomicLong(0L);

        void invalidateSnapshot() {
            snapshotlastUpdateId = 0L;
        }

        void initSnapshotIfInvalid(CurrencyPair currencyPair) {

            if (snapshotlastUpdateId != 0L)
                return;

            // Don't attempt reconnects too often to avoid bans. 3 seconds will do it.
            long now = System.currentTimeMillis();
            if (now - lastSyncTime.get() < 3000) {
                return;
            }

            try {
                LOG.info("Fetching initial orderbook snapshot for {} ", currencyPair);
                BinanceOrderbook book = marketDataService.getBinanceOrderbook(currencyPair, 1000);
                snapshotlastUpdateId = book.lastUpdateId;
                lastUpdateId.set(book.lastUpdateId);
                orderBook = BinanceMarketDataService.convertOrderBook(book, currencyPair);
            } catch (Throwable e) {
                LOG.error("Failed to fetch initial order book for " + currencyPair, e);
                snapshotlastUpdateId = 0L;
                lastUpdateId.set(0L);
                orderBook = null;
            }
            lastSyncTime.set(now);
        }
    }

    private OrderbookSubscription connectOrderBook(CurrencyPair currencyPair) {
        OrderbookSubscription subscription = new OrderbookSubscription();

        // 1. Open a stream to wss://stream.binance.com:9443/ws/bnbbtc@depth
        // 2. Buffer the events you receive from the stream.
        subscription.stream = service.subscribeChannel(channelFromCurrency(currencyPair, "depth"))
                .map((JsonNode s) -> depthTransaction(s.toString()))
                .filter(transaction ->
                        transaction.getData().getCurrencyPair().equals(currencyPair) &&
                                transaction.getData().getEventType() == DEPTH_UPDATE);


        return subscription;
                      }

    private Observable<OrderBook> orderBookStream(CurrencyPair currencyPair) {
        OrderbookSubscription subscription = orderbooks.computeIfAbsent(currencyPair, this::connectOrderBook);

        return subscription.stream

                // 3. Get a depth snapshot from https://www.binance.com/api/v1/depth?symbol=BNBBTC&limit=1000
                // (we do this if we don't already have one or we've invalidated a previous one)
                .doOnNext(transaction -> subscription.initSnapshotIfInvalid(currencyPair))

                // If we failed, don't return anything. Just keep trying until it works
                .filter(transaction -> subscription.snapshotlastUpdateId > 0L)

                .map(BinanceWebsocketTransaction::getData)

                // 4. Drop any event where u is <= lastUpdateId in the snapshot
                .filter(depth -> depth.getLastUpdateId() > subscription.snapshotlastUpdateId)

                // 5. The first processed should have U <= lastUpdateId+1 AND u >= lastUpdateId+1, and subsequent events would
                // normally have u == lastUpdateId + 1 which is stricter version of the above - let's be more relaxed
                // each update has absolute numbers so even if there's an overlap it does no harm
                .filter(depth -> {
                    long lastUpdateId = subscription.lastUpdateId.get();
                    boolean result;
                    if (lastUpdateId == 0L) {
                        result = true;
                    } else {
                        result = depth.getFirstUpdateId() <= lastUpdateId + 1 &&
                                depth.getLastUpdateId() >= lastUpdateId + 1;
                    }
                    if (result) {
                        subscription.lastUpdateId.set(depth.getLastUpdateId());
                    } else {
                        // If not, we re-sync.  This will commonly occur a few times when starting up, since
                        // given update ids 1,2,3,4,5,6,7,8,9, Binance may sometimes return a snapshot
                        // as of 5, but update events covering 1-3, 4-6 and 7-9.  We can't apply the 4-6
                        // update event without double-counting 5, and we can't apply the 7-9 update without
                        // missing 6.  The only thing we can do is to keep requesting a fresh snapshot until
                        // we get to a situation where the snapshot and an update event precisely line up.
                        LOG.info("Orderbook snapshot for {} out of date (last={}, U={}, u={}). This is normal. Re-syncing.", currencyPair, lastUpdateId, depth.getFirstUpdateId(), depth.getLastUpdateId());
                        subscription.invalidateSnapshot();
                    }
                    return result;
                })

                // 7. The data in each event is the absolute quantity for a price level
                // 8. If the quantity is 0, remove the price level
                // 9. Receiving an event that removes a price level that is not in your local order book can happen and is normal.
                .map(depth -> {
                    BinanceOrderbook ob = depth.getOrderBook();
                    ob.bids.forEach((key, value) -> subscription.orderBook.update(new OrderBookUpdate(
                            OrderType.BID,
                            null,
                            currencyPair,
                            key,
                            depth.getEventTime(),
                            value)));
                    ob.asks.forEach((key, value) -> subscription.orderBook.update(new OrderBookUpdate(
                            OrderType.ASK,
                            null,
                            currencyPair,
                            key,
                            depth.getEventTime(),
                            value)));
                    return subscription.orderBook;
                });
    }

    private Observable<BinanceRawTrade> rawTradeStream(CurrencyPair currencyPair) {
        return service.subscribeChannel(channelFromCurrency(currencyPair, "trade"))
                .map((JsonNode s) -> tradeTransaction(s.toString()))
                .filter(transaction ->
                        transaction.getData().getCurrencyPair().equals(currencyPair) &&
                                transaction.getData().getEventType() == TRADE
                )
                .map(transaction -> transaction.getData().getRawTrade());
    }

    /** Force observable to execute its body, this way we get `BinanceStreamingService` to register the observables emitter
     * ready for our message arrivals. */
    private <T> Observable<T> triggerObservableBody(Observable<T> observable) {
        Consumer<T> NOOP = whatever -> {};
        observable.subscribe(NOOP);
        return observable;
    }

    private BinanceWebsocketTransaction<TickerBinanceWebsocketTransaction> tickerTransaction(String s) {
        try {
            return mapper.readValue(s, new TypeReference<BinanceWebsocketTransaction<TickerBinanceWebsocketTransaction>>() {});
        } catch (IOException e) {
            throw new ExchangeException("Unable to parse ticker transaction", e);
        }
    }

    private BinanceWebsocketTransaction<DepthBinanceWebSocketTransaction> depthTransaction(String s) {
        try {
            return mapper.readValue(s, new TypeReference<BinanceWebsocketTransaction<DepthBinanceWebSocketTransaction>>() {});
        } catch (IOException e) {
          throw new ExchangeException("Unable to parse order book transaction", e);
        }
    }

    private BinanceWebsocketTransaction<TradeBinanceWebsocketTransaction> tradeTransaction(String s) {
        try {
            return mapper.readValue(s, new TypeReference<BinanceWebsocketTransaction<TradeBinanceWebsocketTransaction>>() {});
        } catch (IOException e) {
            throw new ExchangeException("Unable to parse trade transaction", e);
        }
    }

    private ExecutionReportBinanceUserTransaction executionReport(String s) {
      try {
          return mapper.readValue(s, ExecutionReportBinanceUserTransaction.class);
      } catch (IOException e) {
          throw new ExchangeException("Unable to parse execution report", e);
      }
    }
}