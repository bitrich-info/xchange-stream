package info.bitrich.xchangestream.service.centrifugo;

import info.bitrich.xchangestream.service.exception.NotConnectedException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import org.coindirect.centrifuge.java.Centrifugo;
import org.coindirect.centrifuge.java.credentials.Token;
import org.coindirect.centrifuge.java.credentials.User;
import org.coindirect.centrifuge.java.listener.ConnectionListener;
import org.coindirect.centrifuge.java.listener.DataMessageListener;
import org.coindirect.centrifuge.java.message.DataMessage;
import org.coindirect.centrifuge.java.subscription.SubscriptionRequest;
import org.coindirect.centrifuge.java.subscription.UnsubscribeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CentrifugoStreamingService implements DataMessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(CentrifugoStreamingService.class);

    protected final Centrifugo centrifugo;
    private State state = State.DISCONNECTED;

    private Map<String, List<ObservableEmitter<String>>> observers = new HashMap<>();

    private enum State {
        CONNECTING, CONNECTED, DISCONNECTED
    }

    public CentrifugoStreamingService(CentrifugoToken centrifugoToken) {
        LOG.debug("Building with "+centrifugoToken);
        centrifugo = new Centrifugo.Builder(centrifugoToken.getWsUrl())
                .setUser(new User(centrifugoToken.user, centrifugoToken.client))
                .setToken(new Token(centrifugoToken.token, centrifugoToken.timestamp))
                .build();
    }


    public Completable connect() {
        return Completable.create(e -> {
            centrifugo.setConnectionListener(new ConnectionListener() {
                @Override
                public void onWebSocketOpen() {
                    LOG.debug("Centrifugo onWebSocketOpen ");
                    state = State.CONNECTING;
                }

                @Override
                public void onConnected() {
                    LOG.debug("Centrifugo onConnected");
                    state = State.CONNECTED;
                    e.onComplete();
                }

                @Override
                public void onDisconnected(int i, String s, boolean b) {
                    LOG.debug("Centrifugo onDisconnected");
                    state = State.DISCONNECTED;
                }
            });

            centrifugo.setDataMessageListener(this);

            centrifugo.connect();
        });
    }

    public Completable disconnect() {
        return Completable.create(completable -> {
            LOG.info("Disconnecting Centrifugo");
            centrifugo.disconnect();
            completable.onComplete();
        });
    }

    public Observable<String> subscribeChannel(String channelName) {
        LOG.info("Subscribing to channel {}.", channelName);
        Observable<String> observable = Observable.<String>create(e -> {
            if (state != State.CONNECTED) {
                LOG.error("Centrifugo not connected.");
                e.onError(new NotConnectedException());
                return;
            }

            LOG.debug("Sending subscribe for {}", channelName);
            centrifugo.subscribe(new SubscriptionRequest(channelName));

            addObserver(channelName, e);
        }).doOnDispose(() -> {
            LOG.info("Unsubscribing from channel {}.", channelName);
//            centrifugo.unsubscribe(new UnsubscribeRequest(channelName));
        });

        return observable;
    }

    private void addObserver(String channel, ObservableEmitter<String> observable) {
        List<ObservableEmitter<String>> existing = observers.get(channel);
        if(existing == null) {
            existing = new ArrayList<>();
            observers.put(channel, existing);
        }
        existing.add(observable);
    }


    public boolean isSocketOpen() {
        return state == State.CONNECTED;
    }

    @Override
    public void onNewDataMessage(DataMessage dataMessage) {
        List<ObservableEmitter<String>> observables = observers.get(dataMessage.getChannel());

        LOG.debug("Got dataMessage "+dataMessage.getChannel()+": "+dataMessage.getData());

        String data = dataMessage.getData();


        if(observables != null) {
            for(ObservableEmitter<String> observable : observables) {
                if(data != null) {
                    LOG.debug("Emitting "+data);
                    observable.onNext(data);
                }
            }
        }
    }

}
