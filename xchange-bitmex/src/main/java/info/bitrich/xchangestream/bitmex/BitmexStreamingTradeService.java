package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.bitmex.dto.BitmexExecution;
import info.bitrich.xchangestream.bitmex.dto.BitmexOrder;
import info.bitrich.xchangestream.bitmex.dto.BitmexPosition;
import io.reactivex.Observable;
import org.knowm.xchange.currency.CurrencyPair;

public interface BitmexStreamingTradeService {
    Observable<BitmexOrder> getOrderChanges(CurrencyPair currencyPair);

    Observable<BitmexExecution> getExecutionChanges(CurrencyPair currencyPair);

    Observable<BitmexPosition> getPositionChanges(CurrencyPair currencyPair);
}
