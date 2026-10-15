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

        try {
            Log.debug("DIAGNOSTICO_INTERNO: iniciando validación");

            if (request == null
                || request.nombre() == null
                || request.nombre().isBlank()
                || request.usuarioId() == null) {

                Log.warn("Solicitud de creación inválida: faltan campos obligatorios (nombre, usuarioId)");

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of(
                            "error",
                            "El nombre y el usuario son obligatorios"
                        ))
                        .build();
            }

            Log.debug("Validación completada");
            Log.info("Solicitud de creación aceptada y procesada");

            return Response.status(Response.Status.CREATED)
                    .entity(Map.of(
                        "mensaje", "Evento procesado",
                        "requestId", requestId
                    ))
                    .build();

        } catch (RuntimeException e) {
            Log.error("No se pudo completar la creación del evento", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(
                        "error",
                        "No se pudo procesar la creación del evento",
                        "requestId", requestId
                    ))
                    .build();
        } finally {
            MDC.remove("request.id");
        }
    }
}
