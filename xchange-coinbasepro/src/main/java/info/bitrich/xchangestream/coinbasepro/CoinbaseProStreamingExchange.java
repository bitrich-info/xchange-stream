package info.bitrich.xchangestream.coinbasepro;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.WebSocketClientHandler;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.coinbasepro.CoinbaseProExchange;
import org.knowm.xchange.coinbasepro.dto.account.CoinbaseProWebsocketAuthData;
import org.knowm.xchange.coinbasepro.service.CoinbaseProAccountServiceRaw;

/**
 * CoinbasePro Streaming Exchange. Connects to live WebSocket feed.
 */
public class CoinbaseProStreamingExchange extends CoinbaseProExchange implements StreamingExchange {
    private static final String API_URI = "wss://ws-feed.pro.coinbase.com";

    private CoinbaseProStreamingService streamingService;
    private CoinbaseProStreamingMarketDataService streamingMarketDataService;

    public CoinbaseProStreamingExchange() { }

    @Override
    protected void initServices() {
        super.initServices();
    }

    @Override
    public Completable connect(ProductSubscription... args) {
        if (args == null || args.length == 0)
            throw new UnsupportedOperationException("The ProductSubscription must be defined!");
        ExchangeSpecification exchangeSpec = getExchangeSpecification();
        this.streamingService = new CoinbaseProStreamingService(API_URI, () -> authData(exchangeSpec));
        this.streamingMarketDataService = new CoinbaseProStreamingMarketDataService(streamingService);
        streamingService.subscribeMultipleCurrencyPairs(args);

        return streamingService.connect();
    }

    private CoinbaseProWebsocketAuthData authData(ExchangeSpecification exchangeSpec) {
        CoinbaseProWebsocketAuthData authData = null;
        if ( exchangeSpec.getApiKey() != null ) {
            try {
                CoinbaseProAccountServiceRaw rawAccountService = (CoinbaseProAccountServiceRaw) getAccountService();
                authData = rawAccountService.getWebsocketAuthData();
            }
            catch (Exception e) {
                logger.warn("Failed attempting to acquire Websocket AuthData needed for private data on" +
                            " websocket.  Will only receive public information via API", e);
            }
        }
        return authData;
    }

    @Override
    public Completable disconnect() {
        CoinbaseProStreamingService service = streamingService;
        streamingService = null;
        streamingMarketDataService = null;
        return service.disconnect();
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
    public ExchangeSpecification getDefaultExchangeSpecification() {
        ExchangeSpecification spec = super.getDefaultExchangeSpecification();
        spec.setShouldLoadRemoteMetaData(false);

        return spec;
    }

    @Override
    public StreamingMarketDataService getStreamingMarketDataService() {
        return streamingMarketDataService;
    }

    /**
     * Enables the user to listen on channel inactive events and react appropriately.
     *
     * @param channelInactiveHandler a WebSocketMessageHandler instance.
     */
    public void setChannelInactiveHandler(WebSocketClientHandler.WebSocketMessageHandler channelInactiveHandler) {
        streamingService.setChannelInactiveHandler(channelInactiveHandler);
    }

    @Override
    public boolean isAlive() {
        return streamingService != null && streamingService.isSocketOpen();
    }

    @Override
    public void useCompressedMessages(boolean compressedMessages) { streamingService.useCompressedMessages(compressedMessages); }

    public CoinbaseProStreamingService getStreamingService() {
        return streamingService;
    }
}
