package info.bitrich.xchangestream.bitmex.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.util.BookSanityChecker;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Foat Akhmadeev
 * 08/08/2018
 */
public class BitmexOrderBookTest {

    private BitmexWebSocketTransaction loadWs(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
        return objectMapper.readValue(resourceAsStream, BitmexWebSocketTransaction.class);
    }

    @Test
    public void shouldGenerateCorrectPartialBook() throws IOException {
        BitmexWebSocketTransaction ws = loadWs("info/bitrich/xchangestream/bitmex/dto/book-partial.json");
        BitmexOrderbook bitmexOrderbook = ws.toBitmexOrderbook();

        final BitmexLimitOrder[] asks = bitmexOrderbook.getAsks();
        final BitmexLimitOrder[] bids = bitmexOrderbook.getBids();
        assertEquals(3947, asks.length);
        assertEquals(2189, bids.length);

        String err = BookSanityChecker.hasErrors(bitmexOrderbook.toOrderbook());
        assertNull(err, err);

        assertEquals(asks[0], new BitmexLimitOrder("XBTUSD", "8799352600", BitmexLimitOrder.ASK_SIDE,
                BigDecimal.valueOf(6474), BigDecimal.valueOf(151204)));
        assertEquals(asks[asks.length - 1], new BitmexLimitOrder("XBTUSD", "8722295650", BitmexLimitOrder.ASK_SIDE,
                BigDecimal.valueOf(777043.5), BigDecimal.valueOf(191)));

        assertEquals(bids[0], new BitmexLimitOrder("XBTUSD", "8799352650", BitmexLimitOrder.BID_SIDE,
                BigDecimal.valueOf(6473.5), BigDecimal.valueOf(922890)));
        assertEquals(bids[bids.length - 1], new BitmexLimitOrder("XBTUSD", "8799999950", BitmexLimitOrder.BID_SIDE,
                BigDecimal.valueOf(0.5), BigDecimal.valueOf(1010)));
    }

    @Test
    public void shouldGenerateCorrectBookAfterUpdate() throws IOException {
        BitmexWebSocketTransaction ws = loadWs("info/bitrich/xchangestream/bitmex/dto/book-partial.json");
        BitmexWebSocketTransaction ws2 = loadWs("info/bitrich/xchangestream/bitmex/dto/book-update.json");
        BitmexOrderbook bitmexOrderbook = ws.toBitmexOrderbook();

        BitmexLimitOrder[] levels = ws2.toBitmexOrderbookLevels();
        bitmexOrderbook.updateLevels(levels, "update");

        final BitmexLimitOrder[] asks = bitmexOrderbook.getAsks();
        final BitmexLimitOrder[] bids = bitmexOrderbook.getBids();
        assertEquals(3947, asks.length);
        assertEquals(2189, bids.length);

        String err = BookSanityChecker.hasErrors(bitmexOrderbook.toOrderbook());
        assertNull(err, err);

        assertEquals(asks[0], new BitmexLimitOrder("XBTUSD", "8799352600", BitmexLimitOrder.ASK_SIDE,
                BigDecimal.valueOf(6474), BigDecimal.valueOf(151204)));
        assertEquals(asks[asks.length - 1], new BitmexLimitOrder("XBTUSD", "8722295650", BitmexLimitOrder.ASK_SIDE,
                BigDecimal.valueOf(777043.5), BigDecimal.valueOf(191)));

        assertEquals(bids[0], new BitmexLimitOrder("XBTUSD", "8799352650", BitmexLimitOrder.BID_SIDE,
                BigDecimal.valueOf(6473.5), BigDecimal.valueOf(922890)));
        assertEquals(bids[bids.length - 1], new BitmexLimitOrder("XBTUSD", "8799999950", BitmexLimitOrder.BID_SIDE,
                BigDecimal.valueOf(0.5), BigDecimal.valueOf(42)));
    }

    @Test
    public void shouldGenerateCorrectBookAfterDelete() throws IOException {
        BitmexWebSocketTransaction ws = loadWs("info/bitrich/xchangestream/bitmex/dto/book-partial.json");
        BitmexWebSocketTransaction ws2 = loadWs("info/bitrich/xchangestream/bitmex/dto/book-update.json");
        BitmexOrderbook bitmexOrderbook = ws.toBitmexOrderbook();

        BitmexLimitOrder[] levels = ws2.toBitmexOrderbookLevels();
        bitmexOrderbook.updateLevels(levels, "delete");

        final BitmexLimitOrder[] asks = bitmexOrderbook.getAsks();
        final BitmexLimitOrder[] bids = bitmexOrderbook.getBids();
        assertEquals(3945, asks.length);
        assertEquals(2186, bids.length);

        String err = BookSanityChecker.hasErrors(bitmexOrderbook.toOrderbook());
        assertNull(err, err);

        assertEquals(asks[0], new BitmexLimitOrder("XBTUSD", "8799352600", BitmexLimitOrder.ASK_SIDE,
                BigDecimal.valueOf(6474), BigDecimal.valueOf(151204)));
        assertEquals(asks[asks.length - 1], new BitmexLimitOrder("XBTUSD", "8722295650", BitmexLimitOrder.ASK_SIDE,
                BigDecimal.valueOf(777043.5), BigDecimal.valueOf(191)));

        assertEquals(bids[0], new BitmexLimitOrder("XBTUSD", "8799352650", BitmexLimitOrder.BID_SIDE,
                BigDecimal.valueOf(6473.5), BigDecimal.valueOf(922890)));
        assertEquals(bids[bids.length - 1], new BitmexLimitOrder("XBTUSD", "8799999900", BitmexLimitOrder.BID_SIDE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(2)));
    }

    @Test
    public void shouldGenerateCorrectBookAfterInsert() throws IOException {
        BitmexWebSocketTransaction ws = loadWs("info/bitrich/xchangestream/bitmex/dto/book-partial.json");
        BitmexWebSocketTransaction ws2 = loadWs("info/bitrich/xchangestream/bitmex/dto/book-insert.json");
        BitmexOrderbook bitmexOrderbook = ws.toBitmexOrderbook();

        BitmexLimitOrder[] levels = ws2.toBitmexOrderbookLevels();
        bitmexOrderbook.updateLevels(levels, "insert");

        final BitmexLimitOrder[] asks = bitmexOrderbook.getAsks();
        final BitmexLimitOrder[] bids = bitmexOrderbook.getBids();
        assertEquals(3948, asks.length);
        assertEquals(2189, bids.length);

        String err = BookSanityChecker.hasErrors(bitmexOrderbook.toOrderbook());
        assertNull(err, err);

        assertEquals(asks[0], new BitmexLimitOrder("XBTUSD", "8799352600", BitmexLimitOrder.ASK_SIDE,
                BigDecimal.valueOf(6474), BigDecimal.valueOf(151204)));
        assertEquals(asks[asks.length - 1], new BitmexLimitOrder("XBTUSD", "123", BitmexLimitOrder.ASK_SIDE,
                BigDecimal.valueOf(777044), BigDecimal.valueOf(234)));

        assertEquals(bids[0], new BitmexLimitOrder("XBTUSD", "8799352650", BitmexLimitOrder.BID_SIDE,
                BigDecimal.valueOf(6473.5), BigDecimal.valueOf(922890)));
        assertEquals(bids[bids.length - 1], new BitmexLimitOrder("XBTUSD", "8799999950", BitmexLimitOrder.BID_SIDE,
                BigDecimal.valueOf(0.5), BigDecimal.valueOf(1010)));
    }
}
