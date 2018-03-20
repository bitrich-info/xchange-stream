package info.bitrich.xchangestream.hitbtc.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.hitbtc.HitbtcStreamingService;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HitbtcStreamingServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getChannelNameFromMessageTest() throws IOException, InvocationTargetException, IllegalAccessException {

        String json1 = "{\"method\":\"aaa\"}";
        String json2 = "{ \"method\": \"updateOrderbook\", \"params\": { \"symbol\": \"ETHBTC\" } }";
        String json3 = "{ \"method\": \"snapshotOrderbook\", \"params\": { \"symbol\": \"ETHBTC\" } }";
        String json4 = "{ \"method\": \"test\", \"params\": { \"symbol\": \"ETHBTC\" } }";
        String json5 = "{ \"noMethod\": \"updateOrderbook\" } }";

        HitbtcStreamingService hitbtcStreamingService = new HitbtcStreamingService("testUrl");
        Method method = MethodUtils.getMatchingMethod(HitbtcStreamingService.class, "getChannelNameFromMessage", JsonNode.class);
        method.setAccessible(true);

        Assert.assertEquals("aaa", method.invoke(hitbtcStreamingService, objectMapper.readTree(json1)));
        Assert.assertEquals("orderbook-ETHBTC", method.invoke(hitbtcStreamingService, objectMapper.readTree(json2)));
        Assert.assertEquals("orderbook-ETHBTC", method.invoke(hitbtcStreamingService, objectMapper.readTree(json3)));

        Assert.assertEquals("test-ETHBTC", method.invoke(hitbtcStreamingService, objectMapper.readTree(json4)));
        Throwable exception = null;
        try {
            method.invoke(hitbtcStreamingService, objectMapper.readTree(json5));
        } catch (InvocationTargetException e) {
            exception = e.getTargetException();
        }
        Assert.assertNotNull(exception);
        Assert.assertEquals(IOException.class, exception.getClass());

    }
}
