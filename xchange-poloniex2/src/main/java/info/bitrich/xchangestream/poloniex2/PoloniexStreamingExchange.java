package info.bitrich.xchangestream.poloniex2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.apache.commons.lang3.tuple.Pair;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.poloniex.PoloniexExchange;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Lukas Zaoralek on 10.11.17.
 */
public class PoloniexStreamingExchange extends PoloniexExchange implements StreamingExchange {
    private static final String API_URI = "wss://api2.poloniex.com";
    private static final String TICKER_URL = "https://poloniex.com/public?command=returnTicker";

    private final PoloniexStreamingService streamingService;
    private PoloniexStreamingMarketDataService streamingMarketDataService;

    public PoloniexStreamingExchange() {
        this.streamingService = new PoloniexStreamingService(API_URI);
    }

    @Override
    protected void initServices() {
        applyStreamingSpecification(getExchangeSpecification(), streamingService);
        super.initServices();
        Pair<Map<CurrencyPair, Integer>, Map<Integer, CurrencyPair>> currencyPairMaps = getCurrencyPairMap();
        streamingMarketDataService = new PoloniexStreamingMarketDataService(streamingService, currencyPairMaps);
    }

    private Pair<Map<CurrencyPair, Integer>, Map<Integer, CurrencyPair>> getCurrencyPairMap() {
        Map<CurrencyPair, Integer> currencyPairMap = new HashMap<>();
        Map<Integer, CurrencyPair> currencyIdMap = new HashMap<>();
        final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

        try {
            URL tickerUrl = new URL(TICKER_URL);
            JsonNode jsonRootTickers = mapper.readTree(tickerUrl);
            Iterator<String> pairSymbols = jsonRootTickers.fieldNames();
            while (pairSymbols.hasNext()) {
                String pairSymbol = pairSymbols.next();
                String id = jsonRootTickers.get(pairSymbol).get("id").toString();

                String[] currencies = pairSymbol.split("_");
                CurrencyPair currencyPair = new CurrencyPair(new Currency(currencies[1]), new Currency(currencies[0]));
                currencyPairMap.put(currencyPair, Integer.valueOf(id));
                currencyIdMap.put(Integer.valueOf(id), currencyPair);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Pair.of(currencyPairMap, currencyIdMap);
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
    public Observable<Object> connectionIdle() {
        return streamingService.subscribeIdle();
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
    
    @Override
    public Observable<Object> connectionSuccess() {
        return streamingService.subscribeConnectionSuccess();
    }
    @Override
    public Observable<Throwable> reconnectFailure() {
        return streamingService.subscribeReconnectFailure();
    }
}
