package info.bitrich.xchangestream.bitstamp;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingMarketDataService;
import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingService;
import info.bitrich.xchangestream.bitstamp.v2.dto.BitstampWebSocketOrderData;
import info.bitrich.xchangestream.bitstamp.v2.dto.BitstampWebSocketOrderEvent;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BitstampStreamingMarketDataServiceV2Test
    extends BitstampStreamingMarketDataServiceBaseTest {
  @Mock private BitstampStreamingService streamingService;
  private BitstampStreamingMarketDataService marketDataService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    marketDataService = new BitstampStreamingMarketDataService(streamingService);
  }

  public void testOrderbookCommon(String channelName, Supplier<TestObserver<OrderBook>> updater)
      throws Exception {
    // Given order book in JSON
    JsonNode orderBook = mapper.readTree(this.getClass().getResource("/order-book-v2.json"));

    when(streamingService.subscribeChannel(eq(channelName), eq("data")))
        .thenReturn(Observable.just(orderBook));

    List<LimitOrder> bids = new ArrayList<>();
    bids.add(
        new LimitOrder(
            Order.OrderType.BID,
            new BigDecimal("0.922"),
            CurrencyPair.BTC_EUR,
            "",
            null,
            new BigDecimal("819.9")));
    bids.add(
        new LimitOrder(
            Order.OrderType.BID,
            new BigDecimal("0.085"),
            CurrencyPair.BTC_EUR,
            "",
            null,
            new BigDecimal("818.63")));

    List<LimitOrder> asks = new ArrayList<>();
    asks.add(
        new LimitOrder(
            Order.OrderType.ASK,
            new BigDecimal("2.89"),
            CurrencyPair.BTC_EUR,
            "",
            null,
            new BigDecimal("821.7")));
    asks.add(
        new LimitOrder(
            Order.OrderType.ASK,
            new BigDecimal("5.18"),
            CurrencyPair.BTC_EUR,
            "",
            null,
            new BigDecimal("821.65")));
    asks.add(
        new LimitOrder(
            Order.OrderType.ASK,
            new BigDecimal("0.035"),
            CurrencyPair.BTC_EUR,
            "",
            null,
            new BigDecimal("821.6")));

    // Call get order book observable
    TestObserver<OrderBook> test = updater.get();

    // We get order book object in correct order
    validateOrderBook(bids, asks, test);
  }

  @Test
  public void testGetDifferentialOrderBook() throws Exception {
    testOrderbookCommon(
        "diff_order_book_btceur",
        () -> marketDataService.getFullOrderBook(CurrencyPair.BTC_EUR).test());
  }

  @Test
  public void testGetOrderBook() throws Exception {
    testOrderbookCommon(
        "order_book_btceur", () -> marketDataService.getOrderBook(CurrencyPair.BTC_EUR).test());
  }

  @Test
  public void testGetTrades() throws Exception {
    // Given order book in JSON
    JsonNode trade = mapper.readTree(this.getClass().getResource("/trade-v2.json"));

    when(streamingService.subscribeChannel(eq("live_trades_btcusd"), eq("trade")))
        .thenReturn(Observable.just(trade));

    Trade expected =
        new Trade.Builder()
            .type(Order.OrderType.ASK)
            .originalAmount(new BigDecimal("34.390000000000001"))
            .currencyPair(CurrencyPair.BTC_USD)
            .price(new BigDecimal("914.38999999999999"))
            .timestamp(new Date(1484858423000L))
            .id("177827396")
            .build();

    // Call get order book observable
    TestObserver<Trade> test = marketDataService.getTrades(CurrencyPair.BTC_USD).test();

    // We get order book object in correct order
    validateTrades(expected, test);
  }

  @Test(expected = NotAvailableFromExchangeException.class)
  public void testGetTicker() throws Exception {
    marketDataService.getTicker(CurrencyPair.BTC_EUR).test();
  }

  @Test
  public void testOrders() throws Exception {
    String channel = "live_orders_btcusd";
    String event = "order_created";

    testOrdersCommons("/orders-v2-created.json", channel, event,
      new BitstampWebSocketOrderEvent(
        new BitstampWebSocketOrderData(
                0,
                BigDecimal.valueOf(5901.51d),
                1584902114L,
                BigDecimal.valueOf(0.1379724d),
                "1212691004391424",
                "0.13797240",
                "5901.51",
                1212691004391424L,
                "1584902114401000"),
        "order_created",
        "live_orders_btcusd"
      )
    );

    testOrdersCommons("/orders-v2-deleted.json", channel, event,
            new BitstampWebSocketOrderEvent(
                    new BitstampWebSocketOrderData(
                            0,
                            BigDecimal.valueOf(5868.27d),
                            1584902114L,
                            BigDecimal.valueOf(0.06d),
                            "1212691001229312",
                            "0.06000000",
                            "5868.27",
                            1212691001229312L,
                            "1584902114398000"),
                    "order_deleted",
                    "live_orders_btcusd"
            )
    );
  }

  public void testOrdersCommons(String resourceName, String channel, String event,
                                BitstampWebSocketOrderEvent expected) throws Exception {
    // Given order event in JSON
    JsonNode order = mapper.readTree(this.getClass().getResource(resourceName));

    when(streamingService.subscribeChannel(eq(channel), eq(event))).thenReturn(Observable.just(order));

    // Call get order book observable
    TestObserver<BitstampWebSocketOrderEvent> test = marketDataService.getOrders(CurrencyPair.BTC_USD).test();

    // We get order book object in correct order
    validateTrades(expected, test);
  }

}
