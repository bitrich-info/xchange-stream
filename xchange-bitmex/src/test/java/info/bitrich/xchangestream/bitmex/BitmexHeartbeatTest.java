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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

import static org.knowm.xchange.bitmex.BitmexPrompt.PERPETUAL;

/**
 * @author Foat Akhmadeev
 * 03/07/2018
 */
public class BitmexHeartbeatTest {

    private static final Logger logger = LoggerFactory.getLogger(BitmexHeartbeatTest.class);

    @Test
    @Ignore
    public void test () throws IOException, InterruptedException {
        LocalExchangeConfig localConfig = PropsLoader.loadKeys(
                "bitmex.secret.keys", "bitmex.secret.keys.origin", "bitmex");

        BitmexStreamingExchange exchange =
                ExchangeFactory.INSTANCE.createExchange(BitmexStreamingExchange.class);
        ExchangeSpecification defaultExchangeSpecification = exchange.getDefaultExchangeSpecification();

        defaultExchangeSpecification.setExchangeSpecificParametersItem("Use_Sandbox", true);

        defaultExchangeSpecification.setApiKey(localConfig.getApiKey());
        defaultExchangeSpecification.setSecretKey(localConfig.getSecretKey());

        defaultExchangeSpecification.setShouldLoadRemoteMetaData(true);

        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.USE_SANDBOX, true);
        defaultExchangeSpecification.setExchangeSpecificParametersItem(StreamingExchange.ACCEPT_ALL_CERITICATES, true);

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

        OrderBook orderBook = marketDataService.getOrderBook(CurrencyPair.XBT_USD, PERPETUAL);

        System.out.println("orderBook = " + orderBook);

        streamingMarketDataService.enableHeartbeat(true);

        String nosOrdId = System.currentTimeMillis() + "";
        BigDecimal originalOrderSize = new BigDecimal("300");
        //    BigDecimal price = new BigDecimal("10000");
        BigDecimal price = orderBook.getBids().get(0).getLimitPrice().add(new BigDecimal("100"));
        BitmexPrivateOrder xbtusd = tradeService.placeLimitOrder("XBTUSD", originalOrderSize, price, BitmexSide.SELL, nosOrdId, null);
        logger.info("!!!!!PRIVATE_ORDER!!!! {}",xbtusd);
        Thread.sleep(100000);
        System.out.println();
        System.out.println();


        exchange.disconnect();
    }
}
