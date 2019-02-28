package info.bitrich.xchangestream.lykke.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LykkeStreamOrderBook {

    private String assetPair;
    private boolean isBuy;
    private String timestamp;
    private List<LykkeOrderBookLevels> levels;

    public LykkeStreamOrderBook(
            @JsonProperty("AssetPair") String assetPair,
            @JsonProperty("IsBuy") boolean isBuy,
            @JsonProperty("Timestamp") String timestamp,
            @JsonProperty("Levels") List<LykkeOrderBookLevels> levels) {
        this.assetPair = assetPair;
        this.isBuy = isBuy;
        this.timestamp = timestamp;
        this.levels = levels;
    }

    public String getAssetPair() {
        return assetPair;
    }

    public void setAssetPair(String assetPair) {
        this.assetPair = assetPair;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<LykkeOrderBookLevels> getLevels() {
        return levels;
    }

    public void setLevels(List<LykkeOrderBookLevels> levels) {
        this.levels = levels;
    }

    @Override
    public String toString() {
        return "LykkeStreamOrderBook{" +
                "assetPair='" + assetPair + '\'' +
                ", isBuy=" + isBuy +
                ", timestamp='" + timestamp + '\'' +
                ", levels=" + levels +
                '}';
    }
}
