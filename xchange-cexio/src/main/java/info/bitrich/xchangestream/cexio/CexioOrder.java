package info.bitrich.xchangestream.cexio;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.util.Date;

public class CexioOrder extends LimitOrder {

    private BigDecimal remainingAmount;

    public CexioOrder(OrderType type,
                      CurrencyPair currencyPair,
                      BigDecimal originalAmount,
                      String id,
                      Date timestamp,
                      BigDecimal limitPrice,
                      BigDecimal fee,
                      OrderStatus status) {
        super(type, CexioAdapters.adaptAmount(originalAmount), currencyPair, id, timestamp, limitPrice,
              null, null, fee, status);
        this.remainingAmount = null;
    }

    public CexioOrder(CurrencyPair currencyPair, String id, OrderStatus status, BigDecimal remainingAmount) {
        this(null, currencyPair, null, id, null, null, null, status);
        this.remainingAmount = CexioAdapters.adaptAmount(remainingAmount);
    }

    @Override
    public BigDecimal getRemainingAmount() {
        if (remainingAmount != null) {
            return remainingAmount;
        }

        return super.getRemainingAmount();
    }
}
