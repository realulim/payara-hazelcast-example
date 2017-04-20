package de.mayring.payarahazelcastexample;

import java.util.Map;
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
    private Map<String, String> storedData = null;

    @Context UriInfo uriInfo;

    private String responseForGet(String color, String uuid, String uriStore, String uriPurge) {
        return "<html><h1 style='color:" + color + "; font-family: sans-serif'>" + uuid + "</h1>" +
            "<form action='" + uriStore + "' method='POST'><input type='submit' value='store'><input type='hidden' name='color' value='" + color + "'></form>" +
            "<form action='" + uriPurge + "' method='POST'><input type='submit' value='purge'></form>" +
            getStoredData() + "</html>";
    }

    private String getStoredData() {
        StringBuilder sb = new StringBuilder("");
        if (storedData != null) {
            sb.append("<ol>");
            for (Map.Entry<String, String> entry : storedData.entrySet()) {
                sb.append("<li style='color:" + entry.getValue() + "'>").append(entry.getKey()).append("</li>");
            }
            sb.append("</ol>");
        }
        return sb.toString();
    }

    private String responseForStore(String color, String uuid, String uri) {
        return "<html><h1 style='color:" + color + "; font-family: sans-serif'>" + uuid + " stored.</h1>" +
            "<a href='" + uri + "'>continue</a>" + getStoredData() + "</html>";
    }

    private String responseForPurge(String color, String uri) {
        return "<html><h1 style='color:" + color + "; font-family: sans-serif'>Purged.</h1>" +
            "<a href='" + uri + "'>continue</a>" + getStoredData() + "</html>";
    }

    public UuidService() {
        HazelcastInstance hazelcast = ApplicationConfig.getHazelcast();
        if (hazelcast == null) {
            throw new WebApplicationException("Hazelcast not initialised yet", Response.Status.INTERNAL_SERVER_ERROR);
        }
        else {
            IMap<String, String> colorsInUse = hazelcast.getMap(ApplicationConfig.COLORS);
            this.color = colorsInUse.get(hazelcast.getCluster().getLocalMember().getUuid());
            this.storedData = hazelcast.getMap(ApplicationConfig.STORED);
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getRandom() {
        String uuid = UUID.randomUUID().toString();
        String uri = uriInfo.getAbsolutePath().toString();
        return responseForGet(this.color, uuid, uri + "/" + uuid, uri + "/purge");
    }

    @POST
    @Path("/purge")
    @Produces(MediaType.TEXT_HTML)
    public String purge() {
        this.storedData.clear();
        Logger.getAnonymousLogger().info("Purged.");
        String uri = uriInfo.getAbsolutePath().toString();
        return responseForPurge(color, uri.substring(0, uri.length() - 6));
    }

    @POST
    @Path("{uuidToSave}")
    @Produces(MediaType.TEXT_HTML)
    public String store(@PathParam("uuidToSave") String uuidToStore, @FormParam("color") final String color) {
        this.storedData.put(uuidToStore, color);
        Logger.getAnonymousLogger().info("Stored " + uuidToStore + ". New size: " + storedData.size());
        String uri = uriInfo.getAbsolutePath().toString();
        int end = uri.length() - (uuidToStore.length() + 1);
        return responseForStore(color, uuidToStore, uri.substring(0, end));
    }

}
