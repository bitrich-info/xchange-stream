package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.bitmex.dto.BitmexWebSocketTransaction;
import info.bitrich.xchangestream.core.StreamingAccountService;
import io.reactivex.Observable;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BitmexStreamingAccountService implements StreamingAccountService {

    private final BitmexStreamingService streamingService;

    public BitmexStreamingAccountService(BitmexStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    /*
        Total Amount = real btc on bitmex account + all borrowed btc in order to go long or short in real-time
        Available Amount = real btc on the account + any unrealised profit/loss in real-time
        Frozen Amount = all borrowed btc funds in order to go long or short.
     */

    @Override
    public Observable<Balance> getBalanceChanges(Currency currency, Object... args) {

        if(currency.equals(Currency.XBT) || currency.equals(Currency.BTC)) {

            return streamingService.subscribeBitmexChannel("margin")

                    .map(BitmexWebSocketTransaction::toBitmexMargin)
                    .map(margin -> {

                        BigDecimal grossLastValue = (margin[0].getGrossLastValue() == 0 && margin[0].getRiskValue() != 0)
                                ? BigDecimal.valueOf(margin[0].getRiskValue()).divide(BigDecimal.valueOf(100000000),8, RoundingMode.UNNECESSARY)
                                : BigDecimal.valueOf(margin[0].getGrossLastValue()).divide(BigDecimal.valueOf(100000000),8, RoundingMode.UNNECESSARY);

                        BigDecimal marginBalance = (margin[0].getMarginBalance() == 0 && margin[0].getWalletBalance() != 0)
                                ? BigDecimal.valueOf(margin[0].getWalletBalance()).divide(BigDecimal.valueOf(100000000),8, RoundingMode.UNNECESSARY)
                                : BigDecimal.valueOf(margin[0].getMarginBalance()).divide(BigDecimal.valueOf(100000000),8, RoundingMode.UNNECESSARY);

                        return new Balance(
                                currency,
                                grossLastValue.add(marginBalance),
                                marginBalance,
                                grossLastValue
                        );

                    }).filter(balance-> balance.getAvailable().compareTo(BigDecimal.ZERO) != 0);
        }else{
            return Observable.error(new IOException("Bitmex exchange only supports XBT or BTC currency."));
        }

    }
}
