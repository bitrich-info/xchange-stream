package info.bitrich.xchangestream.gdax.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.ProductSubscription;
import org.junit.Assert;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;

/**
 * Created by luca on 5/3/17.
 */
public class GDAXWebSocketSubscriptionMessageTest {

  @Test
  public void testWebSocketMessageSerialization() throws JsonProcessingException {

    ProductSubscription productSubscription = ProductSubscription.create().addOrderbook(CurrencyPair.BTC_USD)
            .addTrades(CurrencyPair.BTC_USD).addTicker(CurrencyPair.BTC_USD).build();
    GDAXWebSocketSubscriptionMessage message = new GDAXWebSocketSubscriptionMessage("subscribe", productSubscription);

    final ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    
    String serialized = mapper.writeValueAsString(message);

    Assert.assertEquals("{\"type\":\"subscribe\",\"channels\":[{\"name\":\"matches\",\"product_ids\":[\"BTC-USD\"]},{\"name\":\"ticker\",\"product_ids\":[\"BTC-USD\"]},{\"name\":\"level2\",\"product_ids\":[\"BTC-USD\"]}]}", serialized);
  }
}
