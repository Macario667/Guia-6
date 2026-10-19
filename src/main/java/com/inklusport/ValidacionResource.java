package com.inklusport;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/validacion/eventos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ValidacionResource {

    @POST
    public Response crearEvento(@Valid CrearEventoRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(request)
                .build();
    }
}