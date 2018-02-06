package info.bitrich.xchangestream.service.netty;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

public class RetryWithDelay implements Function<Flowable<? extends Throwable>, Publisher<?>> {
    private final long retryDelayMillis;

    public RetryWithDelay(final long retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public Publisher<?> apply(Flowable<? extends Throwable> flowable) {
        return flowable.flatMap((Function<Throwable, Publisher<?>>) throwable -> Flowable.timer(retryDelayMillis, TimeUnit.MILLISECONDS));
    }
}
