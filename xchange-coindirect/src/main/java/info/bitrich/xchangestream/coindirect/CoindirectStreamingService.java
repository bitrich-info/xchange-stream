package info.bitrich.xchangestream.coindirect;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.service.centrifugo.CentrifugoToken;
import info.bitrich.xchangestream.service.centrifugo.CentrifugoStreamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CoindirectStreamingService extends CentrifugoStreamingService {
    private static final Logger LOG = LoggerFactory.getLogger(CoindirectStreamingService.class);

    List<ProductSubscription> subscriptionList;

    public CoindirectStreamingService(CentrifugoToken centrifugoToken, List<ProductSubscription> subscriptionList) {
        super(centrifugoToken);
        this.subscriptionList = subscriptionList;
    }
}
