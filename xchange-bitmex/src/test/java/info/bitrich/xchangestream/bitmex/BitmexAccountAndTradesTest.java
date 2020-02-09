package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;

import java.util.concurrent.atomic.AtomicBoolean;

public class BitmexAccountAndTradesTest {
    //put keys from Bitmex account
    private String keyApi = "xxxxx";
    private String secretKey = "yyyy";

    private BitmexStreamingExchange exchange;

    @Before
    public void setUp() {
        ExchangeSpecification spec = new BitmexStreamingExchange().getDefaultExchangeSpecification();
        spec.setApiKey(keyApi);
        spec.setSecretKey(secretKey);
        spec.setExchangeSpecificParametersItem(StreamingExchange.USE_SANDBOX, true);//true - testnet, false - production
        exchange = (BitmexStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);
        exchange.connect().blockingAwait();
        Assert.assertTrue(exchange.isAlive());
    }

    @After
    public void tearDown() {
        if (exchange.isAlive()) exchange.disconnect();
    }

    @Test
    public void testAccountServiceChanges() {
        AtomicBoolean margin = new AtomicBoolean(false);
        AtomicBoolean wallet = new AtomicBoolean(false);
        AtomicBoolean affiliate = new AtomicBoolean(false);
        affiliate.set(true);//stub for condition. need to check on account with refs

        BitmexStreamingAccountService accountService = exchange.getStreamingAccountServiceCompat();
        accountService.getMarginChanges()
                .subscribe(bitmexMargin -> {
                    System.out.println("In margin stream");
                    margin.set(true);
                });
        accountService.getWalletChanges()
                .subscribe(bitmexWallet -> {
                    System.out.println("In wallet stream");
                    wallet.set(true);
                });
        accountService.getAffiliateChanges()
                .subscribe(bitmexAffiliate -> {
                    System.out.println("In affiliate stream");
                    affiliate.set(true);
                });

        while (true) {
            if (margin.get() && wallet.get() && affiliate.get())
                break;
        }
        Assert.assertTrue(margin.get());
        Assert.assertTrue(wallet.get());
        Assert.assertTrue(affiliate.get());
        System.out.println("Test [testAccountServiceChanges] end");
    }

    @Test
    public void testTradeServiceChanges() {
        AtomicBoolean position = new AtomicBoolean(false);
        AtomicBoolean execution = new AtomicBoolean(false);
        AtomicBoolean order = new AtomicBoolean(false);

        CurrencyPair currencyPair = CurrencyPair.XBT_USD;

        BitmexStreamingTradeService tradeService = exchange.getStreamingTradeServiceCompat();
        tradeService.getPositionChanges(currencyPair)
                .subscribe(pos -> {
                    System.out.println("In position stream");
                    position.set(true);
                });

        tradeService.getExecutionChanges(currencyPair)
                .subscribe(execution1 -> {
                    System.out.println("In execution stream");
                    execution.set(true);
                });

        tradeService.getOrderChanges(currencyPair)
                .subscribe(ord -> {
                    System.out.println("In order stream");
                    order.set(true);
                });

        while (true) {
            if (position.get() && execution.get() && order.get())
                break;
        }
        Assert.assertTrue(position.get());
        Assert.assertTrue(execution.get());
        Assert.assertTrue(order.get());
        System.out.println("Test [testTradeServiceChanges] end");
    }
}
