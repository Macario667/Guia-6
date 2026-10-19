package com.inklusport;

import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/eventos/gestion")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventoGestionResource {

    @POST
    @Path("/crear")
    public String crear(
        @Valid
        @ConvertGroup(to = ValidationGroups.Crear.class)
        EventoGestionRequest request
    ) {
        return "Evento listo pata crear: " + request.nombre;
    }

    @PUT
    @Path("/actualizar")
    public String actualizar(
        @Valid
        @ConvertGroup(to = ValidationGroups.Actualizar.class)
        EventoGestionRequest request
    ) {
        return "Evento listo para actualizar: " + request.id;
    }
}
