package info.bitrich.xchangestream.bitstamp.v2.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BitstampWebSocketOrderData {

  private static final String ATTR_ORDER_TYPE = "order_type";
  private static final String ATTR_PRICE = "price";
  private static final String ATTR_DATETIME = "datetime";
  private static final String ATTR_AMOUNT = "amount";
  private static final String ATTR_ID_STR = "id_str";
  private static final String ATTR_AMOUNT_STR = "amount_str";
  private static final String ATTR_PRICE_STR = "price_str";
  private static final String ATTR_ID = "id";
  private static final String ATTR_MICROTIMESTAMP = "microtimestamp";

  private final int orderType;
  private final BigDecimal price;
  private final long dateTime;
  private final BigDecimal amount;
  private final String idStr;
  private final String amountStr;
  private final String priceStr;
  private final long id;
  private final String microTimestamp;

  @JsonCreator
  public BitstampWebSocketOrderData(
          @JsonProperty(ATTR_ORDER_TYPE) int orderType,
          @JsonProperty(ATTR_PRICE) BigDecimal price,
          @JsonProperty(ATTR_DATETIME) long dateTime,
          @JsonProperty(ATTR_AMOUNT) BigDecimal amount,
          @JsonProperty(ATTR_ID_STR) String idStr,
          @JsonProperty(ATTR_AMOUNT_STR) String amountStr,
          @JsonProperty(ATTR_PRICE_STR) String priceStr,
          @JsonProperty(ATTR_ID) long id,
          @JsonProperty(ATTR_MICROTIMESTAMP) String microTimestamp) {
    this.orderType = orderType;
    this.price = price;
    this.dateTime = dateTime;
    this.amount = amount;
    this.idStr = idStr;
    this.amountStr = amountStr;
    this.priceStr = priceStr;
    this.id = id;
    this.microTimestamp = microTimestamp;
  }

  public int getOrderType() {
    return orderType;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public long getDateTime() {
    return dateTime;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getIdStr() {
    return idStr;
  }

  public String getAmountStr() {
    return amountStr;
  }

  public String getPriceStr() {
    return priceStr;
  }

  public long getId() {
    return id;
  }

  public String getMicroTimestamp() {
    return microTimestamp;
  }

  @Override
  public String toString() {
    return "{\"orderType\":" + orderType +
            ",\"price\":" + price +
            ",\"dateTime\":" + dateTime +
            ",\"amount\":" + amount +
            ",\"idStr\":\"" + idStr + '"' +
            ",\"amountStr\":\"" + amountStr + '"' +
            ",\"priceStr\":\"" + priceStr + '"' +
            ",\"id\":" + id +
            ",\"microTimestamp\":\"" + microTimestamp + '"' +
            '}';
  }

}
