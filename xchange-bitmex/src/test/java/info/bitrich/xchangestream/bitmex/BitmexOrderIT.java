package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.bitmex.dto.BitmexExecution;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.util.LocalExchangeConfig;
import info.bitrich.xchangestream.util.PropsLoader;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.bitmex.dto.marketdata.BitmexPrivateOrder;
import org.knowm.xchange.bitmex.dto.trade.BitmexSide;
import org.knowm.xchange.bitmex.service.BitmexMarketDataService;
import org.knowm.xchange.bitmex.service.BitmexTradeService;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.knowm.xchange.bitmex.BitmexPrompt.PERPETUAL;

/**
 * @author Nikita Belenkiy on 18/05/2018.
 */
public class BitmexOrderIT {
    private static final CurrencyPair xbtUsd = CurrencyPair.XBT_USD;
    private static final Logger LOG = LoggerFactory.getLogger(BitmexTest.class);

    private static final BigDecimal priceShift = new BigDecimal("50");
    private BitmexStreamingMarketDataService streamingMarketDataService;
    private BigDecimal testAskPrice;
    private BigDecimal testBidPrice;
    private BitmexTradeService tradeService;
    private StreamingExchange exchange;

    @Before
    public void setup() throws IOException {
        LocalExchangeConfig localConfig = PropsLoader.loadKeys(
                "bitmex.secret.keys", "bitmex.secret.keys.origin", "bitmex");
        exchange = StreamingExchangeFactory.INSTANCE.createExchange(BitmexStreamingExchange.class.getName());

        exchange.applySpecification(BitmexTestsCommons.getExchangeSpecification(localConfig,
                exchange.getDefaultExchangeSpecification()));
        exchange.connect().blockingAwait();

        BitmexMarketDataService marketDataService =
                (BitmexMarketDataService) exchange.getMarketDataService();

        streamingMarketDataService =
                (BitmexStreamingMarketDataService) exchange.getStreamingMarketDataService();

        OrderBook orderBook = marketDataService.getOrderBook(xbtUsd, PERPETUAL);
        List<LimitOrder> asks = orderBook.getAsks();
        // todo : for the streaming service best ask is at 0 pos
        BigDecimal topPriceAsk = getPrice(asks, asks.size() - 1);
        BigDecimal topPriceBid = getPrice(orderBook.getBids(), 0);

        LOG.info("Got best ask = {}, best bid = {}", topPriceAsk, topPriceBid);
        Assert.assertTrue("Got empty order book", topPriceAsk != null || topPriceBid != null);

        if (topPriceAsk != null) {
            testAskPrice = topPriceAsk.add(priceShift);
            testBidPrice = topPriceAsk.subtract(priceShift);
        } else {
            testAskPrice = topPriceBid.add(priceShift);
            testBidPrice = topPriceBid.subtract(priceShift);
        }

        tradeService = (BitmexTradeService) exchange.getTradeService();
    }

    @After
    public void tearDown() {
        exchange.disconnect().blockingAwait();
    }

    private BigDecimal getPrice(List<LimitOrder> side, int pos) {
        if (!side.isEmpty()) {
            return side.get(pos).getLimitPrice();
        }
        return null;
    }

    private String generateOrderId() {
        return System.currentTimeMillis() + "";
    }

    private String placeLimitOrder(String clOrdId, BigDecimal price, String size, Order.OrderType type) throws Exception {
        LimitOrder limitOrder =
                new LimitOrder(
                        type,
                        new BigDecimal(size),
                        xbtUsd,
                        clOrdId,
                        new Date(),
                        price);
        String orderId = tradeService.placeLimitOrder(limitOrder);
        LOG.info("Order was placed with id = {}", orderId);
        return orderId;
    }

    private BitmexPrivateOrder cancelLimitOrder(String clOrdId) {
        List<BitmexPrivateOrder> bitmexPrivateOrders =
                tradeService.cancelBitmexOrder(null, clOrdId);
        Assert.assertEquals(1, bitmexPrivateOrders.size());
        BitmexPrivateOrder order = bitmexPrivateOrders.get(0);
        Assert.assertEquals(BitmexPrivateOrder.OrderStatus.Canceled, order.getOrderStatus());
        LOG.info("Order was cancelled = {}", order);
        return order;
    }

    private void checkPrivateOrder(String orderId, BigDecimal price, String size, BitmexSide side,
                                   BitmexPrivateOrder bitmexPrivateOrder) {
        Assert.assertEquals(orderId, bitmexPrivateOrder.getId());
        Assert.assertEquals(price, bitmexPrivateOrder.getPrice());
        Assert.assertEquals(size, bitmexPrivateOrder.getVolume().toString());
        Assert.assertEquals(side, bitmexPrivateOrder.getSide());
    }

    @Test
    public void shouldPlaceLimitOrder() throws Exception {
        final String clOrdId = generateOrderId();
        String orderId = placeLimitOrder(clOrdId, testAskPrice, "10", Order.OrderType.ASK);
        Assert.assertNotNull(orderId);
        cancelLimitOrder(clOrdId);
    }

    @Test
    public void shouldCancelOrder() throws Exception {
        final String clOrdId = generateOrderId();
        String orderId = placeLimitOrder(clOrdId, testAskPrice, "10", Order.OrderType.ASK);
        BitmexPrivateOrder bitmexPrivateOrder = cancelLimitOrder(clOrdId);

        checkPrivateOrder(orderId, testAskPrice, "10", BitmexSide.SELL, bitmexPrivateOrder);
    }

    @Test
    public void shouldReplaceOrder() throws Exception {
        final String clOrdId = generateOrderId();
        String orderId = placeLimitOrder(clOrdId, testAskPrice, "10", Order.OrderType.ASK);

        final String replaceId = clOrdId + "replace";
        BitmexPrivateOrder bitmexPrivateOrder =
                tradeService.replaceLimitOrder(
                        "XBTUSD",
                        new BigDecimal("5"),
                        null,
                        orderId,
                        replaceId,
                        clOrdId);
        LOG.info("Order was replaced = {}", bitmexPrivateOrder);

        checkPrivateOrder(orderId, testAskPrice, "5", BitmexSide.SELL, bitmexPrivateOrder);
        cancelLimitOrder(replaceId);
    }

    @Test
    public void shouldCancelAllOrders() throws Exception {
        final String clOrdId = generateOrderId();
        String orderId = placeLimitOrder(clOrdId, testAskPrice, "10", Order.OrderType.ASK);
        final String clOrdId2 = generateOrderId();
        String orderId2 = placeLimitOrder(clOrdId2, testBidPrice, "5", Order.OrderType.BID);

        List<BitmexPrivateOrder> bitmexPrivateOrders = tradeService.cancelAllOrders();
        Assert.assertEquals(2, bitmexPrivateOrders.size());

        checkPrivateOrder(orderId, testAskPrice, "10", BitmexSide.SELL, bitmexPrivateOrders.get(0));
        checkPrivateOrder(orderId2, testBidPrice, "5", BitmexSide.BUY, bitmexPrivateOrders.get(1));
    }

    @Test
    public void shouldFillPlacedOrder() throws Exception {
        final String clOrdId = generateOrderId();
        String orderId = placeLimitOrder(clOrdId,
                testBidPrice.add(priceShift.multiply(new BigDecimal("2"))),
                "10", Order.OrderType.BID);
        Assert.assertNotNull(orderId);

        List<BitmexPrivateOrder> bitmexPrivateOrders =
                tradeService.cancelBitmexOrder(null, clOrdId);
        Assert.assertEquals(1, bitmexPrivateOrders.size());

        BitmexPrivateOrder order = bitmexPrivateOrders.get(0);
        Assert.assertEquals(BitmexPrivateOrder.OrderStatus.Filled, order.getOrderStatus());
    }

    private ScheduledExecutorService delayedOrder(String clOrdId, BigDecimal price, String size, Order.OrderType side) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            try {
                placeLimitOrder(clOrdId, price, size, side);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }, 1, TimeUnit.SECONDS);
        return scheduler;
    }

    private void expectNoExecution(Predicate<BitmexExecution> valuePredicate) {
        Observable<BitmexExecution> executionObservable = ((BitmexStreamingMarketDataService)
                exchange.getStreamingMarketDataService()).getExecutions("XBTUSD");
        executionObservable.test()
                .awaitCount(5)
                .assertNever(valuePredicate)
                .dispose();
    }

    @Test(expected = AssertionError.class)
    public void shouldGetExecutionOnFill() {
        final String clOrdId = generateOrderId();
        final ScheduledExecutorService scheduler = delayedOrder(clOrdId,
                testBidPrice.add(priceShift.multiply(new BigDecimal("2"))), "10", Order.OrderType.BID);

        expectNoExecution(execution -> Objects.equals(execution.getClOrdID(), clOrdId));
        scheduler.shutdown();
    }

    private String tryTriggerTestExecution() {
        final String clOrdId = generateOrderId();
        final ScheduledExecutorService scheduler = delayedOrder(clOrdId,
                testBidPrice.add(priceShift.multiply(new BigDecimal("2"))), "10", Order.OrderType.ASK);

        expectNoExecution(execution -> Objects.equals(execution.getClOrdID(), clOrdId)
                && Objects.equals(execution.getExecType(), "Canceled"));
        scheduler.shutdown();
        return clOrdId;
    }

    @Test(expected = AssertionError.class)
    public void shouldCancelOrderOnShortDms() throws IOException {
        streamingMarketDataService.enableDeadManSwitch(15000, 1000);

        tryTriggerTestExecution();

        streamingMarketDataService.disableDeadMansSwitch();
    }

    @Test(expected = AssertionError.class)
    public void shouldCancelOrderOnShortDmsWithHeartbeat() throws IOException {
        streamingMarketDataService.enableHeartbeat(true, 15000, 1000);

        tryTriggerTestExecution();

        streamingMarketDataService.disableHeartbeat();
    }

    @Test
    public void shouldNotCancelOrderOnHeartbeatWithDms() throws IOException {
        streamingMarketDataService.enableHeartbeat(true);

        String clOrdId = tryTriggerTestExecution();

        cancelLimitOrder(clOrdId);
    }
}