package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.bitmex.dto.BitmexAffiliate;
import info.bitrich.xchangestream.bitmex.dto.BitmexMargin;
import info.bitrich.xchangestream.bitmex.dto.BitmexWallet;
import io.reactivex.Observable;

public interface BitmexStreamingAccountService {

    Observable<BitmexWallet> getWalletChanges();

    Observable<BitmexAffiliate> getAffiliateChanges();

    Observable<BitmexMargin> getMarginChanges();
}
