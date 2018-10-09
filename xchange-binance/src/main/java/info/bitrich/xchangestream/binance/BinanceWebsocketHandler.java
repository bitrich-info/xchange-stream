package info.bitrich.xchangestream.binance;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameClientExtensionHandshaker;

@ChannelHandler.Sharable
public final class BinanceWebsocketHandler extends WebSocketClientExtensionHandler {

    public static final BinanceWebsocketHandler INSTANCE = new BinanceWebsocketHandler();

    private BinanceWebsocketHandler() {
        super(
                new DeflateFrameClientExtensionHandshaker(false),
                new DeflateFrameClientExtensionHandshaker(true)
        );
    }
}