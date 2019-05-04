package info.bitrich.xchangestream.kraken;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.kraken.dto.KrakenEvent;
import info.bitrich.xchangestream.kraken.dto.KrakenSubscription;
import info.bitrich.xchangestream.kraken.dto.responseDto.KrakenStreamingOrder;
import info.bitrich.xchangestream.kraken.dto.responseDto.KrakenStreamingOrderBook;
import io.reactivex.Observable;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.kraken.KrakenAdapters;
import org.knowm.xchange.kraken.KrakenUtils;
import org.knowm.xchange.utils.jackson.CurrencyPairDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class KrakenStreamingMarketDataService implements StreamingMarketDataService {
    private static final Logger LOG = LoggerFactory.getLogger(KrakenStreamingMarketDataService.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final KrakenStreamingService service;

    private final SortedMap<CurrencyPair, KrakenStreamingOrderBook> orderbooks = new TreeMap<>();

    public KrakenStreamingMarketDataService(KrakenStreamingService service) {
        this.service = service;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {

        service.setChannelName("book|0|25:"+ currencyPairConverter(currencyPair).toString());
        return service.subscribeChannel(service.getChannelName(),createKrakenEvent(null,"book|0|25",currencyPairConverter(currencyPair)))
                .filter(message->
                        (message.isArray() && (message.get(1).has("as") || message.get(1).has("a") || message.get(1).has("b")))).map(message-> {

                    KrakenStreamingOrderBook orderBook;
                    JsonNode messageField = message.get(1);
                    if(messageField.has("as")){
                        KrakenStreamingOrder[] asks = mapper.readValue(messageField.get("as").toString(),KrakenStreamingOrder[].class);
                        KrakenStreamingOrder[] bids = mapper.readValue(messageField.get("bs").toString(),KrakenStreamingOrder[].class);
                        orderBook = new KrakenStreamingOrderBook(asks,bids,currencyPair);
                        orderbooks.put(currencyPair,orderBook);

                    }else {
                        orderBook = orderbooks.get(currencyPair);
                        if(message.size() == 3 && message.get(1).has("a") && message.get(2).has("b")){
                            KrakenStreamingOrder[] asks = mapper.readValue(message.get(1).get("a").toString(),KrakenStreamingOrder[].class);
                            KrakenStreamingOrder[] bids = mapper.readValue(message.get(2).get("b").toString(),KrakenStreamingOrder[].class);
                            orderBook.updateOrderBook(asks, Order.OrderType.ASK);
                            orderBook.updateOrderBook(bids, Order.OrderType.BID);
                        }
                        else if(messageField.has("a")){
                            KrakenStreamingOrder[] asks = mapper.readValue(messageField.get("a").toString(),KrakenStreamingOrder[].class);
                            orderBook.updateOrderBook(asks, Order.OrderType.ASK);

                        }
                        else if(messageField.has("b")){
                            KrakenStreamingOrder[] bids = mapper.readValue(messageField.get("b").toString(),KrakenStreamingOrder[].class);
                            orderBook.updateOrderBook(bids, Order.OrderType.BID);
                        }
                        orderbooks.put(currencyPair,orderBook);
                    }

                    return orderBook.toOrderbook();
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

    private KrakenEvent createKrakenEvent(String reqId,String eventString,CurrencyPair... currencyPairs){
        List<String> currencyPairList = new ArrayList<>();
        for(int i=0;i<currencyPairs.length;i++){
            currencyPairList.add(currencyPairs[i].toString());
        }

        return new KrakenEvent("subscribe",reqId,currencyPairList,createKrakenSubscription(eventString));
    }

    private KrakenSubscription createKrakenSubscription(String subscriptionString){
        KrakenSubscription subscription;

        if(subscriptionString.contains("|")){
            String name  = subscriptionString.substring(0,subscriptionString.indexOf("|"));
            String interval = subscriptionString.substring(subscriptionString.indexOf("|")+1,subscriptionString.lastIndexOf("|"));
            String depth = subscriptionString.substring(subscriptionString.lastIndexOf("|")+1);
            subscription = new KrakenSubscription(name,Integer.valueOf(interval),Integer.valueOf(depth));
        }else{
            subscription = new KrakenSubscription(subscriptionString);
        }

        return subscription;
    }

    private CurrencyPair currencyPairConverter(CurrencyPair currencyPair){
        if(currencyPair.base.equals(Currency.BTC)){
            return new CurrencyPair(Currency.XBT,currencyPair.counter);
        }else if(currencyPair.counter.equals(Currency.BTC)){
            return new CurrencyPair(currencyPair.base,Currency.XBT);
        }else{
            return currencyPair;
        }
    }
}
