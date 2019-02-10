package info.bitrich.xchangestream.lykke.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class LykkeOrderBookLevels {

    private String id;
    private BigDecimal volume;
    private BigDecimal price;

    public LykkeOrderBookLevels(
            @JsonProperty("Id") String id,
            @JsonProperty("Volume") BigDecimal volume,
            @JsonProperty("Price") BigDecimal price) {
        this.id = id;
        this.volume = volume;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "LykkeOrderBookLevels{" +
                "id='" + id + '\'' +
                ", volume=" + volume +
                ", price=" + price +
                '}';
    }
}
