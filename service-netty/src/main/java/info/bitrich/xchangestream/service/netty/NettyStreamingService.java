package info.bitrich.xchangestream.service.netty;

import info.bitrich.xchangestream.service.exception.NotConnectedException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class NettyStreamingService<T> {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_RETRY_DURATION = Duration.ofSeconds(15);

    private class Subscription {
        final ObservableEmitter<T> emitter;
        final String channelName;
        final Object[] args;

        public Subscription(ObservableEmitter<T> emitter, String channelName, Object[] args) {
            this.emitter = emitter;
            this.channelName = channelName;
            this.args = args;
        }
    }

    private final int maxFramePayloadLength;
    private final URI uri;
    private boolean isManualDisconnect = false;
    private Channel webSocketChannel;
    private Duration retryDuration;
    private Duration connectionTimeout;
    private final NioEventLoopGroup eventLoopGroup;
    protected Map<String, Subscription> channels = new ConcurrentHashMap<>();

    public NettyStreamingService(String apiUrl) {
        this(apiUrl, 65536);
    }

    public NettyStreamingService(String apiUrl, int maxFramePayloadLength) {
        this(apiUrl, maxFramePayloadLength, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_RETRY_DURATION);
    }

    public NettyStreamingService(String apiUrl, int maxFramePayloadLength, Duration connectionTimeout, Duration retryDuration) {
        try {
            this.maxFramePayloadLength = maxFramePayloadLength;
            this.retryDuration = retryDuration;
            this.connectionTimeout = connectionTimeout;
            this.uri = new URI(apiUrl);
            this.eventLoopGroup = new NioEventLoopGroup();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Error parsing URI " + apiUrl, e);
        }
    }

    public Completable connect() {
        return Completable.create(completable -> {
            try {
                LOG.info("Connecting to {}://{}:{}{}", uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath());
                String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();

                String host = uri.getHost();
                if (host == null) {
                    throw new IllegalArgumentException("Host cannot be null.");
                }

                final int port;
                if (uri.getPort() == -1) {
                    if ("ws".equalsIgnoreCase(scheme)) {
                        port = 80;
                    } else if ("wss".equalsIgnoreCase(scheme)) {
                        port = 443;
                    } else {
                        port = -1;
                    }
                } else {
                    port = uri.getPort();
                }

                if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
                    throw new IllegalArgumentException("Only WS(S) is supported.");
                }

                final boolean ssl = "wss".equalsIgnoreCase(scheme);
                final SslContext sslCtx;
                if (ssl) {
                    sslCtx = SslContextBuilder.forClient().build();
                } else {
                    sslCtx = null;
                }

                final WebSocketClientHandler handler = getWebSocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(
                        uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders(), maxFramePayloadLength),
                        this::messageHandler);

                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, java.lang.Math.toIntExact(connectionTimeout.toMillis()))
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline p = ch.pipeline();
                                if (sslCtx != null) {
                                    p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                                }

                                WebSocketClientExtensionHandler clientExtensionHandler = getWebSocketClientExtensionHandler();
                                List<ChannelHandler> handlers = new ArrayList<>(7);
                                handlers.add(new HttpClientCodec());
                                handlers.add(new HttpServerKeepAliveHandler());
                                handlers.add(new WriteTimeoutHandler(10));
                                handlers.add(new ReadTimeoutHandler(10));
                                handlers.add(new HttpObjectAggregator(8192));
                                handlers.add(handler);
                                if (clientExtensionHandler != null) handlers.add(clientExtensionHandler);
                                p.addLast(handlers.toArray(new ChannelHandler[handlers.size()]));
                            }
                        });

                b.connect(uri.getHost(), port).addListener((ChannelFuture future) -> {
                    webSocketChannel = future.channel();
                    if (future.isSuccess()) {
                        handler.handshakeFuture().addListener(f -> completable.onComplete());
                    } else {
                        completable.onError(future.cause());
                    }

                });
            } catch (Exception throwable) {
                completable.onError(throwable);
            }
        });
    }

    public Completable disconnect() {
        return disconnect(true);
    }

    public Completable disconnect(boolean resetChannels) {
        isManualDisconnect = true;
        return Completable.create(completable -> {
            if (webSocketChannel.isOpen()) {
                CloseWebSocketFrame closeFrame = new CloseWebSocketFrame();
                webSocketChannel.writeAndFlush(closeFrame).addListener(future -> {
                    if (resetChannels) {
                        channels = new ConcurrentHashMap<>();
                    }
                    completable.onComplete();
                });
            }
        });
    }

    protected abstract String getChannelNameFromMessage(T message) throws IOException;

    public abstract String getSubscribeMessage(String channelName, Object... args) throws IOException;

    public abstract String getUnsubscribeMessage(String channelName) throws IOException;

    public String getSubscriptionUniqueId(String channelName, Object... args) {
        return channelName;
    }

    /**
     * Handler that receives incoming messages.
     *
     * @param message Content of the message from the server.
     */
    public abstract void messageHandler(String message);

    public void sendMessage(String message) {
        LOG.debug("Sending message: {}", message);

        if (webSocketChannel == null || !webSocketChannel.isOpen()) {
            LOG.warn("WebSocket is not open! Call connect first.");
            return;
        }

        if (!webSocketChannel.isWritable()) {
            LOG.warn("Cannot send data to WebSocket as it is not writable.");
            return;
        }

        if (message != null) {
            WebSocketFrame frame = new TextWebSocketFrame(message);
            webSocketChannel.writeAndFlush(frame);
        }
    }

    public Observable<T> subscribeChannel(String channelName, Object... args) {
        final String channelId = getSubscriptionUniqueId(channelName, args);
        LOG.info("Subscribing to channel {}", channelId);

        return Observable.<T>create(e -> {
            if (webSocketChannel == null || !webSocketChannel.isOpen()) {
                e.onError(new NotConnectedException());
            }

            if (!channels.containsKey(channelId)) {
                Subscription newSubscription = new Subscription(e, channelName, args);
                channels.put(channelId, newSubscription);
                try {
                    sendMessage(getSubscribeMessage(channelName, args));
                } catch (IOException throwable) {
                    e.onError(throwable);
                }
            }
        }).doOnDispose(() -> {
            if (!channels.containsKey(channelId)) {
                sendMessage(getUnsubscribeMessage(channelId));
                channels.remove(channelId);
            }
        }).share();
    }

    private void resubscribeChannels() {
        for (Map.Entry<String, Subscription> channel : channels.entrySet()) {
            int resubscribeRetries = 3;

            do {
                try {
                    sendMessage(getSubscribeMessage(channel.getKey(), channel.getValue().args));
                    resubscribeRetries = 0;
                } catch (IOException e) {
                    LOG.error("Failed to resubscribe channel: {}", channel);
                    resubscribeRetries--;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {}
                }
            } while (resubscribeRetries > 0);
        }
    }

    protected String getChannel(T message) {
        String channel;
        try {
            channel = getChannelNameFromMessage(message);
        } catch (IOException e) {
            LOG.error("Cannot parse channel from message: {}", message);
            return "";
        }
        return channel;
    }

    protected void handleMessage(T message) {
        String channel = getChannel(message);
        handleChannelMessage(channel, message);
    }

    protected void handleError(T message, Throwable t) {
        String channel = getChannel(message);
        handleChannelError(channel, t);
    }


    protected void handleChannelMessage(String channel, T message) {
        ObservableEmitter<T> emitter = channels.get(channel).emitter;
        if (emitter == null) {
            LOG.debug("No subscriber for channel {}.", channel);
            return;
        }

        emitter.onNext(message);
    }

    protected void handleChannelError(String channel, Throwable t) {
        ObservableEmitter<T> emitter = channels.get(channel).emitter;
        if (emitter == null) {
            LOG.debug("No subscriber for channel {}.", channel);
            return;
        }

        emitter.onError(t);
    }

    protected WebSocketClientExtensionHandler getWebSocketClientExtensionHandler() {
        return WebSocketClientCompressionHandler.INSTANCE;
    }

    protected WebSocketClientHandler getWebSocketClientHandler(WebSocketClientHandshaker handshaker,
                                                               WebSocketClientHandler.WebSocketMessageHandler handler) {
        return new NettyWebSocketClientHandler(handshaker, handler);
    }

    private boolean isReconnectingWebsocket = false;

    protected void reconnectAndResubscribe() {
        if (!isReconnectingWebsocket) {
            isReconnectingWebsocket  = true;
            try {
                final Completable c = connect()
                        .doOnError(t -> LOG.warn("Problem with reconnect", t))
                        .retryWhen(new RetryWithDelay(retryDuration.toMillis()))
                        .doOnComplete(() -> {
                            LOG.info("Resubscribing channels");
                            resubscribeChannels();
                            isReconnectingWebsocket = false;
                        });
                c.subscribe();
            } catch (Exception e) {
                LOG.warn("Exception while socket reconnect! Message: " + e.getMessage());
            }
        }
    }

    private boolean isWebsocketWatcherRunning = false;

    private void startWebsocketHealthWatcher() {
        if (!isWebsocketWatcherRunning && isWebsocketWatcherSupported()) {
            isWebsocketWatcherRunning = true;
            LOG.info("Starting websocket health watcher for netty socket");
            new Thread(() -> {
                boolean isRunning = true;
                while (isRunning) {
                    if (isWebsocketReconnectRequired()) {
                        LOG.warn("Websocket needs to be reconnected, reconnecting ...");
                        reconnectAndResubscribe();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                        isRunning = false;
                        LOG.info("Stopping websocket health watcher because it was interrupted.");
                    }
                }
            }).start();
        }
    }

    /**
     * Override this method and return true to activate the websocket watcher thread.
     */
    protected boolean isWebsocketWatcherSupported() {
        return false;
    }

    /**
     * Calculate with the help of heartbeats if the websocket is still healthy and running correctly.
     */
    protected boolean isWebsocketReconnectRequired() {
        return false;
    }

    protected class NettyWebSocketClientHandler extends WebSocketClientHandler {
        protected NettyWebSocketClientHandler(WebSocketClientHandshaker handshaker, WebSocketMessageHandler handler) {
            super(handshaker, handler);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            if (isManualDisconnect) {
                isManualDisconnect = false;
            } else {
                super.channelInactive(ctx);
                LOG.info("Reopening websocket because it was closed by the host");
                reconnectAndResubscribe();
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            super.channelActive(ctx);
            startWebsocketHealthWatcher();
        }
    }

    public boolean isSocketOpen() {
        return webSocketChannel.isOpen();
    }
}
