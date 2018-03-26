package info.bitrich.xchangestream.cexio;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
import org.knowm.xchange.cexio.CexIOExchange;

public class CexioStreamingExchange extends CexIOExchange implements StreamingExchange {

    private static final String API_URI = "wss://ws.cex.io/ws/";
    private final CexioStreamingService streamingService;

    public CexioStreamingExchange() {
        this.streamingService = new CexioStreamingService(API_URI);
    }

    @Override
    public Completable connect(ProductSubscription... args) {
        return streamingService.connect();
    }

    @Override
    public Completable disconnect() {
        return streamingService.disconnect();
    }

    @Override
    public boolean isAlive() {
        return streamingService.isSocketOpen();
    }

    @Override
    public StreamingMarketDataService getStreamingMarketDataService() {
        return null;
    }

    public void setCredentials(String apiKey, String apiSecret) {
        streamingService.setApiKey(apiKey);
        streamingService.setApiSecret(apiSecret);
    }

    public CexioStreamingService getStreamingService() {
        return streamingService;
    }

}
