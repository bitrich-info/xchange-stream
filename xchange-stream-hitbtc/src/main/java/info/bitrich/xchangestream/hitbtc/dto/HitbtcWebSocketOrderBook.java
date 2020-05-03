package info.bitrich.xchangestream.hitbtc.dto;

import static java.util.Collections.reverseOrder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcOrderBook;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcOrderLimit;

/** Created by Pavel Chertalev on 15.03.2018. */
public class HitbtcWebSocketOrderBook {
  private Map<BigDecimal, HitbtcOrderLimit> asks;
  private Map<BigDecimal, HitbtcOrderLimit> bids;
  private long sequence = 0;
  private long timestamp = 0;

  public HitbtcWebSocketOrderBook(HitbtcWebSocketOrderBookTransaction orderBookTransaction) {
    createFromLevels(orderBookTransaction);
  }

  private void createFromLevels(HitbtcWebSocketOrderBookTransaction orderBookTransaction) {
    this.asks = new TreeMap<>(BigDecimal::compareTo);
    this.bids = new TreeMap<>(reverseOrder(BigDecimal::compareTo));

    for (HitbtcOrderLimit orderBookItem : orderBookTransaction.getParams().getAsk()) {
      if (orderBookItem.getSize().signum() != 0) {
        asks.put(orderBookItem.getPrice(), orderBookItem);
      }
    }

    for (HitbtcOrderLimit orderBookItem : orderBookTransaction.getParams().getBid()) {
      if (orderBookItem.getSize().signum() != 0) {
        bids.put(orderBookItem.getPrice(), orderBookItem);
      }
    }

    sequence = orderBookTransaction.getParams().getSequence();
    timestamp = ZonedDateTime.parse(orderBookTransaction.getParams().getTimestamp()).toInstant().toEpochMilli();
  }

  public HitbtcOrderBook toHitbtcOrderBook() {
    HitbtcOrderLimit[] asks = this.asks.values().toArray(new HitbtcOrderLimit[0]);
    HitbtcOrderLimit[] bids = this.bids.values().toArray(new HitbtcOrderLimit[0]);

    return new HitbtcOrderBookWithTimestamp(new Date(timestamp), asks, bids);
  }

  public void updateOrderBook(HitbtcWebSocketOrderBookTransaction orderBookTransaction) {
    if (orderBookTransaction.getParams().getSequence() <= sequence) {
      return;
    }
    updateOrderBookItems(orderBookTransaction.getParams().getAsk(), asks);
    updateOrderBookItems(orderBookTransaction.getParams().getBid(), bids);
    sequence = orderBookTransaction.getParams().getSequence();
    timestamp = ZonedDateTime.parse(orderBookTransaction.getParams().getTimestamp()).toInstant().toEpochMilli();
  }

  private void updateOrderBookItems(
      HitbtcOrderLimit[] itemsToUpdate, Map<BigDecimal, HitbtcOrderLimit> localItems) {
    for (HitbtcOrderLimit itemToUpdate : itemsToUpdate) {
      localItems.remove(itemToUpdate.getPrice());
      if (itemToUpdate.getSize().signum() != 0) {
        localItems.put(itemToUpdate.getPrice(), itemToUpdate);
      }
    }
  }
}
