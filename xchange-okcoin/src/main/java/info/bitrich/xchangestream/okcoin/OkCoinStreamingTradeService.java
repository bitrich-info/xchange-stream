package info.bitrich.xchangestream.okcoin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingTradeService;
import info.bitrich.xchangestream.okcoin.dto.OkCoinFuturesOrder;
import io.reactivex.Observable;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.okcoin.FuturesContract;


public class OkCoinStreamingTradeService implements StreamingTradeService {

    private final OkCoinStreamingService streamingService;

    protected ExchangeSpecification exchangeSpecification;

    private final ObjectMapper mapper = new ObjectMapper();

    public OkCoinStreamingTradeService(OkCoinStreamingService streamingService, ExchangeSpecification exchangeSpecification) {
        this.streamingService = streamingService;
        this.exchangeSpecification = exchangeSpecification;
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Observable<Order> getOrders(CurrencyPair currencyPair, Object... args) {
        if (args.length < 1 || !(args[0] instanceof FuturesContract)) {
            throw new NotYetImplementedForExchangeException();
        }
        FuturesContract contract = (FuturesContract)args[0];
        String channel = "ok_sub_futureusd_trades";
        String instrument = String.format("%s_%s", currencyPair.counter.toString().toLowerCase(), currencyPair.base.toString().toLowerCase());

        return this.streamingService.subscribeChannel(channel, new Object[0])
                .map(s -> this.mapper.treeToValue(s.get("data"), OkCoinFuturesOrder.class))
                .filter(order -> order.getSymbol().equals(instrument) && order.getContractType().equals(contract.getName()))
                .map(OkCoinFuturesOrder::toOrder);
    }

    @Override
    public void submitOrder(Order order, CurrencyPair currencyPair, Object... args) {
        throw new NotYetImplementedForExchangeException();
    }

}