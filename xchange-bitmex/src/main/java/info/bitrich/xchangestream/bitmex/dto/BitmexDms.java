package info.bitrich.xchangestream.bitmex.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import info.bitrich.xchangestream.bitmex.BitmexStreamingService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static info.bitrich.xchangestream.util.TimeUtil.currentTime;

/**
 * @author Foat Akhmadeev
 * 09/07/2018
 */
public class BitmexDms {
    private static final Logger LOG = LoggerFactory.getLogger(BitmexDms.class);
    private final BitmexStreamingService service;
    private long cancelAllIn = 60000;
    private long resubscribe = 15000;
    private long lastDmsTime = 0;
    private long dmsCancelTime;

    private Disposable dmsDisposable;

    public BitmexDms(BitmexStreamingService bitmexStreamingService) {
        this.service = bitmexStreamingService;
    }

    public boolean handleMessage(JsonNode message) {
        if (!message.has("now") || !message.has("cancelTime")) {
            return false;
        }
        //handle dead man's switch confirmation
        try {
            String cancelTime = message.get("cancelTime").asText();
            if (cancelTime.equals("0")) {
                LOG.info("Dead man's switch disabled");
                if (dmsDisposable != null) {
                    dmsDisposable.dispose();
                    dmsDisposable = null;
                }
                dmsCancelTime = 0;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat(BitmexMarketDataEvent.BITMEX_TIMESTAMP_FORMAT);
                sdf.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
                long now = sdf.parse(message.get("now").asText()).getTime();
                dmsCancelTime = sdf.parse(cancelTime).getTime();
            }
        } catch (ParseException e) {
            LOG.error("Error parsing deadman's confirmation ");
        }
        return true;
    }

    public void enableDeadMansSwitch() throws JsonProcessingException {
        this.enableDeadMansSwitch(15000, 60000);
    }

    public void enableDeadMansSwitch(long rate, long timeout) throws JsonProcessingException {
        if (dmsDisposable != null) {
            LOG.warn("You already have Dead Man's switch enabled. Doing nothing");
            return;
        }
        this.resubscribe = rate;
        this.cancelAllIn = timeout;
        String message = dmsMessage(timeout);
        dmsDisposable = Schedulers.single().schedulePeriodicallyDirect(() -> service.sendMessage(message), 0, rate, TimeUnit.MILLISECONDS);
        Schedulers.single().start();
    }

    public void disableDeadMansSwitch() throws IOException {
        String message = dmsMessage(0);
        service.sendMessage(message);
    }

    public boolean isDeadMansSwitchEnabled() {
        return dmsCancelTime > 0 && System.currentTimeMillis() < dmsCancelTime;
    }

    public void sendDmsMessage() {
        long time = currentTime();
        if (time - lastDmsTime >= resubscribe) {
            String message;
            try {
                message = dmsMessage(cancelAllIn);
                service.sendMessage(message);
                LOG.debug("sending dms {}", message);
                lastDmsTime = time;
            } catch (JsonProcessingException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private String dmsMessage(long dmsCancelAllIn) throws JsonProcessingException {
        final BitmexWebSocketSubscriptionMessage subscriptionMessage = new BitmexWebSocketSubscriptionMessage("cancelAllAfter", new Object[]{dmsCancelAllIn});
        return StreamingObjectMapperHelper.getObjectMapper().writeValueAsString(subscriptionMessage);
    }

    public void setRateTimeout(long rate, long timeout) {
        this.resubscribe = rate;
        this.cancelAllIn = timeout;
    }
}
