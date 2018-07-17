package info.bitrich.xchangestream.coindirect;

import info.bitrich.xchangestream.coindirect.dto.CoindirectUserToken;
import info.bitrich.xchangestream.coindirect.service.CoindirectStreamingMarketDataService;
import info.bitrich.xchangestream.coindirect.service.CoindirectTokenService;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.centrifugo.CentrifugoToken;
import io.reactivex.Completable;
import org.knowm.xchange.coindirect.Coindirect;
import org.knowm.xchange.coindirect.CoindirectExchange;

import java.io.IOException;
import java.util.Arrays;

public class CoindirectStreamingExchange extends CoindirectExchange implements StreamingExchange {

    private CoindirectStreamingService coindirectStreamingService = null;
    private CoindirectStreamingMarketDataService coindirectStreamingMarketDataService = null;
    private CoindirectUserToken coindirectUserToken = null;

    private CentrifugoToken getUserToken() {
        CoindirectTokenService coindirectTokenService = new CoindirectTokenService("https://user-token.coindirect.com");
        try {
            coindirectUserToken = coindirectTokenService.getUserToken();
            CentrifugoToken centrifugoToken = new CentrifugoToken(coindirectUserToken.user, coindirectUserToken.timestamp, coindirectUserToken.token);
            centrifugoToken.setUrl(coindirectUserToken.url);
            return centrifugoToken;
        } catch(IOException ignored) {
            return null;
        }
    }

    @Override
    public Completable connect(ProductSubscription... args) {
        CentrifugoToken centrifugoToken = getUserToken();
        coindirectStreamingService = new CoindirectStreamingService(centrifugoToken, Arrays.asList(args));
        coindirectStreamingMarketDataService = new CoindirectStreamingMarketDataService(coindirectStreamingService, this);
        return coindirectStreamingService.connect();
    }

    @Override
    public Completable disconnect() {
        return coindirectStreamingService.disconnect();
    }


    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public StreamingMarketDataService getStreamingMarketDataService() {
        return coindirectStreamingMarketDataService;
    }
    @Override
    public void useCompressedMessages(boolean compressedMessages) {

    }
}
