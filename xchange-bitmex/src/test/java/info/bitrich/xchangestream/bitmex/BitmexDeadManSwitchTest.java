package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.util.LocalExchangeConfig;
import info.bitrich.xchangestream.util.PropsLoader;
import org.junit.Ignore;
import org.junit.Test;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitmex.dto.marketdata.BitmexPrivateOrder;
import org.knowm.xchange.bitmex.dto.trade.BitmexSide;
import org.knowm.xchange.bitmex.service.BitmexMarketDataService;
import org.knowm.xchange.bitmex.service.BitmexTradeService;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.utils.CertHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.knowm.xchange.bitmex.BitmexPrompt.PERPETUAL;

/**
 * @author Nikita Belenkiy on 18/05/2018.
 */
public class BitmexDeadManSwitchTest {
    private static final Logger logger = LoggerFactory.getLogger(BitmexDeadManSwitchTest.class);

    @Test
    @Ignore
    public void testDeadmanSwitch() throws Exception {
        LocalExchangeConfig localConfig = PropsLoader.loadKeys(
                "bitmex.secret.keys", "bitmex.secret.keys.origin", "bitmex");
        CertHelper.trustAllCerts();
        BitmexStreamingExchange exchange =
                (BitmexStreamingExchange) ExchangeFactory.INSTANCE.createExchange(BitmexStreamingExchange.class);
        ExchangeSpecification defaultExchangeSpecification = exchange.getDefaultExchangeSpecification();

        defaultExchangeSpecification.setExchangeSpecificParametersItem("Use_Sandbox", true);

        defaultExchangeSpecification.setApiKey(localConfig.getApiKey());
        defaultExchangeSpecification.setSecretKey(localConfig.getSecretKey());

        defaultExchangeSpecification.setShouldLoadRemoteMetaData(true);
        defaultExchangeSpecification.setProxyHost("localhost");
        defaultExchangeSpecification.setProxyPort(9999);

        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.SOCKS_PROXY_HOST, "localhost");
        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.SOCKS_PROXY_PORT, 8889);

        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.USE_SANDBOX, true);
        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.ACCEPT_ALL_CERITICATES, true);
//        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.ENABLE_LOGGING_HANDLER, true);

        exchange.applySpecification(defaultExchangeSpecification);
        exchange.connect().blockingAwait();
        BitmexMarketDataService marketDataService =
                (BitmexMarketDataService) exchange.getMarketDataService();

        BitmexTradeService tradeService = (BitmexTradeService)exchange.getTradeService();

        final BitmexStreamingMarketDataService streamingMarketDataService = (BitmexStreamingMarketDataService) exchange.getStreamingMarketDataService();
//        streamingMarketDataService.authenticate();
        CurrencyPair xbtUsd = CurrencyPair.XBT_USD;

        streamingMarketDataService.getExecutions("XBTUSD").subscribe(bitmexExecution -> {
            logger.info("!!!!EXECUTION!!!! = {}", bitmexExecution);
        });
        streamingMarketDataService.getOrderBook( CurrencyPair.XBT_USD, PERPETUAL).subscribe(bitmexExecution -> {
            logger.info("!!!!OB!!!! = {}", bitmexExecution);
        });

        OrderBook orderBook = marketDataService.getOrderBook(CurrencyPair.XBT_USD, PERPETUAL);
        //    OrderBook orderBook = marketDataService.getOrderBook(new CurrencyPair(Currency.ADA,
        // Currency.BTC), BitmexPrompt.QUARTERLY);
        //    OrderBook orderBook = marketDataService.getOrderBook(new CurrencyPair(Currency.BTC,
        // Currency.USD), BitmexPrompt.BIQUARTERLY);

        System.out.println("orderBook = " + orderBook);

        streamingMarketDataService.enableDeadManSwitch(10000,30000);

        String nosOrdId = System.currentTimeMillis() + "";
        BigDecimal originalOrderSize = new BigDecimal("300");
        //    BigDecimal price = new BigDecimal("10000");
        BigDecimal price = orderBook.getBids().get(0).getLimitPrice().add(new BigDecimal("100"));
        BitmexPrivateOrder xbtusd = tradeService.placeLimitOrder("XBTUSD", originalOrderSize, price, BitmexSide.SELL, nosOrdId, null);
        logger.info("!!!!!PRIVATE_ORDER!!!! {}",xbtusd);
        Thread.sleep(100000000);
        System.out.println();
        System.out.println();


        exchange.disconnect();
    }
}
