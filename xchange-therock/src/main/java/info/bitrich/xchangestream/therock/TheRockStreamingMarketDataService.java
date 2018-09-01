package info.bitrich.xchangestream.therock;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.therock.dto.TheRockStreamOrderBook;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.pusher.PusherStreamingService;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.util.ArrayList;
import java.util.List;

public class TheRockStreamingMarketDataService implements StreamingMarketDataService {
    private final PusherStreamingService service;

    public TheRockStreamingMarketDataService(PusherStreamingService service){
        this.service = service;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String channelName = currencyPair.toString().replace("/","");

        return service.subscribeChannel(channelName,"orderbook")
                .map(s-> {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    TheRockStreamOrderBook orderBook = mapper.readValue(s,TheRockStreamOrderBook.class);
                    List<LimitOrder> asks = new ArrayList<>();
                    List<LimitOrder> bids = new ArrayList<>();
                    orderBook.getAsks().forEach(theRockStreamLimitOrder -> {
                        asks.add(new LimitOrder(
                                Order.OrderType.ASK,
                                theRockStreamLimitOrder.getAmount(),
                                currencyPair,
                                null,null,
                                theRockStreamLimitOrder.getPrice()
                        ));
                    });
                    orderBook.getBids().forEach(theRockStreamLimitOrder -> {
                        bids.add(new LimitOrder(
                                Order.OrderType.BID,
                                theRockStreamLimitOrder.getAmount(),
                                currencyPair,
                                null,null,
                                theRockStreamLimitOrder.getPrice()
                        ));
                    });

                    return new OrderBook(null,asks,bids);
                });
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        return null;
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        return null;
    }
}
