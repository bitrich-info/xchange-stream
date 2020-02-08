package info.bitrich.xchangestream.okcoin;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.okcoin.OkCoinExchange;

public class OkCoinV3StreamingExchange extends OkCoinExchange implements StreamingExchange {
    private static final String API_URI = "wss://real.okcoin.com:10442/ws/v3";

    private final OkCoinV3StreamingService streamingService;
    private OkCoinV3StreamingMarketDataService streamingMarketDataService;

    public OkCoinV3StreamingExchange() {
        streamingService = new OkCoinV3StreamingService(API_URI);
    }

    protected OkCoinV3StreamingExchange(OkCoinV3StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @Override
    protected void initServices() {
        super.initServices();
        streamingMarketDataService = new OkCoinV3StreamingMarketDataService(streamingService);
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
    public void useCompressedMessages(boolean compressedMessages) { streamingService.useCompressedMessages(compressedMessages); }
}
