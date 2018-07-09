package info.bitrich.xchangestream.bitmex.dto;

import com.google.common.collect.Lists;
import info.bitrich.xchangestream.bitmex.BitmexStreamingService;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static info.bitrich.xchangestream.util.TimeUtil.currentTime;

/**
 * @author Foat Akhmadeev
 * 09/07/2018
 */
public class BitmexHeartbeat {
    private static final Logger LOG = LoggerFactory.getLogger(BitmexHeartbeat.class);

    private static final String PING = "ping";
    private static final String PONG = "pong";

    private static final int HEARTBEAT_DELAY = 5000;
    private final BitmexStreamingService service;
    private final List<Runnable> onPing;
    private long lastMsgTime = 0;
    private long lastPingTime = 0;
    private long lastPongTime = 0;
    private Disposable heartbeatDisposable;

    public BitmexHeartbeat(BitmexStreamingService bitmexStreamingService) {
        this.service = bitmexStreamingService;
        this.onPing = Lists.newArrayList();
    }

    /**
     * @param message service message
     * @return true if got 'pong'
     */
    public boolean handleMessage(String message) {
        if (!isEnabled()) {
            return false;
        }
        lastMsgTime = currentTime();
        if (Objects.equals(message, PONG)) {
            lastPongTime = currentTime();
            LOG.info("Got pong message");
            return true;
        }
        return false;
    }

    public void enableHeartbeat() {
        if (isEnabled()) {
            LOG.warn("You already started heartbeat service.");
            return;
        }
        heartbeatDisposable = Schedulers.single().schedulePeriodicallyDirect(this::ping, 0, HEARTBEAT_DELAY, TimeUnit.MILLISECONDS);
        Schedulers.single().start();
    }

    public void disableHeartbeat() {
        if (!isEnabled()) {
            LOG.warn("Heartbeat is already disabled");
        }
        heartbeatDisposable.dispose();
        heartbeatDisposable = null;
        LOG.info("Stopping heartbeat");
    }

    private void ping() {
        LOG.info("ping(), {}, {}, {}", lastMsgTime, lastPingTime, lastPongTime);
        if (lastPingTime != 0 && (lastPongTime == 0 || lastPongTime - lastPingTime >= HEARTBEAT_DELAY)) {
            LOG.error("Did not get pong messages in time");
        }
        if (lastMsgTime - lastPingTime >= HEARTBEAT_DELAY || lastPingTime >= lastMsgTime) {
            lastPongTime = 0;
            LOG.info("Sending ping message");
            service.sendMessage(PING);
        }
        lastPingTime = currentTime();

        onPing.forEach(Runnable::run);
    }

    public void addOnPingFunction(Runnable runnable) {
        onPing.add(runnable);
    }

    public void clearOnPingFunctions() {
        onPing.clear();
    }

    public boolean isEnabled() {
        return heartbeatDisposable != null;
    }
}
