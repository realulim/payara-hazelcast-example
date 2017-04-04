package de.mayring.payarahazelcastexample;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;

@Path("uuid")
public class UuidService {

    private final HazelcastInstance hazelcast;

    @Context UriInfo uriInfo;

    private final String outputOnGet = "<html><h2 style='color:%1$s; font-family: sans-serif'>%2$s</h2>" +
            "<form action='%3$s' method='POST'><input type='submit' value='store'><input type='hidden' name='color' value='%1$s'></form></html>";

    private final String outputOnPost = "<html><h2 style='color:%1$s; font-family: sans-serif'>%2$s stored.</h2>" +
            "<a href='%3$s'>continue</a></html>";

    public UuidService() throws NamingException {
        javax.naming.Context ctx = new InitialContext();
        hazelcast = (HazelcastInstance) ctx.lookup("payara/Hazelcast");
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getRandom() {
        String uuid = UUID.randomUUID().toString();
        String color = getRandomColor();
        return String.format(outputOnGet, color, uuid, uriInfo.getAbsolutePath().toString() + "/" + uuid);
    }

    @POST
    @Path("{uuidToSave}")
    @Produces(MediaType.TEXT_HTML)
    public String save(@PathParam("uuidToSave") String uuidToStore, @FormParam("color") final String color) {
        String uri = uriInfo.getAbsolutePath().toString();
        int end = uri.length() - (uuidToStore.length() + 1);
        return String.format(outputOnPost, color, uuidToStore, uri.substring(0, end));
    }

    private String getRandomColor() {
        IList<String> colors = hazelcast.getList("colors");
        return colors.get(ThreadLocalRandom.current().nextInt(0, colors.size()));
    }

}
