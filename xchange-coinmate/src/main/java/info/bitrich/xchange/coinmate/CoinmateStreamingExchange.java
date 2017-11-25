package info.bitrich.xchange.coinmate;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.pusher.PusherStreamingService;
import io.reactivex.Completable;
import org.knowm.xchange.coinmate.CoinmateExchange;

public class CoinmateStreamingExchange extends CoinmateExchange implements StreamingExchange {
    private static final String API_KEY = "af76597b6b928970fbb0";
    private final PusherStreamingService streamingService;

    private CoinmateStreamingMarketDataService streamingMarketDataService;

    public CoinmateStreamingExchange() {
        streamingService = new PusherStreamingService(API_KEY);
    }

    @Override
    protected void initServices() {
        super.initServices();
        streamingMarketDataService = new CoinmateStreamingMarketDataService(streamingService);
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
    public StreamingMarketDataService getStreamingMarketDataService() {
        return streamingMarketDataService;
    }
}
