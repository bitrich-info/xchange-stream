package info.bitrich.xchangestream.cexio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public class CexioStreamingServiceTest {

    private CexioStreamingExchange cexioStreamingExchange;

    @Before
    public void setUp() {
        cexioStreamingExchange = new CexioStreamingExchange();
    }

    @Test
    public void testGetOrderExecution_orderPlace() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ClassLoader.getSystemClassLoader()
                                                             .getResourceAsStream("order-place.json"));

        CexioStreamingOrderDataService service = cexioStreamingExchange.getStreamingOrderDataService();

        TestObserver<Order> test = service.getOrderExecution().test();

        service.handleMessage(jsonNode);

        CexioOrder expected = new CexioOrder(Order.OrderType.BID, CurrencyPair.BTC_USD, new BigDecimal("0.002"),
                                             "5913254239", new Date(1522135708956L), new BigDecimal("7176.5"),
                                             new BigDecimal("0.16"), Order.OrderStatus.NEW);
        test.assertValue(expected);
    }

    @Test
    public void testGetOrderExecution_orderFill() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ClassLoader.getSystemClassLoader()
                                                             .getResourceAsStream("order-fill.json"));

        CexioStreamingOrderDataService service = cexioStreamingExchange.getStreamingOrderDataService();

        TestObserver<Order> test = service.getOrderExecution().test();

        service.handleMessage(jsonNode);

        CexioOrder expected = new CexioOrder(CurrencyPair.BTC_USD, "5891752542", Order.OrderStatus.FILLED,
                                             BigDecimal.ZERO);
        test.assertValue(expected);
    }

    @Test
    public void testGetOrderExecution_orderPartialFill() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ClassLoader.getSystemClassLoader()
                                                             .getResourceAsStream("order-partial-fill.json"));

        CexioStreamingOrderDataService service = cexioStreamingExchange.getStreamingOrderDataService();

        TestObserver<Order> test = service.getOrderExecution().test();

        service.handleMessage(jsonNode);

        CexioOrder expected = new CexioOrder(CurrencyPair.BTC_USD,
                                             "5891752542",
                                             Order.OrderStatus.PARTIALLY_FILLED,
                                             new BigDecimal("0.02"));
        test.assertValue(expected);
    }

    @Test
    public void testGetOrderExecution_orderCancel() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ClassLoader.getSystemClassLoader()
                                                             .getResourceAsStream("order-cancel.json"));

        CexioStreamingOrderDataService service = cexioStreamingExchange.getStreamingOrderDataService();

        TestObserver<Order> test = service.getOrderExecution().test();

        service.handleMessage(jsonNode);

        CexioOrder expected = new CexioOrder(CurrencyPair.BTC_USD,
                                             "5891717811",
                                             Order.OrderStatus.CANCELED,
                                             new BigDecimal("0.02"));
        test.assertValue(expected);
    }

    @Test
    public void testGetOrderExecution_invalidJson() throws Exception {
        CexioStreamingOrderDataService service = cexioStreamingExchange.getStreamingOrderDataService();

        TestObserver<Order> test = service.getOrderExecution().test();

        service.messageHandler("wrong");

        test.assertError(IOException.class);
    }
}
