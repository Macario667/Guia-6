package com.inklusport;

import org.jboss.logging.Logger;

public class EventoService {

    private static final Logger LOG =
        Logger.getLogger(EventoService.class);

    public void registrarEvento(String nombre) {
        LOG.infof("Registrando evento: %s", nombre);
    }
}