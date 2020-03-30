package info.bitrich.xchangestream.kraken;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;

public class KrakenStreamingExchangeMock extends KrakenStreamingExchange {
  @Override
  protected void initServices() {
    super.initServices();

    try {
      String uri = (String) FieldUtils.readField(this, "API_URI", true);

      KrakenStreamingService krakenStreamingService = spy(new KrakenStreamingService(false, uri));
      Mockito.doNothing().when(krakenStreamingService).sendMessage(anyString());

      KrakenStreamingMarketDataService krakenStreamingMarketDataService = new KrakenStreamingMarketDataService(krakenStreamingService);

      FieldUtils.writeField(this, "streamingService", krakenStreamingService, true);
      FieldUtils.writeField(this, "streamingMarketDataService", krakenStreamingMarketDataService, true);
    } catch (Exception e) {
      logger.error("Failed to mock Kraken streaming service", e);
    }
  }
}
