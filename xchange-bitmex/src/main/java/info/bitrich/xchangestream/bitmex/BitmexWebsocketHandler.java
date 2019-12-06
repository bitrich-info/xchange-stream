package info.bitrich.xchangestream.bitmex;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.DeflateFrameClientExtensionHandshaker;

@ChannelHandler.Sharable
public final class BitmexWebsocketHandler extends WebSocketClientExtensionHandler {

    public static final BitmexWebsocketHandler INSTANCE = new BitmexWebsocketHandler();

    private BitmexWebsocketHandler() {
        super(
            new DeflateFrameClientExtensionHandshaker(false),
            new DeflateFrameClientExtensionHandshaker(true)
        );
    }

}