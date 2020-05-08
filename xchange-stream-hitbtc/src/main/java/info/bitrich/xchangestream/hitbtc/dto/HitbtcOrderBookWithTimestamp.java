package info.bitrich.xchangestream.hitbtc.dto;

import org.knowm.xchange.hitbtc.v2.dto.HitbtcOrderBook;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcOrderLimit;

import java.util.Date;

public class HitbtcOrderBookWithTimestamp extends HitbtcOrderBook {

    private final Date timestamp;

    HitbtcOrderBookWithTimestamp(Date timestamp, HitbtcOrderLimit[] asks, HitbtcOrderLimit[] bids) {
        super(asks, bids);
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        StringBuilder asks = new StringBuilder();
        StringBuilder bids = new StringBuilder();

        for (HitbtcOrderLimit ask : getAsks()) {
            asks.append(ask).append(';');
        }

        for (HitbtcOrderLimit bid : getBids()) {
            bids.append(bid).append(';');
        }

        return "HitbtcOrderBookWithTimestamp{" +
                "asks=" + asks +
                ", bids=" + bids +
                ", timestamp=" + timestamp +
                '}';
    }
}
