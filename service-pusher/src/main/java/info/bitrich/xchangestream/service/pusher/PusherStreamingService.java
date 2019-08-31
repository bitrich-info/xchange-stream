package info.bitrich.xchangestream.service.pusher;

import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import info.bitrich.xchangestream.service.ConnectableService;
import info.bitrich.xchangestream.service.exception.NotConnectedException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PusherStreamingService extends ConnectableService  {
    private static final Logger LOG = LoggerFactory.getLogger(PusherStreamingService.class);

    private final Pusher pusher;

    public PusherStreamingService(String apiKey) {
        pusher = new Pusher(apiKey);
    }

    public PusherStreamingService(String apiKey, String cluster) {
        PusherOptions options = new PusherOptions();
        options.setCluster(cluster);
        pusher = new Pusher(apiKey, options);
    }

    public PusherStreamingService(String apiKey, String cluster, Authorizer authorizer) {
        PusherOptions options = new PusherOptions();
        options.setCluster(cluster);
        options.setAuthorizer(authorizer);
        this.pusher = new Pusher(apiKey, options);
    }

    /**
     * Testing constructor
     */
    protected PusherStreamingService(Pusher pusher) {
        this.pusher = pusher;
    }

    @Override
    protected Completable openConnection() {
        return Completable.create(e -> pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                LOG.info("State changed to " + change.getCurrentState() +
                        " from " + change.getPreviousState());
                if (ConnectionState.CONNECTED.equals(change.getCurrentState())) {
                    e.onComplete();
                }
            }

            @Override
            public void onError(String message, String code, Exception throwable) {
                if (throwable != null) {
                    e.onError(throwable);
                } else {
                    e.onError(new RuntimeException("No exception found: [code: " + code + "], message: " + message));
                }
            }
        }, ConnectionState.ALL));
    }

    public Completable disconnect() {
        return Completable.create(completable -> {
            pusher.disconnect();
            completable.onComplete();
        });
    }

    public Observable<String> subscribePrivateChannel(String channelName, String eventName) {
        return this.subscribePrivateChannel(channelName, Collections.singletonList(eventName));
    }

    public Observable<String> subscribeChannel(String channelName, String eventName) {
        return subscribeChannel(channelName, Collections.singletonList(eventName));
    }

    public Observable<String> subscribeChannel(String channelName, List<String> eventsName) {
        LOG.info("Subscribing to channel {}.", channelName);
        return Observable.<String>create(e -> {
            if (!ConnectionState.CONNECTED.equals(pusher.getConnection().getState())) {
                e.onError(new NotConnectedException());
                return;
            }
            Channel channel = pusher.subscribe(channelName);
            for (String event : eventsName) {
                channel.bind(event, (channel1, ev, data) -> {
                    LOG.debug("Incoming data: {}", data);
                    e.onNext(data);
                });
            }
        }).doOnDispose(() -> pusher.unsubscribe(channelName));
    }

    public Observable<String> subscribePrivateChannel(String channelName, List<String> eventsName) {
        LOG.info("Subscribing to channel {}.", channelName);

        return Observable.<String>create(e -> {
            if (!ConnectionState.CONNECTED.equals(pusher.getConnection().getState())) {
                e.onError(new NotConnectedException());
                return;
            }
            PrivateChannelEventListener listener = new PrivateChannelEventListener() {
                public void onAuthenticationFailure(String s, Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    e.onError(ex);
                }

                public void onSubscriptionSucceeded(String s) {
                    LOG.info("Subscription successful! :{} ", s);
                }

                public void onEvent(String s, String s1, String s2) {
                    LOG.debug("Incoming data: {}", s2);
                    e.onNext(s2);
                }
            };
            Channel channel = pusher.subscribePrivate(channelName,listener);
            for (String event : eventsName) {
                channel.bind(event, (channel1, ev, data) -> {
                    LOG.debug("Incoming data: {}", data);
                    e.onNext(data);
                });
            }
        }).doOnDispose(() -> {
            LOG.info("Disposing " + channelName);
            pusher.unsubscribe(channelName);
        });
    }

    public boolean isSocketOpen() {
        return pusher.getConnection().getState() == ConnectionState.CONNECTED;
    }

    public void useCompressedMessages(boolean compressedMessages) { throw new UnsupportedOperationException(); }
}
