package com.inklusport;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FiltroConsolaTest {

    private final FiltroConsola filtro = new FiltroConsola();

    @Test
    void descartaMensajesDeDiagnosticoInterno() {
        LogRecord record = new LogRecord(Level.INFO, "DIAGNOSTICO_INTERNO: ejemplo");

        assertFalse(filtro.isLoggable(record));
    }

    @Test
    void conservaMensajesNormales() {
        LogRecord record = new LogRecord(Level.INFO, "Solicitud procesada");

        assertTrue(filtro.isLoggable(record));
    }
}
