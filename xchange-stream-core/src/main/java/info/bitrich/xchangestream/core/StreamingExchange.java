package info.bitrich.xchangestream.core;

import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

public interface StreamingExchange extends Exchange {
    /**
     * Connects to the WebSocket API of the exchange.
     *
     * @param args Product subscription is used only in certain exchanges where you need to specify subscriptions during the connect phase.
     * @return {@link Completable} that completes upon successful connection.
     */
    Completable connect(ProductSubscription... args);

    /**
     * Disconnect from the WebSocket API.
     *
     * @return {@link Completable} that completes upon successful disconnect.
     */
    Completable disconnect();

    /**
     * Checks whether connection to the exchange is alive.
     *
     * @return true if connection is open, otherwise false.
     */
    boolean isAlive();

    /**
     * Observable for reconnection failure event.
     * When this happens, it usually indicates that the server or the network is down.
     *
     * @return Observable with the exception during reconnection.
     *
     * Note that any NettyStreamingService backed exchanges can easily implement this method just by calling subscribeReconnectFailure()
     */
    default Observable<Throwable> reconnectFailure() {
        throw new NotYetImplementedForExchangeException();
    }

    /**
     * Returns service that can be used to access market data.
     */
    StreamingMarketDataService getStreamingMarketDataService();

    /**
     * Set whether or not to enable compression handler.
     *
     * @param compressedMessages Defaults to false
     */
    void useCompressedMessages(boolean compressedMessages);

}
