package info.bitrich.xchangestream.bitstamp;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.bitstamp.v2.dto.BitstampWebSocketOrderEvent;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.observers.TestObserver;
import java.util.List;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;

public class BitstampStreamingMarketDataServiceBaseTest {

  protected ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  protected void validateTrades(Trade expected, TestObserver<Trade> test) {
    test.assertValue(
        trade1 -> {
          assertThat(trade1.getId()).as("Id").isEqualTo(expected.getId());
          assertThat(trade1.getCurrencyPair())
              .as("Currency pair")
              .isEqualTo(expected.getCurrencyPair());
          assertThat(trade1.getPrice()).as("Price").isEqualTo(expected.getPrice());
          // assertThat(trade1.getTimestamp()).as("Timestamp").isEqualTo(expected.getTimestamp());
          assertThat(trade1.getOriginalAmount())
              .as("Amount")
              .isEqualTo(expected.getOriginalAmount());
          assertThat(trade1.getType()).as("Type").isEqualTo(expected.getType());
          return true;
        });
  }

    protected void validateTrades(BitstampWebSocketOrderEvent expected, TestObserver<BitstampWebSocketOrderEvent> test) {
        test.assertValue(
                actual -> {
                    assertThat(actual.getData().getOrderType()).as("OrderType")
                            .isEqualTo(expected.getData().getOrderType());
                    assertThat(actual.getData().getPrice()).as("Price")
                            .isEqualTo(expected.getData().getPrice());
                    assertThat(actual.getData().getDateTime()).as("DateTime")
                            .isEqualTo(expected.getData().getDateTime());
                    assertThat(actual.getData().getAmount()).as("Amount")
                            .isEqualTo(expected.getData().getAmount());
                    assertThat(actual.getData().getIdStr()).as("IdStr")
                            .isEqualTo(expected.getData().getIdStr());
                    assertThat(actual.getData().getAmountStr()).as("AmountStr")
                            .isEqualTo(expected.getData().getAmountStr());
                    assertThat(actual.getData().getPriceStr()).as("PriceStr")
                            .isEqualTo(expected.getData().getPriceStr());
                    assertThat(actual.getData().getId()).as("Id")
                            .isEqualTo(expected.getData().getId());
                    assertThat(actual.getData().getMicroTimestamp()).as("MicroTimestamp")
                            .isEqualTo(expected.getData().getMicroTimestamp());
                    assertThat(actual.getEvent()).as("Event")
                            .isEqualTo(expected.getEvent());
                    assertThat(actual.getChannel()).as("Channel")
                            .isEqualTo(expected.getChannel());
                    return true;
                });
    }

  protected void validateOrderBook(
      List<LimitOrder> bids, List<LimitOrder> asks, TestObserver<OrderBook> test) {
    test.assertValue(
        orderBook1 -> {
          assertThat(orderBook1.getAsks()).as("Asks").isEqualTo(asks);
          assertThat(orderBook1.getBids()).as("Bids").isEqualTo(bids);
          return true;
        });
  }
}
