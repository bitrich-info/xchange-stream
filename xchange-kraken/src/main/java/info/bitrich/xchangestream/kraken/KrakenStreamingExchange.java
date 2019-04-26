package info.bitrich.xchangestream.kraken;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.kraken.KrakenExchange;

public class KrakenStreamingExchange extends KrakenExchange implements StreamingExchange {
    private static final String API_URI = "wss://ws.kraken.com";

    private final KrakenStreamingService streamingService;
    private KrakenStreamingMarketDataService streamingMarketDataService;

    public KrakenStreamingExchange() {
        this.streamingService = new KrakenStreamingService(API_URI);
    }

    @Override
    protected void initServices() {
        super.initServices();
        streamingMarketDataService = new KrakenStreamingMarketDataService(streamingService);
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
    public Observable<Throwable> reconnectFailure() {
        return streamingService.subscribeReconnectFailure();
    }

    @Override
    public Observable<Object> connectionSuccess() {
        return streamingService.subscribeConnectionSuccess();
    }

    @Override
    public StreamingMarketDataService getStreamingMarketDataService() {
        return streamingMarketDataService;
    }

    @Override
    public void useCompressedMessages(boolean compressedMessages) {

    }

}
