package info.bitrich.xchangestream.bitmex.dto;

import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static info.bitrich.xchangestream.bitmex.dto.BitmexLimitOrder.ASK_SIDE;

/**
 * Created by Lukas Zaoralek on 13.11.17.
 */
public class BitmexOrderbook {
    private Map<String, BitmexLimitOrder> asks;
    private Map<String, BitmexLimitOrder> bids;

    public BitmexOrderbook() {
        this.asks = new ConcurrentHashMap<>();
        this.bids = new ConcurrentHashMap<>();
    }

    public BitmexOrderbook(BitmexLimitOrder[] levels) {
        this();
        createFromLevels(levels);
    }

    private void createFromLevels(BitmexLimitOrder[] levels) {
        for (BitmexLimitOrder level : levels) {
            Map<String, BitmexLimitOrder> orderBookSide = level.getSide().equals(ASK_SIDE) ? asks : bids;
            orderBookSide.put(level.getId(), level);
        }
    }

    public void updateLevels(BitmexLimitOrder[] levels, String action) {
        for (BitmexLimitOrder level : levels) {
            updateLevel(level, action);
        }
    }

    private void updateLevel(BitmexLimitOrder level, String action) {
        Map<String, BitmexLimitOrder> orderBookSide = level.getSide().equals(ASK_SIDE) ? asks : bids;
        String id = level.getId();

        if (action.equals("insert")) {
            orderBookSide.put(id, level);
        } else if (action.equals("delete")) {
            orderBookSide.remove(id);
        } else if (action.equals("update")) {
            BitmexLimitOrder existing = orderBookSide.get(id);
            if (existing != null) {
                BitmexLimitOrder modified = new BitmexLimitOrder(level.getSymbol(), id, level.getSide(),
                        existing.getPrice(), level.getSize()); // update action's data doesn't have price
                orderBookSide.put(id, modified);
            }
        }
    }

    private List<LimitOrder> toLimitOrders(Map<String, BitmexLimitOrder> orderMap) {
        if (orderMap == null || orderMap.size() == 0) {
            return Collections.emptyList();
        }
        int length = orderMap.size();
        List<LimitOrder> limitOrders = new ArrayList<>(length);
        BitmexLimitOrder[] levelOrders = orderMap.values().toArray(new BitmexLimitOrder[length]);
        for (BitmexLimitOrder level: levelOrders) {
            limitOrders.add(level.toLimitOrder());
        }
        Collections.sort(limitOrders);
        return limitOrders;
    }

    public OrderBook toOrderbook() {
        List<LimitOrder> orderbookAsks = toLimitOrders(asks);
        List<LimitOrder> orderbookBids = toLimitOrders(bids);
        return new OrderBook(null, orderbookAsks, orderbookBids);
    }

}
