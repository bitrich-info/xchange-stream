package info.bitrich.xchangestream.kraken;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                  () -> {},
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
}
