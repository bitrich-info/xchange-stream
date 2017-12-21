package info.bitrich.xchangestream.poloniex2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.poloniex2.dto.*;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.knowm.xchange.poloniex.PoloniexAdapters.*;

/**
 * Created by Lukas Zaoralek on 10.11.17.
 */
public class PoloniexStreamingMarketDataService implements StreamingMarketDataService {
    private static final Logger LOG = LoggerFactory.getLogger(PoloniexStreamingMarketDataService.class);

    private final PoloniexStreamingService service;
    private final Map<CurrencyPair, Integer> currencyPairMap;

    private Map<CurrencyPair, PoloniexOrderbook> orderbooks = new ConcurrentHashMap<>();

    public PoloniexStreamingMarketDataService(PoloniexStreamingService service, Map<CurrencyPair, Integer> currencyPairMap) {
        this.service = service;
        this.currencyPairMap = currencyPairMap;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        Observable<PoloniexOrderbook> subscribedOrderbook = service.subscribeCurrencyPairChannel(currencyPair)
                .filter(s -> s.getEventType().equals("i") || s.getEventType().equals("o"))
                .map(s -> {
                    PoloniexOrderbook orderbook;
                    if (s.getEventType().equals("i")) {
                        OrderbookInsertEvent insertEvent = ((PoloniexWebSocketOrderbookInsertEvent) s).getInsert();
                        SortedMap<BigDecimal, BigDecimal> asks = insertEvent.toDepthLevels(OrderbookInsertEvent.ASK_SIDE);
                        SortedMap<BigDecimal, BigDecimal> bids = insertEvent.toDepthLevels(OrderbookInsertEvent.BID_SIDE);
                        orderbook = new PoloniexOrderbook(asks, bids);
                        orderbooks.put(currencyPair, orderbook);
                    } else {
                        OrderbookModifiedEvent modifiedEvent = ((PoloniexWebSocketOrderbookModifiedEvent) s).getModifiedEvent();
                        orderbook = orderbooks.get(currencyPair);
                        orderbook.modify(modifiedEvent);
                    }
                    return orderbook;
                });

        return subscribedOrderbook.map(s -> adaptPoloniexDepth(s.toPoloniexDepth(), currencyPair));
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        int currencyPairId = currencyPairMap.getOrDefault(currencyPair, 0);
        Observable<PoloniexWebSocketTickerTransaction> subscribedChannel = service.subscribeChannel("1002")
                .map(s -> mapper.readValue(s.toString(), PoloniexWebSocketTickerTransaction.class));

        return subscribedChannel
                .filter(s -> s.getPairId() == currencyPairId)
                .map(s -> adaptPoloniexTicker(s.toPoloniexTicker(currencyPair), currencyPair));
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        Observable<PoloniexWebSocketTradeEvent> subscribedTrades = service.subscribeCurrencyPairChannel(currencyPair)
                .filter(s -> s.getEventType().equals("t"))
                .map(s -> (PoloniexWebSocketTradeEvent) s).share();

        return subscribedTrades
                .map(s -> adaptPoloniexPublicTrade(s.toPoloniexPublicTrade(currencyPair), currencyPair));
    }
}
