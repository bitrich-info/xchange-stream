package info.bitrich.xchangestream.bitmex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.bitmex.dto.*;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import org.knowm.xchange.bitmex.BitmexContract;
import org.knowm.xchange.bitmex.BitmexPrompt;
import org.knowm.xchange.bitmex.BitmexUtils;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by Lukas Zaoralek on 13.11.17.
 */
public class BitmexStreamingMarketDataService implements StreamingMarketDataService {
    private static final Logger LOG = LoggerFactory.getLogger(BitmexStreamingMarketDataService.class);

    private final ObjectMapper objectMapper = StreamingObjectMapperHelper.getObjectMapper();

    private final BitmexStreamingService streamingService;

    private final SortedMap<String, BitmexOrderbook> orderbooks = new TreeMap<>();


    public BitmexStreamingMarketDataService(BitmexStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    private String getBitmexSymbol(CurrencyPair currencyPair, Object... args) {
        if (args.length > 0) {
            BitmexPrompt prompt = (BitmexPrompt) args[0];
            BitmexContract contract = new BitmexContract(currencyPair, prompt);
            return BitmexUtils.translateBitmexContract(contract);
        } else {
            return currencyPair.base.toString() + currencyPair.counter.toString();
        }
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        String instrument = getBitmexSymbol(currencyPair, args);
        String channelName = String.format("orderBookL2:%s", instrument);

        return streamingService.subscribeBitmexChannel(channelName).map(s -> {
            BitmexOrderbook orderbook;
            String action = s.getAction();
            if (action.equals("partial")) {
                orderbook = s.toBitmexOrderbook();
                orderbooks.put(instrument, orderbook);
            } else {
                orderbook = orderbooks.get(instrument);
                //ignore updates until first "partial"
                if (orderbook == null) {
                    return new OrderBook(null, Collections.emptyList(), Collections.emptyList());
                }
                BitmexLimitOrder[] levels = s.toBitmexOrderbookLevels();
                orderbook.updateLevels(levels, action);
            }

            return orderbook.toOrderbook();
        });
    }

    public Observable<BitmexTicker> getRawTicker(CurrencyPair currencyPair, Object... args) {
        String instrument = getBitmexSymbol(currencyPair, args);
        String channelName = String.format("quote:%s", instrument);

        return streamingService.subscribeBitmexChannel(channelName).map(s -> s.toBitmexTicker());
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        String instrument = getBitmexSymbol(currencyPair, args);
        String channelName = String.format("quote:%s", instrument);

        return streamingService.subscribeBitmexChannel(channelName).map(s -> {
            BitmexTicker bitmexTicker = s.toBitmexTicker();
            return bitmexTicker.toTicker();
        });
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        String instrument = getBitmexSymbol(currencyPair, args);
        String channelName = String.format("trade:%s", instrument);

        return streamingService.subscribeBitmexChannel(channelName).flatMapIterable(s -> {
            BitmexTrade[] bitmexTrades = s.toBitmexTrades();
            List<Trade> trades = new ArrayList<>(bitmexTrades.length);
            for (BitmexTrade bitmexTrade : bitmexTrades) {
                trades.add(bitmexTrade.toTrade());
            }
            return trades;
        });
    }


    public Observable<BitmexExecution> getExecutions(String symbol) {
        return streamingService.subscribeBitmexChannel("execution:" + symbol).flatMapIterable(s -> {
            JsonNode executions = s.getData();
            List<BitmexExecution> bitmexExecutions = new ArrayList<>(executions.size());
            for (JsonNode execution : executions) {
                bitmexExecutions.add(objectMapper.treeToValue(execution, BitmexExecution.class));
            }
            return bitmexExecutions;
        });
    }

    public void enableDeadManSwitch() throws IOException {
        streamingService.enableDeadManSwitch();
    }

    /**
     * @param rate    in milliseconds to send updated
     * @param timeout milliseconds from now after which orders will be cancelled
     */
    public void enableDeadManSwitch(long rate, long timeout) throws IOException {
        streamingService.enableDeadMansSwitch(rate, timeout);
    }

    public void enableHeartbeat(boolean withDms) {
        streamingService.enableHeartbeat(withDms);
    }

    public void enableHeartbeat(boolean withDms, long rate, long timeout) {
        streamingService.enableHeartbeat(withDms, rate, timeout);
    }

    public void disableHeartbeat() throws IOException {
        streamingService.disableHeartbeat();
    }

    public boolean isDeadManSwitchEnabled() throws IOException {
        return streamingService.isDeadMansSwitchEnabled();
    }

    public void disableDeadMansSwitch() throws IOException {
        streamingService.disableDeadMansSwitch();
    }
}
