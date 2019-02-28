package info.bitrich.xchangestream.lykke;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.wamp.WampStreamingService;
import io.reactivex.Completable;
import org.knowm.xchange.lykke.LykkeExchange;

public class LykkeStreamingExchange extends LykkeExchange implements StreamingExchange {

    private static final String API_URI = "wss://wamp.lykke.com/ws/";
    private static final String API_REALM = "prices";

    private final WampStreamingService streamingService;
    private LykkeStreamingMarketDataService streamingMarketDataService;

    public LykkeStreamingExchange() {
        this.streamingService = new WampStreamingService(API_URI,API_REALM);
    }

    @Override
    protected void initServices() {
        super.initServices();
        streamingMarketDataService = new LykkeStreamingMarketDataService(streamingService);
    }

    @Override
    public Completable connect(ProductSubscription... args) {
        return streamingService.connect();
    }

    @Override
    public Completable disconnect() {
        return null;
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
