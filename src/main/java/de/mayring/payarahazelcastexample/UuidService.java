package de.mayring.payarahazelcastexample;

import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Path("uuid")
public class UuidService {

    private String color = null;

    @Context UriInfo uriInfo;

    private final String outputOnGet = "<html><h1 style='color:%1$s; font-family: sans-serif'>%2$s</h1>" +
            "<form action='%3$s' method='POST'><input type='submit' value='store'><input type='hidden' name='color' value='%1$s'></form></html>";

    private final String outputOnPost = "<html><h1 style='color:%1$s; font-family: sans-serif'>%2$s stored.</h1>" +
            "<a href='%3$s'>continue</a></html>";

    public UuidService() {
        HazelcastInstance hazelcast = ApplicationConfig.getHazelcast();
        if (hazelcast == null) {
            throw new WebApplicationException("Hazelcast not initialised yet", Response.Status.INTERNAL_SERVER_ERROR);
        }
        else {
            IMap<String, String> colorsInUse = hazelcast.getMap(ApplicationConfig.COLORS);
            this.color = colorsInUse.get(hazelcast.getCluster().getLocalMember().getUuid());
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getRandom() {
        String uuid = UUID.randomUUID().toString();
        return String.format(outputOnGet, this.color, uuid, uriInfo.getAbsolutePath().toString() + "/" + uuid);
    }

    @POST
    @Path("{uuidToSave}")
    @Produces(MediaType.TEXT_HTML)
    public String save(@PathParam("uuidToSave") String uuidToStore, @FormParam("color") final String color) {
        String uri = uriInfo.getAbsolutePath().toString();
        int end = uri.length() - (uuidToStore.length() + 1);
        return String.format(outputOnPost, color, uuidToStore, uri.substring(0, end));
    }

}
