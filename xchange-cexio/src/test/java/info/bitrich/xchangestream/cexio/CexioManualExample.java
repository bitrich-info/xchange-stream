package info.bitrich.xchangestream.cexio;

import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CexioManualExample {

    private static final Logger LOG = LoggerFactory.getLogger(CexioManualExample.class);

    public static void main(String[] args) throws IOException {
        CexioStreamingExchange exchange = (CexioStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(
                CexioStreamingExchange.class.getName());
        CexioProperties properties = new CexioProperties();
        exchange.setCredentials(properties.getApiKey(), properties.getSecretKey());
        exchange.connect().blockingAwait();

        exchange.getStreamingService().getOrderCancelled().subscribe(
                order -> LOG.info("Cancelled: {}", order.getId()));

        exchange.getStreamingService().getOrderFilledFull().subscribe(
                order -> LOG.info("Full filled: {}", order));

        exchange.getStreamingService().getOrderFilledPartially().subscribe(
                order -> LOG.info("Partially filled: {}", order));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
