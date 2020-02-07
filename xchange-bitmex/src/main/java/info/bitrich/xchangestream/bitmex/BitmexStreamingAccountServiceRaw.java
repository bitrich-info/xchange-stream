package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.bitmex.dto.BitmexAffiliate;
import info.bitrich.xchangestream.bitmex.dto.BitmexMargin;
import info.bitrich.xchangestream.bitmex.dto.BitmexWallet;
import info.bitrich.xchangestream.bitmex.dto.BitmexWebSocketTransaction;
import io.reactivex.Observable;

public class BitmexStreamingAccountServiceRaw {
    private BitmexStreamingService streamingService;

    public BitmexStreamingAccountServiceRaw(BitmexStreamingExchange exchange) {
        this.streamingService = exchange.getBitmexStreamingService();
    }

    public Observable<BitmexWallet> getWalletChanges() {
        String channelName = "wallet";
        return streamingService.subscribeBitmexChannel(channelName)
                .map(BitmexWebSocketTransaction::toBitmexWallet);
    }

    public Observable<BitmexAffiliate> getAffiliateChanges() {
        String channelName = "affiliate";
        return streamingService.subscribeBitmexChannel(channelName)
                .flatMap(trans ->
                        Observable.create(emitter -> {
                            BitmexAffiliate affiliate = trans.toBitmexAffiliate();
                            if (affiliate != null)
                                emitter.onNext(affiliate);
                        }));
    }

    public Observable<BitmexMargin> getMarginChanges() {
        String channelName = "margin";
        return streamingService.subscribeBitmexChannel(channelName)
                .flatMap(trans ->
                        Observable.create(emitter -> {
                            BitmexMargin margin = trans.toBitmexMargin();
                            if (margin != null)
                                emitter.onNext(margin);
                        }));
    }
}
