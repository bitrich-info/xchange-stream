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

        exchange.getStreamingRawService().getOrderData()
                .subscribe(
                        order -> LOG.info("Order id={}, status={}, pair={}, remains={}",
                                          order.getId(),
                                          order.getStatus(),
                                          order.getCurrencyPair(),
                                          order.getRemainingAmount()),
                        throwable -> LOG.error("ERROR in getting order data: ", throwable));

        exchange.getStreamingRawService().getTransactions()
                .subscribe(
                        transaction -> LOG.info("Transaction: {}", transaction),
                        throwable -> LOG.error("ERROR in getting order data: ", throwable));

        try {
            Thread.sleep(10_000);
        } catch (InterruptedException ignored) {
        }
    }

}
