// Bitstamp v2 - websocket api
// JSON mapping class

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

package info.bitrich.xchangestream.bitstamp.v2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;


public class BitstampWebSocketTrade {
	
	private long date;
	private long tid;
	private BigDecimal price;
	private BigDecimal amount;
	private int type;
	
	
	public BitstampWebSocketTrade(
			@JsonProperty("timestamp") long date, 
			@JsonProperty("id") long tid,
			@JsonProperty("price_str") String price_str, 
			@JsonProperty("amount_str") String amount_str,
			@JsonProperty("order_type") int type) {
		
		this.date = date;
		this.tid = tid;
		this.price = new BigDecimal(price_str);
		this.amount = new BigDecimal(amount_str);
		this.type = type;
	}
	
	// GETTERS, SETTERS
	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getTid() {
		return tid;
	}

	public void setTid(long tid) {
		this.tid = tid;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
