package info.bitrich.xchangestream.coindirect;

import info.bitrich.xchangestream.coindirect.dto.CoindirectUserToken;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface CoindirectToken {
    @GET
    @Path("user-token")
    CoindirectUserToken getUserToken() throws IOException;
}
