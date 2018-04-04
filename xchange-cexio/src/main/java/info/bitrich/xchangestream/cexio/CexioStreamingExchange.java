package info.bitrich.xchangestream.cexio;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.core.StreamingPrivateDataService;
import io.reactivex.Completable;
import org.knowm.xchange.cexio.CexIOExchange;

public class CexioStreamingExchange extends CexIOExchange implements StreamingExchange {

    private static final String API_URI = "wss://ws.cex.io/ws/";

    private final CexioStreamingMarketDataService streamingMarketDataService;
    private final CexioStreamingPrivateDataRawService streamingPrivateDataService;

    public CexioStreamingExchange() {
        this.streamingPrivateDataService = new CexioStreamingPrivateDataRawService(API_URI);
        this.streamingMarketDataService = new CexioStreamingMarketDataService();
    }

    @Override
    public Completable connect(ProductSubscription... args) {
        return streamingPrivateDataService.connect();
    }

    @Override
    public Completable disconnect() {
        return streamingPrivateDataService.disconnect();
    }

    @Override
    public boolean isAlive() {
        return streamingPrivateDataService.isSocketOpen();
    }

    @Override
    public StreamingMarketDataService getStreamingMarketDataService() {
        return streamingMarketDataService;
    }

    @Override
    public StreamingPrivateDataService getStreamingPrivateDataService() {
        return streamingPrivateDataService;
    }

    @Override
    public void useCompressedMessages(boolean compressedMessages) {
        streamingPrivateDataService.useCompressedMessages(compressedMessages);
    }

    public void setCredentials(String apiKey, String apiSecret) {
        streamingPrivateDataService.setApiKey(apiKey);
        streamingPrivateDataService.setApiSecret(apiSecret);
    }
}
