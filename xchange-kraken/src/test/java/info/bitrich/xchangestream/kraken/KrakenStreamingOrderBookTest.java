package info.bitrich.xchangestream.kraken;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class KrakenStreamingOrderBookTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KrakenStreamingOrderBookTest.class);

    @Test
    public void orderBookTest() throws InterruptedException{
        StreamingExchange krakenExchange = StreamingExchangeFactory.INSTANCE.createExchange(KrakenStreamingExchange.class.getName());
        krakenExchange.connect(ProductSubscription.create().addAll(CurrencyPair.ETH_BTC).addAll(CurrencyPair.BTC_USD).addAll(CurrencyPair.BTC_EUR).build()).blockingAwait();
        Disposable dis = krakenExchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.ETH_BTC).subscribe(s->{
            for(int i=0;i<s.getBids().size();i++){
                System.out.println("Bid "+i+": price "+ s.getBids().get(i).getLimitPrice()+" volume "+s.getBids().get(i).getOriginalAmount()+" date "+s.getBids().get(i).getTimestamp());
            }
            for(int i=0;i<s.getAsks().size();i++){
                System.out.println("Ask "+i+": price "+ s.getAsks().get(i).getLimitPrice()+" volume "+s.getAsks().get(i).getOriginalAmount()+" date "+s.getAsks().get(i).getTimestamp());
            }
        });
        Disposable dis2 = krakenExchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_EUR).subscribe(s->{
            for(int i=0;i<s.getBids().size();i++){
                System.out.println("Bid "+i+": price "+ s.getBids().get(i).getLimitPrice()+" volume "+s.getBids().get(i).getOriginalAmount()+" date "+s.getBids().get(i).getTimestamp());
            }
            for(int i=0;i<s.getAsks().size();i++){
                System.out.println("Ask "+i+": price "+ s.getAsks().get(i).getLimitPrice()+" volume "+s.getAsks().get(i).getOriginalAmount()+" date "+s.getAsks().get(i).getTimestamp());
            }
        });
            TimeUnit.SECONDS.sleep(10);
            dis.dispose();
            dis2.dispose();
    }

//    @Test
    public void orderBookVerificationTest(){
        StreamingExchange krakenExchange = StreamingExchangeFactory.INSTANCE.createExchange(KrakenStreamingExchange.class.getName());
        krakenExchange.connect(ProductSubscription.create().addAll(CurrencyPair.ETH_BTC).addAll(CurrencyPair.BTC_USD).addAll(CurrencyPair.BTC_EUR).build()).blockingAwait();

        krakenExchange.getStreamingMarketDataService().getOrderBook(CurrencyPair.BTC_EUR).subscribe(orderBook -> {
            LOGGER.info("Ask 3: "+orderBook.getAsks().get(2).getLimitPrice()+" volume "+ orderBook.getAsks().get(2).getOriginalAmount());
            LOGGER.info("Ask 2: "+orderBook.getAsks().get(1).getLimitPrice()+" volume "+ orderBook.getAsks().get(1).getOriginalAmount());
            LOGGER.info("Ask 1: "+orderBook.getAsks().get(0).getLimitPrice()+" volume "+ orderBook.getAsks().get(0).getOriginalAmount());
            LOGGER.info("--");
            LOGGER.info("Bid 1: "+orderBook.getBids().get(0).getLimitPrice()+" volume "+ orderBook.getBids().get(0).getOriginalAmount());
            LOGGER.info("Bid 2: "+orderBook.getBids().get(1).getLimitPrice()+" volume "+ orderBook.getBids().get(1).getOriginalAmount());
            LOGGER.info("Bid 3: "+orderBook.getBids().get(2).getLimitPrice()+" volume "+ orderBook.getBids().get(2).getOriginalAmount());
            LOGGER.info("=================");
        });

        while(true){
            try{
                TimeUnit.SECONDS.sleep(10000);
            }catch (Exception e){
                LOGGER.error(e.getMessage(),e);
            }
        }
    }

}
