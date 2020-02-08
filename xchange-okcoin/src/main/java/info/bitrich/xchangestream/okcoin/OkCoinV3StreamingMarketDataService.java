package info.bitrich.xchangestream.okcoin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.okcoin.dto.OkCoinOrderbook;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.okcoin.OkCoinAdapters;
import org.knowm.xchange.okcoin.dto.marketdata.OkCoinDepth;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class OkCoinV3StreamingMarketDataService implements StreamingMarketDataService {
    private final OkCoinV3StreamingService service;

    private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
    private final Map<String, OkCoinOrderbook> orderBooks = new HashMap<>();

    OkCoinV3StreamingMarketDataService(OkCoinV3StreamingService service) {
        this.service = service;
    }


    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
    String channel =
        String.format(
            "spot/depth:%s-%s",
            currencyPair.base.toString().toUpperCase(),
            currencyPair.counter.toString().toUpperCase());

        final String key=channel;

        return service.subscribeChannel(channel)
                .map(s -> {
                    OkCoinOrderbook okCoinOrderbook;
                    JsonNode data = s.get("data").get(0);
                    if (!orderBooks.containsKey(key)) {
                        OkCoinDepth okCoinDepth = mapper.treeToValue(data, OkCoinDepth.class);
                        okCoinOrderbook = new OkCoinOrderbook(okCoinDepth);
                        orderBooks.put(key, okCoinOrderbook);
                    } else {
                        okCoinOrderbook = orderBooks.get(key);
                        if (data.has("asks")) {
                            JsonNode n = data.get("asks");
                            if (n.size() > 0) {
                                BigDecimal[][] askLevels = mapper.treeToValue(n, BigDecimal[][].class);
                                okCoinOrderbook.updateLevels(askLevels, Order.OrderType.ASK);
                            }
                        }

                        if (data.has("bids")) {
                            if (data.get("bids").size() > 0) {
                                BigDecimal[][] bidLevels = mapper.treeToValue(data.get("bids"), BigDecimal[][].class);
                                okCoinOrderbook.updateLevels(bidLevels, Order.OrderType.BID);
                            }
                        }
                    }

                    return OkCoinAdapters.adaptOrderBook(okCoinOrderbook.toOkCoinDepth(data.get("timestamp").asLong()), currencyPair);
                });
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        return null;
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        return null;
    }
}
