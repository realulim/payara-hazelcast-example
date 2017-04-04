package de.mayring.payarahazelcastexample;

import java.util.UUID;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path("uuid")
public class UuidService {

    @Context UriInfo uriInfo;

    private final String outputOnGet = "<html><h2 style='color:%1$s; font-family: sans-serif'>%2$s</h2>" +
            "<form action='%3$s' method='POST'><input type='submit' value='store'></form></html>";

    private final String outputOnPost = "<html><h2 style='color:%1$s; font-family: sans-serif'>%2$s stored.</h2>" +
            "<a href='%3$s'>continue</a></html>";

    public UuidService() {
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getRandom() {
        String uuid = UUID.randomUUID().toString();
        String color = ApplicationConfig.getColor();
        return String.format(outputOnGet, color, uuid, uriInfo.getAbsolutePath().toString() + "/" + uuid + "?color=" + color);
    }

    @POST
    @Path("{uuidToSave}")
    @Produces(MediaType.TEXT_HTML)
    public String save(@PathParam("uuidToSave") String uuidToStore, @QueryParam("color") final String color) {
        String uri = uriInfo.getAbsolutePath().toString();
        int end = uri.length() - (uuidToStore.length() + 1);
        return String.format(outputOnPost, color, uuidToStore, uri.substring(0, end));
    }

}
