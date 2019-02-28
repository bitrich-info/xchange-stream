package info.bitrich.xchangestream.lykke;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.lykke.dto.LykkeBalanceDto;
import info.bitrich.xchangestream.lykke.dto.LykkeStreamOrderBook;
import info.bitrich.xchangestream.service.wamp.WampStreamingService;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.lykke.LykkeAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LykkeStreamingMarketDataService implements StreamingMarketDataService {

    private WampStreamingService wampStreamingService;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public LykkeStreamingMarketDataService(WampStreamingService wampStreamingService) {
        this.wampStreamingService = wampStreamingService;
    }
    private List<LimitOrder> asks = new ArrayList<>();
    private List<LimitOrder> bids = new ArrayList<>();
    boolean newUpdate = false;

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {

        wampStreamingService.subscribeChannel("orderbook.spot."
                +LykkeAdapter.adaptToAssetPair(currencyPair).toLowerCase()+".buy").map(pubSubData -> {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    List<LykkeStreamOrderBook> lykkeStreamOrderBook
                            = Arrays.asList(mapper.readValue(pubSubData.arguments().toString(),LykkeStreamOrderBook[].class));
                    System.out.println("Number1"+lykkeStreamOrderBook.get(0).toString());
                    List<LimitOrder> bidLimitOrders = new ArrayList<>();
                    lykkeStreamOrderBook.get(0).getLevels().forEach(lykkeOrderBookLevels -> {
                        bidLimitOrders.add(new LimitOrder(
                                Order.OrderType.BID,
                                lykkeOrderBookLevels.getVolume(),
                                currencyPair,
                                lykkeOrderBookLevels.getId(),
                                null,
                                lykkeOrderBookLevels.getPrice()
                        ));
                    });
                    bids = bidLimitOrders;
                    return new OrderBook(null, asks, bids);
                });
         wampStreamingService.subscribeChannel("orderbook.spot."
                +LykkeAdapter.adaptToAssetPair(currencyPair).toLowerCase()+".sell").map(pubSubData -> {
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    List<LykkeStreamOrderBook> lykkeStreamOrderBook
                            = Arrays.asList(mapper.readValue(pubSubData.arguments().toString(),LykkeStreamOrderBook[].class));
                    System.out.println("Number1"+lykkeStreamOrderBook.get(0).toString());
                    List<LimitOrder> askLimitOrders = new ArrayList<>();
                    lykkeStreamOrderBook.get(0).getLevels().forEach(lykkeOrderBookLevels -> {
                        askLimitOrders.add(new LimitOrder(
                                Order.OrderType.ASK,
                                lykkeOrderBookLevels.getVolume(),
                                currencyPair,
                                lykkeOrderBookLevels.getId(),
                                null,
                                lykkeOrderBookLevels.getPrice()
                        ));
                    });
                    asks = askLimitOrders;
                    return new OrderBook(null, asks, bids);

                });

        ObservableOnSubscribe<OrderBook> handler = emitter -> {

            Future<OrderBook> future = executor.schedule(() -> {
//                if(newUpdate) {
//                    emitter.onNext(new OrderBook(null, asks, bids));
//                }
                emitter.onNext(new OrderBook(null,asks,bids));
                return null;
            }, 1, TimeUnit.SECONDS);

            emitter.setCancellable(() -> future.cancel(false));
        };

        return Observable.create(handler);


    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        return null;
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        return null;
    }

    public Observable<Wallet> getWallet(){
        return wampStreamingService.subscribeChannel("balances").doOnError(throwable -> {
            throwable.getMessage();
        }).map(pubSubData -> {
            System.out.println(pubSubData.arguments().toString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            LykkeBalanceDto balanceDto = mapper.readValue(pubSubData.arguments().toString(),LykkeBalanceDto.class);
            System.out.println(balanceDto);
            return null;
        });
    }

    private Observable<List<LimitOrder>> asks(CurrencyPair currencyPair){
        return wampStreamingService.subscribeChannel("orderbook.spot."
                +LykkeAdapter.adaptToAssetPair(currencyPair).toLowerCase()+".buy")
                .map(pubSubData -> {
                    System.out.println("biddddddd");
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    List<LykkeStreamOrderBook> lykkeStreamOrderBook
                            = Arrays.asList(mapper.readValue(pubSubData.arguments().toString(),LykkeStreamOrderBook[].class));
                    System.out.println(lykkeStreamOrderBook.get(0).toString());
                    List<LimitOrder> bidLimitOrders = new ArrayList<>();
                    lykkeStreamOrderBook.get(0).getLevels().forEach(lykkeOrderBookLevels -> {
                        bidLimitOrders.add(new LimitOrder(
                                Order.OrderType.BID,
                                lykkeOrderBookLevels.getVolume(),
                                currencyPair,
                                lykkeOrderBookLevels.getId(),
                                null,
                                lykkeOrderBookLevels.getPrice()
                        ));
                    });
                    return bidLimitOrders;
                });
    }
    private Observable<List<LimitOrder>> bids(CurrencyPair currencyPair){
        return wampStreamingService.subscribeChannel("orderbook.spot."
                +LykkeAdapter.adaptToAssetPair(currencyPair).toLowerCase()+".buy")
                .map(pubSubData -> {
                    System.out.println("biddddddd");
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    List<LykkeStreamOrderBook> lykkeStreamOrderBook
                            = Arrays.asList(mapper.readValue(pubSubData.arguments().toString(),LykkeStreamOrderBook[].class));
                    System.out.println(lykkeStreamOrderBook.get(0).toString());
                    List<LimitOrder> bidLimitOrders = new ArrayList<>();
                    lykkeStreamOrderBook.get(0).getLevels().forEach(lykkeOrderBookLevels -> {
                        bidLimitOrders.add(new LimitOrder(
                                Order.OrderType.BID,
                                lykkeOrderBookLevels.getVolume(),
                                currencyPair,
                                lykkeOrderBookLevels.getId(),
                                null,
                                lykkeOrderBookLevels.getPrice()
                        ));
                    });
                    return bidLimitOrders;
                });
    }
}
