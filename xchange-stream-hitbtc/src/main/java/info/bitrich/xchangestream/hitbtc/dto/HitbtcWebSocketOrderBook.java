package info.bitrich.xchangestream.hitbtc.dto;

import static java.util.Collections.reverseOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcOrderBook;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcOrderLimit;

/** Created by Pavel Chertalev on 15.03.2018. */
public class HitbtcWebSocketOrderBook {
  // To parse dates in format of '2018-11-19T05:00:28.700Z'
  private static DateTimeFormatter DATE_FORMAT =
          new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").toFormatter();
  private Map<BigDecimal, HitbtcOrderLimit> asks;
  private Map<BigDecimal, HitbtcOrderLimit> bids;
  private long sequence = 0;
  private long timestamp = 0;

  public HitbtcWebSocketOrderBook(HitbtcWebSocketOrderBookTransaction orderbookTransaction) {
    createFromLevels(orderbookTransaction);
  }

  private void createFromLevels(HitbtcWebSocketOrderBookTransaction orderbookTransaction) {
    this.asks = new TreeMap<>(BigDecimal::compareTo);
    this.bids = new TreeMap<>(reverseOrder(BigDecimal::compareTo));

    for (HitbtcOrderLimit orderBookItem : orderbookTransaction.getParams().getAsk()) {
      if (orderBookItem.getSize().signum() != 0) {
        asks.put(orderBookItem.getPrice(), orderBookItem);
      }
    }

    for (HitbtcOrderLimit orderBookItem : orderbookTransaction.getParams().getBid()) {
      if (orderBookItem.getSize().signum() != 0) {
        bids.put(orderBookItem.getPrice(), orderBookItem);
      }
    }

    sequence = orderbookTransaction.getParams().getSequence();
    timestamp = parseISOTimestampInUTC(orderbookTransaction.getParams().getTimestamp());
  }

  private long parseISOTimestampInUTC(String timestamp) {
    if (timestamp != null) {
        LocalDateTime time = LocalDateTime.parse(timestamp, DATE_FORMAT);
        return time.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
    return 0L;
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
    timestamp = parseISOTimestampInUTC(orderBookTransaction.getParams().getTimestamp());
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
