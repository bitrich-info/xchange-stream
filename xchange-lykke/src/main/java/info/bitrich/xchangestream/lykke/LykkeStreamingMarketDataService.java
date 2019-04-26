package info.bitrich.xchangestream.lykke;

import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.service.wamp.WampStreamingService;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;


public class LykkeStreamingMarketDataService implements StreamingMarketDataService {

    private WampStreamingService wampStreamingService;

    public LykkeStreamingMarketDataService(WampStreamingService wampStreamingService) {
        this.wampStreamingService = wampStreamingService;
    }

    @Override
    public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
        return null;
    }

    @Override
    public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
        return null;
    }

    @Override
    public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
        return null;
    }

}
