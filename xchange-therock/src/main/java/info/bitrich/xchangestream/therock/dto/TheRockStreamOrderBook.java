package info.bitrich.xchangestream.therock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class TheRockStreamOrderBook {

    private List<TheRockStreamLimitOrder> asks;
    private List<TheRockStreamLimitOrder> bids;

    public TheRockStreamOrderBook(
            @JsonProperty("asks") List<TheRockStreamLimitOrder> asks,
            @JsonProperty("bids") List<TheRockStreamLimitOrder> bids) {
        this.asks = asks;
        this.bids = bids;
    }

    public List<TheRockStreamLimitOrder> getAsks() {
        return asks;
    }

    public void setAsks(List<TheRockStreamLimitOrder> asks) {
        this.asks = asks;
    }

    public List<TheRockStreamLimitOrder> getBids() {
        return bids;
    }

    public void setBids(List<TheRockStreamLimitOrder> bids) {
        this.bids = bids;
    }

    @Override
    public String toString() {
        return "TheRockStreamOrderBook{" +
                "asks=" + asks +
                ", bids=" + bids +
                '}';
    }

    public static class TheRockStreamLimitOrder {

        private BigDecimal price;
        private BigDecimal amount;

        public TheRockStreamLimitOrder(
                @JsonProperty("price") BigDecimal price,
                @JsonProperty("amount") BigDecimal amount) {
            this.price = price;
            this.amount = amount;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "TheRockStreamLimitOrder{" +
                    "price=" + price +
                    ", amount=" + amount +
                    '}';
        }
    }
}
