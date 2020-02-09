package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.bitmex.dto.BitmexAffiliate;
import info.bitrich.xchangestream.bitmex.dto.BitmexMargin;
import info.bitrich.xchangestream.bitmex.dto.BitmexWallet;
import info.bitrich.xchangestream.bitmex.dto.BitmexWebSocketTransaction;
import io.reactivex.Observable;

public class BitmexStreamingAccountServiceRaw implements BitmexStreamingAccountService {
    private BitmexStreamingService streamingService;

    public BitmexStreamingAccountServiceRaw(BitmexStreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @Override
    public Observable<BitmexWallet> getWalletChanges() {
        String channelName = "wallet";
        return streamingService.subscribeBitmexChannel(channelName)
                .map(BitmexWebSocketTransaction::toBitmexWallet);
    }

    @Override
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

    @Override
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
