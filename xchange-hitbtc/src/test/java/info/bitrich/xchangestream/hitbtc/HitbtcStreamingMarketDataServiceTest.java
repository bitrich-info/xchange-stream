package info.bitrich.xchangestream.hitbtc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HitbtcStreamingMarketDataServiceTest {

    @Mock
    private HitbtcStreamingService streamingService;
    private HitbtcStreamingMarketDataService marketDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        marketDataService = new HitbtcStreamingMarketDataService(streamingService);
    }

    @Test
    public void testOrderbookCommon() throws Exception {

        Supplier<TestObserver<OrderBook>> updater = () -> marketDataService.getOrderBook(CurrencyPair.BTC_EUR).test();

        // Given order book in JSON
        String orderBook = IOUtils.toString(getClass().getResource("/example/notificationSnapshotOrderBook.json"), "UTF8");

        when(streamingService.subscribeChannel(eq("orderbook-BTCEUR"))).thenReturn(Observable.just(objectMapper.readTree(orderBook)));

        List<LimitOrder> bids = new ArrayList<>();
        bids.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("0.500"), CurrencyPair.BTC_EUR, null, null, new BigDecimal("0.054558")));
        bids.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("0.076"), CurrencyPair.BTC_EUR, null, null, new BigDecimal("0.054557")));
        bids.add(new LimitOrder(Order.OrderType.BID, new BigDecimal("7.725"), CurrencyPair.BTC_EUR, null, null, new BigDecimal("0.054524")));

        List<LimitOrder> asks = new ArrayList<>();
        asks.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("0.245"), CurrencyPair.BTC_EUR, null, null, new BigDecimal("0.054588")));
        asks.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("0.000"), CurrencyPair.BTC_EUR, null, null, new BigDecimal("0.054590")));
        asks.add(new LimitOrder(Order.OrderType.ASK, new BigDecimal("2.784"), CurrencyPair.BTC_EUR, null, null, new BigDecimal("0.054591")));

        // Call get order book observable
        TestObserver<OrderBook> test = updater.get();

        // We get order book object in correct order
        test.assertValue(orderBook1 -> {
            assertThat(orderBook1.getAsks()).as("Asks").isEqualTo(asks);
            assertThat(orderBook1.getBids()).as("Bids").isEqualTo(bids);
            return true;
        });
    }

    @Test
    public void testGetTrades() throws Exception {
        // Given order book in JSON
        String trades = IOUtils.toString(getClass().getResource("/example/notificationSnapshotTrades.json"), "UTF8");

        when(streamingService.subscribeChannel(eq("trades-BTCUSD"))).thenReturn(Observable.just(objectMapper.readTree(trades)));

        Trade expected1 = new Trade(Order.OrderType.BID, new BigDecimal("0.057"), CurrencyPair.BTC_USD, new BigDecimal("0.054656"), new Date(1508430822821L), "54469456");
        Trade expected2 = new Trade(Order.OrderType.BID, new BigDecimal("0.092"), CurrencyPair.BTC_USD, new BigDecimal("0.054656"), new Date(1508430828754L), "54469497");
        Trade expected3 = new Trade(Order.OrderType.BID, new BigDecimal("0.002"), CurrencyPair.BTC_USD, new BigDecimal("0.054669"), new Date(1508430853288L), "54469697");

        // Call get order book observable
        TestObserver<Trade> test = marketDataService.getTrades(CurrencyPair.BTC_USD).test();

        test.assertValues(expected1, expected2, expected3);
    }

    @Test
    public void testGetTicker() throws Exception {
        //TODO: implement
        //marketDataService.getTicker(CurrencyPair.BTC_EUR).test();
    }
}
