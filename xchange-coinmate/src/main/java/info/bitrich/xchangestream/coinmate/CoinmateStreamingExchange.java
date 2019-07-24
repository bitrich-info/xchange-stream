package info.bitrich.xchangestream.coinmate;

import com.pusher.client.PusherOptions;
import com.pusher.client.util.HttpAuthorizer;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.pusher.PusherStreamingService;
import io.reactivex.Completable;
import org.knowm.xchange.coinmate.CoinmateExchange;

import java.util.HashMap;
import java.util.Map;

public class CoinmateStreamingExchange extends CoinmateExchange implements StreamingExchange {
    private static final String API_KEY = "af76597b6b928970fbb0";
    private final PusherStreamingService streamingService;

    private CoinmateStreamingMarketDataService streamingMarketDataService;

    public CoinmateStreamingExchange() {
        HttpAuthorizer authorizer = new HttpAuthorizer("https://www.coinmate.io/api/pusherAuth");
        Map<String,String> headers = new HashMap<>();
        headers.put("","");

        authorizer.setHeaders(headers);
        PusherOptions options = new PusherOptions().setCluster("mt1").setAuthorizer(authorizer);
        streamingService = new PusherStreamingService(API_KEY,options);
    }

    @Override
    protected void initServices() {
        super.initServices();
        streamingMarketDataService = new CoinmateStreamingMarketDataService(streamingService,exchangeSpecification.getUserName());
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

    @Override
    public boolean isAlive() {
        return streamingService.isSocketOpen();
    }

    @Override
    public void useCompressedMessages(boolean compressedMessages) { streamingService.useCompressedMessages(compressedMessages); }
}
