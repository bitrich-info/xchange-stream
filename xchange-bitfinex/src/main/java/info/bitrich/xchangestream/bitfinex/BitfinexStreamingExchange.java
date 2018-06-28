package info.bitrich.xchangestream.bitfinex;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;

/**
 * Created by Lukas Zaoralek on 7.11.17.
 */
public class BitfinexStreamingExchange extends BitfinexExchange implements StreamingExchange {
    private static final String API_URI = "wss://api.bitfinex.com/ws/2";

    private final BitfinexStreamingService streamingService;
    private BitfinexStreamingMarketDataService streamingMarketDataService;
    private BitfinexStreamingRawService streamingAuthenticatedDataService;

    public BitfinexStreamingExchange() {
        this.streamingService = new BitfinexStreamingService(API_URI);
        this.streamingAuthenticatedDataService = new BitfinexStreamingRawService(API_URI);
    }

    @Override
    protected void initServices() {
        super.initServices();
        this.streamingMarketDataService = new BitfinexStreamingMarketDataService(streamingService);
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
    public void useCompressedMessages(boolean compressedMessages) { streamingService.useCompressedMessages(compressedMessages); }

    public Completable connectToAuthenticated() {
        return streamingAuthenticatedDataService.connect();
    }

    public void authenticate() {
        streamingAuthenticatedDataService.auth();
    }

    public Completable disconnectToAuthenticated() {
        return streamingAuthenticatedDataService.disconnect();
    }

    public boolean isAuthenticatedAlive() {
        return streamingAuthenticatedDataService.isSocketOpen();
    }

    public void setCredentials(String apiKey, String apiSecret) {
        streamingAuthenticatedDataService.setApiKey(apiKey);
        streamingAuthenticatedDataService.setApiSecret(apiSecret);
    }

    public BitfinexStreamingRawService getStreamingAuthenticatedDataService() {
        return streamingAuthenticatedDataService;
    }
}
