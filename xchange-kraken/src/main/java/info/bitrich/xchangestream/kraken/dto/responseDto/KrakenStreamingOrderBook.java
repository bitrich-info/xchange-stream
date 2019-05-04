package info.bitrich.xchangestream.kraken.dto.responseDto;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public class KrakenStreamingOrderBook {

    private SortedMap<BigDecimal, LimitOrder> asks;
    private SortedMap<BigDecimal, LimitOrder> bids;

    private final CurrencyPair currencyPair;

    public KrakenStreamingOrderBook(KrakenStreamingOrder[] asks, KrakenStreamingOrder[] bids, CurrencyPair currencyPair) {
        this.asks = new TreeMap<>();
        this.bids = new TreeMap<>(java.util.Collections.reverseOrder());
        this.currencyPair = currencyPair;
        createKrakenOrderBook(asks, bids);
    }

    public void createKrakenOrderBook(KrakenStreamingOrder[] asks,KrakenStreamingOrder[] bids) {
        for(int i=0;i<asks.length;i++){
            this.asks.put(asks[i].getPrice(),toLimitOrder(asks[i], Order.OrderType.ASK));
            this.bids.put(bids[i].getPrice(),toLimitOrder(bids[i], Order.OrderType.BID));
        }
    }

    private LimitOrder toLimitOrder(KrakenStreamingOrder order, Order.OrderType orderType) {
        return new LimitOrder(
                orderType,order.getVolume(),
                currencyPair,null,
                Date.from(Instant.ofEpochSecond( Long.valueOf(order.getTimestamp().substring(0,order.getTimestamp().indexOf(".")))))
                ,order.getPrice());
    }

    public void updateOrderBook(KrakenStreamingOrder[] order, Order.OrderType orderType){
        for(int i=0;i<order.length;i++){
            if(orderType.equals(Order.OrderType.ASK)){
                if(order[i].getVolume().compareTo(BigDecimal.ZERO) == 0){
                    asks.remove(order[i].getPrice());
                }else{
                    asks.put(order[i].getPrice(),toLimitOrder(order[i], Order.OrderType.ASK));
                }
            }else{
                if(order[i].getVolume().compareTo(BigDecimal.ZERO) == 0){
                    bids.remove(order[i].getPrice());
                }else{
                    bids.put(order[i].getPrice(),toLimitOrder(order[i], Order.OrderType.BID));
                }
            }
        }
        verifyOrderBook();

    }

    public void verifyOrderBook(){
        if(asks.firstKey().compareTo(bids.firstKey()) <= 0
                || bids.firstKey().compareTo(asks.firstKey()) >=0){
            if(asks.get(asks.firstKey()).getTimestamp().before(bids.get(bids.firstKey()).getTimestamp())){
                asks.remove(asks.firstKey());
            }else{
                bids.remove(bids.firstKey());
            }
        }
    }
    public OrderBook toOrderbook() {
        List<LimitOrder> orderbookAsks = new ArrayList<>(asks.values());
        List<LimitOrder> orderbookBids = new ArrayList<>(bids.values());

        return new OrderBook(null, orderbookAsks, orderbookBids);
    }
}
