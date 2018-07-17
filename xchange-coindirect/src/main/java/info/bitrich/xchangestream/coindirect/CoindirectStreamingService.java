package info.bitrich.xchangestream.coindirect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import info.bitrich.xchangestream.coindirect.dto.CoindirectUserToken;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.service.centrifugo.CentrifugoToken;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.centrifugo.CentrifugoStreamingService;
import io.reactivex.Completable;
import org.knowm.xchange.coindirect.CoindirectAdapters;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static info.bitrich.xchangestream.coindirect.CoindirectStreamingService.StreamType.BOOK;
import static info.bitrich.xchangestream.coindirect.CoindirectStreamingService.StreamType.TICKER;

public class CoindirectStreamingService extends CentrifugoStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(CoindirectStreamingService.class);

    List<ProductSubscription> subscriptionList;
    private CoindirectUserToken coindirectUserToken;

    public CoindirectStreamingService(CentrifugoToken centrifugoToken, List<ProductSubscription> subscriptionList) {
        super(centrifugoToken);
        this.subscriptionList = subscriptionList;
    }

    public void subscribe(String symbol, StreamType streamType) {
        switch(streamType) {
            case BOOK:
                super.subscribeChannel("book-"+symbol);
                break;
            case TICKER:
                super.subscribeChannel("ticker-"+symbol);
                break;
        }

    }


    public enum StreamType {
        BOOK, TICKER
    }
}
