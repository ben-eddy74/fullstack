package com.app.health.boundary;

import com.app.health.entity.PingResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.Instant;

@Path("/ping")
public class PingResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PingResponse ping() {
        return new PingResponse("ok", Instant.now());
    }
}
