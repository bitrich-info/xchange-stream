package info.bitrich.xchangestream.cexio;

import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.core.StreamingPrivateDataService;
import org.knowm.xchange.ExchangeSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CexioManualExample {

    private static final Logger LOG = LoggerFactory.getLogger(CexioManualExample.class);

    public static void main(String[] args) throws IOException {
        CexioStreamingExchange exchange = (CexioStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(
                CexioStreamingExchange.class.getName());

        CexioProperties properties = new CexioProperties();

        ExchangeSpecification specification = exchange.getDefaultExchangeSpecification();
        specification.setApiKey(properties.getApiKey());
        specification.setSecretKey(properties.getSecretKey());

        exchange.applySpecification(specification);

        exchange.connect().blockingAwait();

        StreamingPrivateDataService streamingPrivateDataService = exchange.getStreamingPrivateDataService();

        streamingPrivateDataService.getOrders()
                .subscribe(
                        order -> LOG.info("Order id={}, status={}, pair={}, remains={}",
                                          order.getId(),
                                          order.getStatus(),
                                          order.getCurrencyPair(),
                                          order.getRemainingAmount()),
                        throwable -> LOG.error("ERROR in getting order data: ", throwable));

        CexioStreamingPrivateDataRawService streamingPrivateDataRawService =
                (CexioStreamingPrivateDataRawService) streamingPrivateDataService;

        streamingPrivateDataRawService.getTransactions()
                .subscribe(
                        transaction -> LOG.info("Transaction: {}", transaction),
                        throwable -> LOG.error("ERROR in getting order data: ", throwable));

        try {
            Thread.sleep(10_000);
        } catch (InterruptedException ignored) {
        }
    }

}
