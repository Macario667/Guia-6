package com.inklusport;

import java.util.Map;
import java.util.UUID;

import org.jboss.logging.MDC;

import io.quarkus.logging.Log;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/eventos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventoResource {

    @POST
    public Response crearEvento(CrearEventoRequest request) {

        String requestId = UUID.randomUUID().toString();
        MDC.put("request.id", requestId);
        
        if (request == null
                || request.nombre() == null
                || request.nombre().isBlank()
                || request.usuarioId() == null) {

            Log.warn("Solicitud de creación de evento inválida");

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                        "error",
                        "El nombre y el usuario son obligatorios"
                    ))
                    .build();
        }

        Log.infof(
            "Validando solicitud de creación. usuarioId=%d",
            request.usuarioId()
        );

        Log.infof(
            "Solicitud de creación de evento. usuarioId=%d",
            request.usuarioId()
        );
        try {
            /**
             * Simulación de la creación del evento.
             * Todavía no guardamos datos en una base de datos.
             */
            Log.infof(
                "Evento procesado correctamente. usuarioId=%d",
                request.usuarioId()
            );

            return Response.status(Response.Status.CREATED)
                    .entity(Map.of(
                        "mensaje", "Evento creado correctamente",
                        "nombre", request.nombre()
                    ))
                    .build();
        } catch (RuntimeException e) {
            Log.errorf(
                e,
                "Error al procesar la creación del evento"
            );
            throw e;
        }
    }
}