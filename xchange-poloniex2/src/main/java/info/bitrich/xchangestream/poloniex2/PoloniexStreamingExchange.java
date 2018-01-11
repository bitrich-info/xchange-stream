package info.bitrich.xchangestream.poloniex2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
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
        super.initServices();
        Map<CurrencyPair, Integer> currencyPairMap = getCurrencyPairMap();
        streamingMarketDataService = new PoloniexStreamingMarketDataService(streamingService, currencyPairMap);
    }

    private Map<CurrencyPair, Integer> getCurrencyPairMap() {
        Map<CurrencyPair, Integer> currencyPairMap = new HashMap<>();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            URL tickerUrl = new URL(TICKER_URL);
            JsonNode jsonRootTickers = mapper.readTree(tickerUrl);
            Iterator<String> pairSymbols = jsonRootTickers.fieldNames();
            while (pairSymbols.hasNext()) {
                String pairSymbol = pairSymbols.next();
                String id = jsonRootTickers.get(pairSymbol).get("id").toString();

                String[] currencies = pairSymbol.split("_");
                CurrencyPair currencyPair = new CurrencyPair(new Currency(currencies[1]), new Currency(currencies[0]));
                currencyPairMap.put(currencyPair, new Integer(id));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currencyPairMap;
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
}
