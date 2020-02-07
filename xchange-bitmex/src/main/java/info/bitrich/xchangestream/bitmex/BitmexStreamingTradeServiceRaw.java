package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.bitmex.dto.BitmexExecution;
import info.bitrich.xchangestream.bitmex.dto.BitmexOrder;
import info.bitrich.xchangestream.bitmex.dto.BitmexPosition;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;

public class BitmexStreamingTradeServiceRaw {
    private final BitmexStreamingService streamingService;

    public BitmexStreamingTradeServiceRaw(BitmexStreamingExchange exchange) {
        this.streamingService = exchange.getBitmexStreamingService();
    }

    public Observable<BitmexOrder> getOrderChanges(CurrencyPair currencyPair) {
        String channelName = "order";
        String instrument = currencyPair.base.toString() + currencyPair.counter.toString();
        return streamingService.subscribeBitmexChannel(channelName)
                .flatMap(trans ->
                        Observable.create(emitter -> {
                            BitmexOrder[] bitmexOrders = trans.toBitmexOrders();
                            if (bitmexOrders.length > 0)
                                for (BitmexOrder order : bitmexOrders) {
                                    if (order.getSymbol().equalsIgnoreCase(instrument))
                                        emitter.onNext(order);
                                }
                        }));
    }

    public Observable<BitmexExecution> getExecutionChanges(CurrencyPair currencyPair) {
        String channelName = "execution";
        String instrument = currencyPair.base.toString() + currencyPair.counter.toString();
        return streamingService.subscribeBitmexChannel(channelName)
                .flatMap(trans ->
                        Observable.create(emitter -> {
                            BitmexExecution[] executions = trans.toBitmexExecutions();
                            if (executions.length > 0)
                                for (BitmexExecution execution : executions) {
                                    if (execution.getSymbol().equalsIgnoreCase(instrument))
                                        emitter.onNext(execution);
                                }
                        }));
    }

    public Observable<BitmexPosition> getPositionChanges(CurrencyPair currencyPair) {
        String channelName = "position";
        String instrument = currencyPair.base.toString() + currencyPair.counter.toString();
        return streamingService.subscribeBitmexChannel(channelName)
                .flatMap(trans -> Observable.create(emitter -> {
                    BitmexPosition[] positions = trans.toBitmexPositions();
                    if (positions.length > 0)
                        for (BitmexPosition position : positions) {
                            if (position.getSymbol().equals(instrument))
                                emitter.onNext(position);
                        }
                }));
    }
}
