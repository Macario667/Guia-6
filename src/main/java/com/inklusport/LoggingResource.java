package com.inklusport;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("/logs")
@Produces(MediaType.TEXT_PLAIN)
public class LoggingResource {

    private static final Logger LOG = Logger.getLogger(LoggingResource.class);

    @GET
    @Path("/demo")
    public String demo() {
        LOG.trace("[DEMO] Ejemplo de registro TRACE");
        LOG.debug("[DEMO] Ejemplo de registro DEBUG");
        LOG.info("[DEMO] Ejemplo de registro INFO");
        LOG.warn("[DEMO] Ejemplo de registro WARN");
        LOG.error("[DEMO] Ejemplo de registro ERROR");
        LOG.fatal("[DEMO] Ejemplo de registro FATAL");
        LOG.info("[DEMO] DIAGNOSTICO_INTERNO: el filtro lo oculta de la consola");

        return "Se emitieron registros de TRACE a FATAL. Revisa la consola y logs/eventos.log.";
    }
}
