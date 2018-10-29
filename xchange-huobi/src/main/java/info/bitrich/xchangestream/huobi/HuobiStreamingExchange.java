package info.bitrich.xchangestream.huobi;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
import org.knowm.xchange.huobi.HuobiExchange;

public class HuobiStreamingExchange extends HuobiExchange implements StreamingExchange {

    private static final String API_BASE_URI = "wss://api.huobi.pro/ws";

    private HuobiStreamingService streamingService;
    private HuobiStreamingMarketDataService streamingMarketDataService;

    public HuobiStreamingExchange() {
        this.streamingService = new HuobiStreamingService(API_BASE_URI);
        this.streamingService.useCompressedMessages(true);
    }

    @Override
    protected void initServices() {
        super.initServices();
        streamingMarketDataService = new HuobiStreamingMarketDataService(streamingService);
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
        return streamingMarketDataService;
    }

    @Override
    public void useCompressedMessages(boolean compressedMessages) {
        streamingService.useCompressedMessages(compressedMessages);
    }
}
