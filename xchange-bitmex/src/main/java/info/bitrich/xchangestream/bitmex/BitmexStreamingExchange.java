package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitmex.BitmexExchange;

/**
 * Created by Lukas Zaoralek on 12.11.17.
 */
public class BitmexStreamingExchange extends BitmexExchange implements StreamingExchange {
    private static final String API_URI = "wss://www.bitmex.com/realtime";
    private static final String API_SANDBOX_URI = "wss://testnet.bitmex.com/realtime";

    private BitmexStreamingService streamingService;
    private BitmexStreamingMarketDataService streamingMarketDataService;

    public BitmexStreamingExchange() {
    }

    protected BitmexStreamingExchange(BitmexStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @Override
    protected void initServices() {
        super.initServices();
        if (exchangeSpecification.getExchangeSpecificParametersItem(USE_SANDBOX).equals(true))
            this.streamingService = new BitmexStreamingService(API_SANDBOX_URI, exchangeSpecification);
        else
            this.streamingService = new BitmexStreamingService(API_URI, exchangeSpecification);

        streamingMarketDataService = new BitmexStreamingMarketDataService(streamingService);
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
    public ExchangeSpecification getDefaultExchangeSpecification() {
        ExchangeSpecification spec = super.getDefaultExchangeSpecification();
        spec.setShouldLoadRemoteMetaData(false);
        return spec;
    }

    @Override
    public StreamingMarketDataService getStreamingMarketDataService() {
        return streamingMarketDataService;
    }

    @Override
    public boolean isAlive() {
        return streamingService.isSocketOpen();
    }

    @Override
    public void useCompressedMessages(boolean compressedMessages) { streamingService.useCompressedMessages(compressedMessages); }

    public BitmexStreamingService getStreamingService() {
        return streamingService;
    }
}
