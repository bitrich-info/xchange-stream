package info.bitrich.xchangestream.bitmex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

public class BitmexFunding {
    private String symbol;
    private Date timestamp;
    private BigDecimal fundingRate;
    private BigDecimal fundingRateDaily;

    public BitmexFunding(@JsonProperty("symbol") String symbol,
                         @JsonProperty("timestamp") Date timestamp,
                         @JsonProperty("fundingRate") BigDecimal fundingRate,
                         @JsonProperty("fundingRateDaily") BigDecimal fundingRateDaily) {
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.fundingRate = fundingRate;
        this.fundingRateDaily = fundingRateDaily;
    }

    public String getSymbol() {
        return symbol;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public BigDecimal getFundingRate() {
        return fundingRate;
    }

    public BigDecimal getFundingRateDaily() {
        return fundingRateDaily;
    }
}
