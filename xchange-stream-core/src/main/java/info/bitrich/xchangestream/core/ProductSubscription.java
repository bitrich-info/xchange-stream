package info.bitrich.xchangestream.core;

import org.knowm.xchange.currency.CurrencyPair;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukas Zaoralek on 24.11.17.
 */
public class ProductSubscription {
  private List<CurrencyPair> orderbook;
  private List<CurrencyPair> trades;
  private List<CurrencyPair> ticker;

  private ProductSubscription(ProductSubscriptionBuilder builder) {
    this.orderbook = builder.orderbook;
    this.trades = builder.trades;
    this.ticker = builder.ticker;
  }

  public CurrencyPair[] getOrderbook() {
    return orderbook.toArray(new CurrencyPair[orderbook.size()]);
  }

  public CurrencyPair[] getTrades() {
    return trades.toArray(new CurrencyPair[trades.size()]);
  }

  public CurrencyPair[] getTicker() {
    return ticker.toArray(new CurrencyPair[ticker.size()]);
  }

  public static ProductSubscriptionBuilder create() {
    return new ProductSubscriptionBuilder();
  }

  public static class ProductSubscriptionBuilder {
    private List<CurrencyPair> orderbook;
    private List<CurrencyPair> trades;
    private List<CurrencyPair> ticker;

    private ProductSubscriptionBuilder() {
      orderbook = new ArrayList<>();
      trades = new ArrayList<>();
      ticker = new ArrayList<>();
    }

    public ProductSubscriptionBuilder addOrderbook(CurrencyPair pair) {
      orderbook.add(pair);
      return this;
    }

    public ProductSubscriptionBuilder addTrades(CurrencyPair pair) {
      trades.add(pair);      
      return this;
    }

    public ProductSubscriptionBuilder addTicker(CurrencyPair pair) {
      ticker.add(pair);
      return this;    
    }

    public ProductSubscriptionBuilder addAll(CurrencyPair pair) {
      orderbook.add(pair);
      trades.add(pair);
      ticker.add(pair);
      return this;
    }

    public ProductSubscription build() {
      return new ProductSubscription(this);
    }
  }
}
