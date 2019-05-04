package info.bitrich.xchangestream.kraken;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.kraken.dto.responseDto.KrakenStreamingOrder;
import io.reactivex.disposables.Disposable;
import org.assertj.core.api.ComparatorFactory;
import org.assertj.core.internal.Comparables;
import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class KrakenStreamingOrderBookTest {

    @Test
    public void orderBookTest() throws InterruptedException{
        StreamingExchange krakenExchange = StreamingExchangeFactory.INSTANCE.createExchange(KrakenStreamingExchange.class.getName());
        krakenExchange.connect(ProductSubscription.create().addAll(CurrencyPair.ETH_BTC).addAll(CurrencyPair.BTC_USD).addAll(CurrencyPair.BTC_EUR).build()).blockingAwait();
        Disposable dis = krakenExchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.ETH_BTC).subscribe(s->{
            for(int i=0;i<s.getBids().size();i++){
                System.out.println("Bid "+i+": price "+ s.getBids().get(i).getLimitPrice()+" volume "+s.getBids().get(i).getOriginalAmount());
            }
            for(int i=0;i<s.getAsks().size();i++){
                System.out.println("Ask "+i+": price "+ s.getAsks().get(i).getLimitPrice()+" volume "+s.getAsks().get(i).getOriginalAmount());
            }
        });
        Disposable dis2 = krakenExchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_EUR).subscribe(s->{
            for(int i=0;i<s.getBids().size();i++){
                System.out.println("Bid "+i+": price "+ s.getBids().get(i).getLimitPrice()+" volume "+s.getBids().get(i).getOriginalAmount());
            }
            for(int i=0;i<s.getAsks().size();i++){
                System.out.println("Ask "+i+": price "+ s.getAsks().get(i).getLimitPrice()+" volume "+s.getAsks().get(i).getOriginalAmount());
            }
        });
            TimeUnit.SECONDS.sleep(10);
//            dis.dispose();
            dis2.dispose();
    }

    @Test
    public void timestampTesting(){
        String timestamp = "1556969749.712237";
//        long newTimestamp = Long.valueOf(timestamp);
        System.out.println(timestamp);
        Date date = Date.from(Instant.ofEpochSecond(Long.valueOf(timestamp.substring(0,timestamp.indexOf(".")))));
        System.out.println(date.toString());
//        LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
//        Timestamp timestamp1 = new Timestamp(newTimestamp);
//        System.out.println(timestamp1.toLocalDateTime());
    }

    @Test
    public void ceilingKeyTest(){
        SortedMap<BigDecimal, LimitOrder> asks = new TreeMap<>();
        SortedMap<BigDecimal, LimitOrder> bids = new TreeMap<>();

    }
}
