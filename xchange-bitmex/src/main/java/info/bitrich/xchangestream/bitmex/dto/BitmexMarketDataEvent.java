package info.bitrich.xchangestream.bitmex.dto;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lukas Zaoralek on 13.11.17.
 */
public class BitmexMarketDataEvent {
    protected String timestamp;
    protected String symbol;

    public BitmexMarketDataEvent(String symbol, String timestamp) {
        this.timestamp = timestamp;
        this.symbol = symbol;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSymbol() {
        return symbol;
    }

    public CurrencyPair getCurrencyPair() {
        String base = symbol.substring(0, 3);
        String counter = symbol.substring(3, 6);
        return new CurrencyPair(new Currency(base), new Currency(counter));
    }

    public Date getDate() {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = null;
        try {
            date = formatter.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
