package info.bitrich.xchangestream.bitfinex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.bitfinex.dto.BitfinexWebSocketAuthOrder;
import info.bitrich.xchangestream.bitfinex.dto.BitfinexWebSocketAuthenticatedPreTrade;
import info.bitrich.xchangestream.bitfinex.dto.BitfinexWebSocketAuthTrade;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;

public class BitfinexStreamingServiceTest {

    private BitfinexStreamingRawService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        BitfinexStreamingExchange bitfinexStreamingExchange = new BitfinexStreamingExchange();
        service = bitfinexStreamingExchange.getStreamingAuthenticatedDataService();
    }

    @Test
    public void testGetOrders() throws Exception {

        JsonNode jsonNode = objectMapper.readTree(ClassLoader.getSystemClassLoader().getResourceAsStream("orders.json"));

        TestObserver<BitfinexWebSocketAuthOrder> test = service.getAuthenticatedOrders().test();

        service.handleMessage(jsonNode);
        BitfinexWebSocketAuthOrder expected =
            new BitfinexWebSocketAuthOrder(
                13759731408L,
                0,
                50999677532L,
                "tETHUSD",
                1530108599707L,
                1530108599726L,
                new BigDecimal("-0.02"),
                new BigDecimal("-0.02"),
                "EXCHANGE LIMIT",
                null,
                "ACTIVE",
                new BigDecimal("431.19"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0L,
                0);
        test.assertValue(expected);
    }

    @Test
    public void testGetPreTrades() throws Exception {

        JsonNode jsonNode = objectMapper.readTree(ClassLoader.getSystemClassLoader().getResourceAsStream("preTrade.json"));

        TestObserver<BitfinexWebSocketAuthenticatedPreTrade> test = service.getAuthenticatedPreTrades().test();

        service.handleMessage(jsonNode);

        BitfinexWebSocketAuthenticatedPreTrade expected =
            new BitfinexWebSocketAuthenticatedPreTrade(
                262861164L,
                "tETHUSD",
                1530187145559L,
                13787457748L,
                new BigDecimal("-0.04"),
                new BigDecimal("435.8"),
                "EXCHANGE LIMIT",
                new BigDecimal("435.8"),
                1);
        test.assertValue(expected);
    }

    @Test
    public void testGetTrades() throws Exception {

        JsonNode jsonNode = objectMapper.readTree(ClassLoader.getSystemClassLoader().getResourceAsStream("trade.json"));

        TestObserver<BitfinexWebSocketAuthTrade> test = service.getAuthenticatedTrades().test();

        service.handleMessage(jsonNode);

        BitfinexWebSocketAuthTrade expected =
            new BitfinexWebSocketAuthTrade(
                262861164L,
                "tETHUSD",
                1530187145559L,
                13787457748L,
                new BigDecimal("-0.04"),
                new BigDecimal("435.8"),
                "EXCHANGE LIMIT",
                new BigDecimal("435.8"),
                1,
                new BigDecimal("-0.0104592"),
                "USD");
        test.assertValue(expected);
    }
}
