package de.mayring.payarahazelcastexample;

import java.util.UUID;

import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("uuid")
public class UuidService {

    public UuidService() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getRandom() {
        return Json.createArrayBuilder().add(UUID.randomUUID().toString()).build().toString();
    }

}
