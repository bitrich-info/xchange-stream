package info.bitrich.xchangestream.okcoin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.okcoin.dto.WebSocketMessage;
import info.bitrich.xchangestream.service.netty.JsonNettyStreamingService;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import info.bitrich.xchangestream.service.netty.WebSocketClientHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import org.knowm.xchange.exceptions.ExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

public class OkCoinV3StreamingService extends JsonNettyStreamingService {

  private Observable<Long> pingPongSrc = Observable.interval(15, 15, TimeUnit.SECONDS);

  private Disposable pingPongSubscription;

  public OkCoinV3StreamingService(String apiUrl) {
    super(apiUrl);
  }

  @Override
  public Completable connect() {
    Completable conn = super.connect();
    return conn.andThen(
        (CompletableSource)
            (completable) -> {
              try {
                if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
                  pingPongSubscription.dispose();
                }
                pingPongSubscription =
                    pingPongSrc.subscribe(
                        o -> {
                          this.sendMessage("ping");
                        });
                completable.onComplete();
              } catch (Exception e) {
                completable.onError(e);
              }
            });
  }

  @Override
  protected String getChannelNameFromMessage(JsonNode message) throws IOException {
    JsonNode table = message.get("table");
    if (table != null) {
      JsonNode data = message.get("data");
      JsonNode instrument_id = data.get(0).get("instrument_id");
      String pair = instrument_id.toString();
      String result = table + ":" + pair;
      return result.replace("\"", "");
    }
    String event = message.get("event").asText();
    return event;
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {

    return String.format("{\"op\": \"subscribe\", \"args\": [\"%s\"]}", channelName);
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    return String.format("{\"op\": \"subscribe\", \"args\": [\"%s\"]}", channelName);
  }

  @Override
  protected void handleMessage(JsonNode message) {
    if (message.get("event") != null && "pong".equals(message.get("event").asText())) {
      // ignore pong message
      return;
    }
    super.handleMessage(message);
  }

  @Override
  protected WebSocketClientHandler getWebSocketClientHandler(
      WebSocketClientHandshaker handshaker,
      WebSocketClientHandler.WebSocketMessageHandler handler) {
    return new OkCoinNettyWebSocketClientHandler(handshaker, handler);
  }

  protected class OkCoinNettyWebSocketClientHandler extends NettyWebSocketClientHandler {

    private final Logger LOG = LoggerFactory.getLogger(OkCoinNettyWebSocketClientHandler.class);

    protected OkCoinNettyWebSocketClientHandler(
        WebSocketClientHandshaker handshaker, WebSocketMessageHandler handler) {
      super(handshaker, handler);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
      if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
        pingPongSubscription.dispose();
      }
      super.channelInactive(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

      if (!handshaker.isHandshakeComplete()) {
        super.channelRead0(ctx, msg);
        return;
      }

      super.channelRead0(ctx, msg);

      WebSocketFrame frame = (WebSocketFrame) msg;
      if (frame instanceof BinaryWebSocketFrame) {
        BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
        ByteBuf byteBuf = binaryFrame.content();
        byte[] temp = new byte[byteBuf.readableBytes()];
        ByteBufInputStream bis = new ByteBufInputStream(byteBuf);
        StringBuilder appender = new StringBuilder();
        try {
          bis.read(temp);
          bis.close();
          Inflater infl = new Inflater(true);
          infl.setInput(temp, 0, temp.length);
          byte[] result = new byte[1024];
          while (!infl.finished()) {
            int length = infl.inflate(result);
            appender.append(new String(result, 0, length, "UTF-8"));
          }
          infl.end();
        } catch (Exception e) {
          LOG.trace("Error when inflate websocket binary message");
        }

        String message = appender.toString();
        if (message.equals("pong")) return;
        handler.onMessage(message);
      }
    }
  }
}
