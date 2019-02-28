package info.bitrich.xchangestream.lykke.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class LykkeBalanceDto {

    private String id;
    private String asset;
    private BigDecimal balance;
    private BigDecimal reserved;

    public LykkeBalanceDto(
            @JsonProperty("id") String id,
            @JsonProperty("a") String asset,
            @JsonProperty("b") BigDecimal balance,
            @JsonProperty("r") BigDecimal reserved) {
        this.id = id;
        this.asset = asset;
        this.balance = balance;
        this.reserved = reserved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getReserved() {
        return reserved;
    }

    public void setReserved(BigDecimal reserved) {
        this.reserved = reserved;
    }

    @Override
    public String toString() {
        return "LykkeBalanceDto{" +
                "id='" + id + '\'' +
                ", asset='" + asset + '\'' +
                ", balance=" + balance +
                ", reserved=" + reserved +
                '}';
    }
}
