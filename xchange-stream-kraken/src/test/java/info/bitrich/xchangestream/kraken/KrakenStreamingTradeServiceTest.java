package info.bitrich.xchangestream.kraken;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.kraken.service.KrakenAccountServiceRaw;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class KrakenStreamingTradeServiceTest {



    @Test
    public void testOwnTrades() throws InterruptedException{
        StreamingExchange exchange = krakenStreamingSettleBotApiConnection();

        exchange.connect(ProductSubscription.create().addAll(CurrencyPair.BTC_EUR).build()).blockingAwait();

        exchange.getStreamingTradeService().getUserTrades(CurrencyPair.BTC_EUR).subscribe();
        while (true){
            TimeUnit.SECONDS.sleep(555);
        }

    }

    @Test
    public void testing() throws InterruptedException, IOException {
        Exchange exchange = krakenStreamingSettleBotApiConnection();

        KrakenAccountServiceRaw accountServiceRaw = (KrakenAccountServiceRaw) exchange.getAccountService();

        System.out.println(accountServiceRaw.getKrakenWebsocketToken());

    }

    public StreamingExchange krakenStreamingSettleBotApiConnection(){
        //Working
        final String username = "makarid";
        final String krakenApiKey = "B2yBxZJwjq5w53+z3Lv4BiPam2KrQjEduWF9zHM9gczzk9GjK6UFyx04";
        final String krakenApiSecret = "axCY2U9N5BHStaMFyP5rjkOSqSBSbOmApBihyUz65cGwRxIN5DZJhP9zqM8MESogJpnsyKEsw6oj0Mc+qBsbVg==";

        StreamingExchange krakenExchange = StreamingExchangeFactory.INSTANCE.createExchange(KrakenStreamingExchange.class.getName());

        krakenExchange.getExchangeSpecification().setApiKey(krakenApiKey);
        krakenExchange.getExchangeSpecification().setSecretKey(krakenApiSecret);
        krakenExchange.getExchangeSpecification().setUserName(username);
        krakenExchange.applySpecification(krakenExchange.getExchangeSpecification());

        return krakenExchange;
    }
}
