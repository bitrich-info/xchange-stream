package info.bitrich.xchangestream.coindirect.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.coindirect.CoindirectStreamingService;
import info.bitrich.xchangestream.coindirect.dto.CoindirectOrderBookEvent;
import info.bitrich.xchangestream.coindirect.dto.CoindirectTradeEvent;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.coindirect.CoindirectAdapters;
import org.knowm.xchange.coindirect.dto.account.CoindirectAccountChannel;
import org.knowm.xchange.coindirect.dto.marketdata.CoindirectOrderbook;
import org.knowm.xchange.coindirect.service.CoindirectAccountServiceRaw;
import org.knowm.xchange.coindirect.service.CoindirectMarketDataService;
import org.knowm.xchange.coindirect.service.CoindirectMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.OrderBookUpdate;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CoindirectStreamingMarketDataService implements StreamingMarketDataService {
    private static final Logger LOG = LoggerFactory.getLogger(CoindirectStreamingMarketDataService.class);

    private final CoindirectStreamingService service;
    private final Exchange exchange;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<CurrencyPair, CoindirectOrderbook> coindirectOrderbookMap = new HashMap<>();
    private Map<CurrencyPair, OrderBook> orderBookMap = new HashMap<>();
    private CoindirectMarketDataServiceRaw coindirectMarketDataServiceRaw;
    private CoindirectAccountServiceRaw coindirectAccountServiceRaw;

    public CoindirectStreamingMarketDataService(CoindirectStreamingService service, Exchange exchange) {
        this.service = service;
        this.exchange = exchange;
    }

    private CoindirectMarketDataServiceRaw getCoindirectMarketDataServiceRaw() {
        if(coindirectMarketDataServiceRaw == null) {
            coindirectMarketDataServiceRaw = new CoindirectMarketDataServiceRaw(exchange);
        }
        return coindirectMarketDataServiceRaw;
    }

    private CoindirectAccountServiceRaw getCoindirectAccountServiceRaw() {
        if(coindirectAccountServiceRaw == null) {
            coindirectAccountServiceRaw = new CoindirectAccountServiceRaw(exchange);
        }
        return coindirectAccountServiceRaw;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String channelName = "book-" + CoindirectAdapters.toSymbol(currencyPair);

        return service.subscribeChannel(channelName)
                .map(s -> {
                    CoindirectOrderbook coindirectOrderbook = coindirectOrderbookMap.get(currencyPair);
                    OrderBook orderBook = orderBookMap.get(currencyPair);

                    if (coindirectOrderbook == null) {

                        coindirectOrderbook = getCoindirectMarketDataServiceRaw().getCoindirectOrderbook(currencyPair);
                        coindirectOrderbookMap.put(currencyPair, coindirectOrderbook);

                        orderBook = CoindirectAdapters.adaptOrderBook(currencyPair, coindirectOrderbook);
                        orderBookMap.put(currencyPair, orderBook);
                    }

                    CoindirectOrderBookEvent coindirectOrderBookEvent = objectMapper.readValue(s, CoindirectOrderBookEvent.class);

                    if (coindirectOrderBookEvent.sequence > coindirectOrderbook.sequence) {
                        OrderBookUpdate orderBookUpdate = new OrderBookUpdate(
                                CoindirectAdapters.convert(coindirectOrderBookEvent.side),
                                coindirectOrderBookEvent.size,
                                currencyPair,
                                coindirectOrderBookEvent.price,
                                new Date(),
                                coindirectOrderBookEvent.size
                        );
                        orderBook.update(orderBookUpdate);
                    } else {
                            /* Ignore this event as its older than the latest sequence */
                    }

                    return orderBook;
                });
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String channelName = "ticker-" + CoindirectAdapters.toSymbol(currencyPair);
        return service.subscribeChannel(channelName).map(s -> {
            CoindirectTradeEvent coindirectTradeEvent = objectMapper.readValue(s, CoindirectTradeEvent.class);
            return new Ticker.Builder()
                .currencyPair(currencyPair)
                .last(coindirectTradeEvent.price)
                .volume(coindirectTradeEvent.amount)
                .timestamp(new Date(coindirectTradeEvent.timestamp))
                .build();
        });
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        String channelName = "ticker-" + CoindirectAdapters.toSymbol(currencyPair);
        return service.subscribeChannel(channelName).map(s -> {
            CoindirectTradeEvent coindirectTradeEvent = objectMapper.readValue(s, CoindirectTradeEvent.class);
            return new Trade(Order.OrderType.BID, coindirectTradeEvent.amount, currencyPair, coindirectTradeEvent.price, new Date(coindirectTradeEvent.timestamp), null);
        });
    }
}
