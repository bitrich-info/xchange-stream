package info.bitrich.xchangestream.okcoin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OkCoinV3StreamingMarketDataServiceTest {

    @Mock
    private OkCoinV3StreamingService okCoinStreamingService;
    private OkCoinV3StreamingMarketDataService marketDataService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        okCoinStreamingService = new OkCoinV3StreamingService("wss://real.okcoin.com:10442/ws/v3");
        marketDataService = new OkCoinV3StreamingMarketDataService(okCoinStreamingService);
    }

    @Test
    public void testGetOrderBook() throws Exception {
        // Given order book in JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ClassLoader.getSystemClassLoader().getResourceAsStream("order-bookv3.json"));


        // Call get order book observable
        okCoinStreamingService.connect().blockingAwait();
        Observable<OrderBook> orderBook = marketDataService.getOrderBook(CurrencyPair.BTC_USD);
        OrderBook orderBook1 = orderBook.blockingFirst();
    }
}
