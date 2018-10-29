package info.bitrich.xchangestream.huobi;

import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HuobiStreamingMarketDataService implements StreamingMarketDataService {

    private final HuobiStreamingService streamingService;

    public HuobiStreamingMarketDataService(HuobiStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String channel = String.format("market.%s%s.depth.",
                currencyPair.base.toString().toLowerCase(),
                currencyPair.counter.toString().toLowerCase())
                + args[0];
        return streamingService.subscribeChannel(channel)
                .map(message -> {
                    JsonNode tick = message.get("tick");
                    Date ts = new Date(message.get("ts").longValue() * 1000);
                    JsonNode asks = tick.get("asks");
                    JsonNode bids = tick.get("bids");

                    List<LimitOrder> askOrders = new ArrayList<>();
                    List<LimitOrder> bidOrders = new ArrayList<>();
                    OrderBook orderBook = new OrderBook(ts, askOrders, bidOrders);

                    for (int i = 0; i < asks.size(); i++) {
                        JsonNode order = asks.get(i);
                        BigDecimal price = order.get(0).decimalValue();
                        BigDecimal amount = order.get(1).decimalValue();
                        LimitOrder lo =
                                new LimitOrder(Order.OrderType.ASK, amount, currencyPair,
                                        null, ts, price);
                        askOrders.add(lo);
                    }

                    for (int i = 0; i < bids.size(); i++) {
                        JsonNode order = bids.get(i);
                        BigDecimal price = order.get(0).decimalValue();
                        BigDecimal amount = order.get(1).decimalValue();
                        LimitOrder lo =
                                new LimitOrder(Order.OrderType.BID, amount, currencyPair,
                                        null, ts, price);
                        bidOrders.add(lo);
                    }
                    return orderBook;
                });
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String channel = String.format("market.%s%s.detail",
                currencyPair.base.toString().toLowerCase(),
                currencyPair.counter.toString().toLowerCase());
        return streamingService.subscribeChannel(channel)
                .map(message -> {
                    JsonNode data = message.get("tick");
                    Date ts = new Date(message.get("ts").longValue());
                    BigDecimal amount = data.get("amount").decimalValue();
                    BigDecimal open = data.get("open").decimalValue();
                    BigDecimal close = data.get("close").decimalValue();
                    BigDecimal high = data.get("high").decimalValue();
                    //Long id = data.get("id").longValue();
                    //Integer count = data.get("count").intValue();
                    BigDecimal low = data.get("low").decimalValue();
                    //BigDecimal vol = data.get("vol").decimalValue();
                    Ticker.Builder tickerBuilder = new Ticker.Builder();
                    tickerBuilder.currencyPair(currencyPair);
                    tickerBuilder.timestamp(ts);
                    tickerBuilder.volume(amount);
                    tickerBuilder.open(open);
                    tickerBuilder.last(close);
                    tickerBuilder.high(high);
                    tickerBuilder.low(low);
                    return tickerBuilder.build();
                });
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        String channel = String.format("market.%s%s.trade.detail",
                currencyPair.base.toString().toLowerCase(),
                currencyPair.counter.toString().toLowerCase());
        return streamingService.subscribeChannel(channel)
                .map(message -> {
                    JsonNode data = message.get("tick").get("data");
                    List<Trade> list = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        JsonNode json = data.get(i);
                        String direction = json.get("direction").textValue();
                        Order.OrderType orderType = null;
                        switch (direction) {
                            case "buy":
                                orderType = Order.OrderType.ASK;
                                break;
                            case "sell":
                                orderType = Order.OrderType.BID;
                                break;
                            default:
                                break;
                        }
                        if (orderType == null) {
                            return null;
                        }
                        BigDecimal price = json.get("price").decimalValue();
                        Date ts = new Date(json.get("ts").longValue());
                        String id = json.get("id").textValue();
                        BigDecimal amount = json.get("amount").decimalValue();
                        Trade trade = new Trade(orderType, amount, currencyPair, price, ts, id);
                        list.add(trade);
                    }
                    return list.get(list.size() - 1);
                });
    }
}
