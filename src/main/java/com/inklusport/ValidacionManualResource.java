
package com.inklusport;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/eventos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ValidacionManualResource {

    @Inject
    /**
     * Ejecuta las restricciones de Bean Validation.
     */
    Validator validator;

    @POST
    @Path("/validacion-manual")
    public Response validarManualmente(CrearEventoRequest request) {

        Set<ConstraintViolation<CrearEventoRequest>> errores =
                validator.validate(request); /** Revisa las anotaciones de tu DTO y devuelve un conjunto de infracciones. */

        /**
         * Indica si todos los datos cumplen las restricciones.
         */
        if (!errores.isEmpty()) {
            String mensajes = errores.stream()
                    /**
                     * Representa una regla incumplida y contiene su mensaje.
                     */
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "valido", false,
                            "errores", mensajes
                    ))
                    .build();
        }

        return Response.ok(Map.of(
                "valido", true,
                "mensaje", "El evento cumple las reglas de validación"
        )).build();
    }
}
