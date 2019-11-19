/*  actual message
{
	"data":{
		"microtimestamp":"1574158935282494",
		"amount":0.01000309,
		"buy_order_id":4352888198,
		"sell_order_id":4352886848,
		"amount_str":"0.01000309",
		"price_str":"7360.72",
		"timestamp":"1574158935",
		"price":7360.72,
		"type":0,
		"id":100568768
		},
	"event":"trade",
	"channel":"live_trades_btceur"
}
*/




package info.bitrich.xchangestream.bitstamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.knowm.xchange.bitstamp.dto.marketdata.BitstampTransaction;

import java.math.BigDecimal;

public class BitstampWebSocketTransaction extends BitstampTransaction {
	
	public BitstampWebSocketTransaction(
			@JsonProperty("timestamp") long date, 
			@JsonProperty("id") long tid,
			@JsonProperty("price_str") String price_str, 
			@JsonProperty("amount_str") String amount_str,
			@JsonProperty("order_type") int type) {
		
		super(date, tid, getBigDecimal(price_str), getBigDecimal(amount_str), type);
		
	}
	
	// return BigDecimal object
	public static BigDecimal getBigDecimal(String val) {
	     
		BigDecimal ret = new BigDecimal(val);
		return ret;
	}
	
}
