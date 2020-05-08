package info.bitrich.xchangestream.kraken;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class KrakenRateLimitExceedTest {
  private static final Logger LOG = LoggerFactory.getLogger(KrakenRateLimitExceedTest.class);
  private static final Random RANDOM = new Random(System.currentTimeMillis());

  public static void main(String[] args) throws InterruptedException {
    ExchangeSpecification exchangeSpecification =
        new ExchangeSpecification(KrakenStreamingExchange.class);

    StreamingExchange krakenExchange =
        StreamingExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
    krakenExchange.connect().blockingAwait();

    StreamingMarketDataService krakenStreamingMarketDataSerice =
        krakenExchange.getStreamingMarketDataService();
    List<Disposable> disposables = new ArrayList<>();

    Map<CurrencyPair, CurrencyPairMetaData> exchangePairs =
        krakenExchange.getExchangeMetaData().getCurrencyPairs();
    Set<CurrencyPair> subscribedPairs = new ConcurrentSkipListSet<>();
    Set<CurrencyPair> receivedPairs = new ConcurrentSkipListSet<>();
    AtomicInteger retries = new AtomicInteger(0);

    for (CurrencyPair pair : krakenExchange.getExchangeMetaData().getCurrencyPairs().keySet()) {
      Disposable orderBookDisposable =
          krakenStreamingMarketDataSerice
              .getOrderBook(pair, 100)
              .retryWhen(
                  errors ->
                      errors
                          .zipWith(Observable.range(1, 5), (n, i) -> i)
                          .flatMap(
                              retryCount -> {
                                long secondsDelay =
                                    (long)
                                        (Math.pow(2, retryCount)
                                            * (0.85 + RANDOM.nextDouble() % 0.3f));
                                LOG.info(
                                    "Retrying subscription of pair {} after {} seconds, retry {}/5",
                                    pair,
                                    secondsDelay,
                                    retryCount);
                                retries.addAndGet(1);
                                return Observable.timer(secondsDelay, TimeUnit.SECONDS);
                              }))
              .subscribe(
                  s -> {
                    LOG.info(
                        "Received order book {}({},{}) ask[0] = {} bid[0] = {}",
                        pair,
                        s.getAsks().size(),
                        s.getBids().size(),
                        s.getAsks().get(0),
                        s.getBids().get(0));
                    receivedPairs.add(pair);
                  },
                  throwable -> {
                    LOG.error("Order book {} FAILED {}", pair, throwable.getMessage(), throwable);
                  },
                  () -> {
                  },
                  c -> {
                    LOG.info("Successfully subscribed to order book for {}", pair);
                    subscribedPairs.add(pair);
                  });

      disposables.add(orderBookDisposable);
    }

    TimeUnit.SECONDS.sleep(60);

    if (subscribedPairs.size() != exchangePairs.size()) {
      LOG.error(
          "Subscribed to only {} pairs of all {}, even after a total of {} retries",
          subscribedPairs.size(),
          exchangePairs.size(),
          retries);
    } else {
      LOG.info(
          "Successfully subscribed to all {} pairs, after a total of {} retries",
          subscribedPairs.size(),
          retries);
    }
    if (receivedPairs.size() != exchangePairs.size()) {
      LOG.error(
          "Received order books for only {} pairs of all {}, even after a total of {} retries",
          receivedPairs.size(),
          exchangePairs.size(),
          retries);
    } else {
      LOG.info(
          "Successfully received order books for all {} pairs, after a total of {} retries",
          receivedPairs.size(),
          retries);
    }

    disposables.forEach(Disposable::dispose);
    krakenExchange.disconnect().subscribe(() -> LOG.info("Disconnected"));
  }

  @Test
  public void testSubscribeWithTimeout() throws Exception {
    CurrencyPair currencyPair = CurrencyPair.BTC_USD;

    KrakenStreamingExchange krakenStreamingExchange = ExchangeFactory.INSTANCE.createExchange(KrakenStreamingExchangeMock.class);
    krakenStreamingExchange
        .connect()
        .blockingAwait();
    StreamingMarketDataService krakenStreamingMarketDataService = krakenStreamingExchange.getStreamingMarketDataService();

    Integer timeout = (Integer) FieldUtils.readStaticField(KrakenStreamingService.class, "DEFAULT_SUBSCRIBE_WAIT_TIME", true);
    if (timeout == null) {
      fail("Unable to read the 'subscribe wait time' field from Kraken streaming service class");
    }

    AtomicBoolean isOnNextCalled = new AtomicBoolean(false);
    AtomicBoolean isOnErrorCalled = new AtomicBoolean(false);

    Disposable disposable = krakenStreamingMarketDataService.getOrderBook(currencyPair)
        .subscribe(
            s -> {
              LOG.error("Received order book, which should not happen");
              isOnNextCalled.set(true);
            },
            throwable -> {
              LOG.error("Order book FAILED {}", throwable.getMessage(), throwable);
              isOnErrorCalled.set(true);
            },
            () -> {
            },
            c -> {
            });

    try {
      TimeUnit.SECONDS.sleep(timeout * 2);

      assertFalse("onNext() was called during unsuccessful subscribe attempt", isOnNextCalled.get());
      assertTrue("onError() was not called after subscribe timeout", isOnErrorCalled.get());
    } finally {
      disposable.dispose();
    }
  }

  @Test
  public void testSubscribeDisposeBeforeTimeout() throws Exception {
    CurrencyPair currencyPair = CurrencyPair.BTC_USD;

    KrakenStreamingExchange krakenStreamingExchange = ExchangeFactory.INSTANCE.createExchange(KrakenStreamingExchangeMock.class);
    krakenStreamingExchange
        .connect()
        .blockingAwait();
    StreamingMarketDataService krakenStreamingMarketDataService = krakenStreamingExchange.getStreamingMarketDataService();

    Integer timeout = (Integer) FieldUtils.readStaticField(KrakenStreamingService.class, "DEFAULT_SUBSCRIBE_WAIT_TIME", true);
    if (timeout == null) {
      fail("Unable to read the 'subscribe wait time' field from Kraken streaming service class");
    }

    AtomicBoolean isOnNextCalled = new AtomicBoolean(false);
    AtomicBoolean isOnErrorCalled = new AtomicBoolean(false);

    Disposable disposable = krakenStreamingMarketDataService.getOrderBook(currencyPair)
        .subscribe(
            s -> {
              LOG.error("Received order book, which should not happen");
              isOnNextCalled.set(true);
            },
            throwable -> {
              LOG.error("Order book FAILED {}", throwable.getMessage(), throwable);
              isOnErrorCalled.set(true);
            },
            () -> {
            },
            c -> {
            });

    disposable.dispose();
    TimeUnit.SECONDS.sleep(timeout * 2);

    assertFalse("onNext() was called after subscribe timeout", isOnNextCalled.get());
    assertFalse("onError() was called after subscribe timeout", isOnErrorCalled.get());
  }

  @Test
  public void testSubscribeDisconnectBeforeTimeout() throws Exception {
    CurrencyPair currencyPair = CurrencyPair.BTC_USD;

    KrakenStreamingExchange krakenStreamingExchange = ExchangeFactory.INSTANCE.createExchange(KrakenStreamingExchangeMock.class);
    krakenStreamingExchange
        .connect()
        .blockingAwait();
    StreamingMarketDataService krakenStreamingMarketDataService = krakenStreamingExchange.getStreamingMarketDataService();

    Integer timeout = (Integer) FieldUtils.readStaticField(KrakenStreamingService.class, "DEFAULT_SUBSCRIBE_WAIT_TIME", true);
    if (timeout == null) {
      fail("Unable to read the 'subscribe wait time' field from Kraken streaming service class");
    }

    AtomicBoolean isOnNextCalled = new AtomicBoolean(false);
    AtomicBoolean isOnErrorCalled = new AtomicBoolean(false);

    Disposable disposable = krakenStreamingMarketDataService.getOrderBook(currencyPair)
        .subscribe(
            s -> {
              LOG.error("Received order book, which should not happen");
              isOnNextCalled.set(true);
            },
            throwable -> {
              LOG.error("Order book FAILED {}", throwable.getMessage(), throwable);
              isOnErrorCalled.set(true);
            },
            () -> {
            },
            c -> {
            });

    disposable.dispose();
    krakenStreamingExchange.disconnect().blockingAwait();
    TimeUnit.SECONDS.sleep(timeout * 2);

    assertFalse("onNext() was called after subscribe timeout", isOnNextCalled.get());
    assertFalse("onError() was called after subscribe timeout", isOnErrorCalled.get());
  }
}
