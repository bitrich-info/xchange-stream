package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.core.StreamingAccountService;
import io.reactivex.Observable;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;

import java.math.BigDecimal;

public class BitmexStreamingAccountService implements StreamingAccountService {

    private final BitmexStreamingService streamingService;

    public BitmexStreamingAccountService(BitmexStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @Override
    public Observable<Balance> getBalanceChanges(Currency currency, Object... args) {
        String channelName = "position";

        return streamingService.subscribeBitmexChannel(channelName).map(s->{
            System.out.println(s.getData().toString());
            return new Balance(Currency.BTC, BigDecimal.valueOf(1),BigDecimal.ONE);
        });
    }
}
