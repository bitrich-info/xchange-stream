package info.bitrich.xchangestream.core;

import io.reactivex.Observable;

import org.knowm.xchange.dto.Order;

/**
 * Interface to provide the following to {@link StreamingExchange}:
 * <ul>
 * <li>Standard methods available to subscribe to private API data
 * </ul>
 */
public interface StreamingPrivateDataService {
    /**
     * Get the orders representing the current status of orders
     *
     * @return {@link Observable} that emits {@link Order} when exchange sends the update
     */
    Observable<Order> getOrders();
}
