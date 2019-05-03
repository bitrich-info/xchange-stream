package info.bitrich.xchangestream.kraken;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import org.assertj.core.api.ComparatorFactory;
import org.assertj.core.internal.Comparables;
import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.concurrent.TimeUnit;

public class KrakenStreamingOrderBookTest {

    @Test
    public void orderBookTest() throws InterruptedException{
        StreamingExchange krakenExchange = StreamingExchangeFactory.INSTANCE.createExchange(KrakenStreamingExchange.class.getName());
        krakenExchange.connect(ProductSubscription.create().addAll(CurrencyPair.BTC_USD).addAll(CurrencyPair.BTC_EUR).build()).blockingAwait();
        Disposable dis = krakenExchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_USD).subscribe(s->{
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
            dis.dispose();
            dis2.dispose();
    }

}
