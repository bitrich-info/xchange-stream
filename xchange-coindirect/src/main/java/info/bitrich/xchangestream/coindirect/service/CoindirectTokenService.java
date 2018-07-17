package info.bitrich.xchangestream.coindirect.service;

import info.bitrich.xchangestream.coindirect.CoindirectToken;
import info.bitrich.xchangestream.coindirect.dto.CoindirectUserToken;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;

public class CoindirectTokenService {
    private CoindirectToken coindirectToken;

    public CoindirectTokenService(String tokenUrl) {
        coindirectToken = RestProxyFactory.createProxy(CoindirectToken.class, tokenUrl);
    }

    public CoindirectUserToken getUserToken() throws IOException {
        return coindirectToken.getUserToken();
    }
}
