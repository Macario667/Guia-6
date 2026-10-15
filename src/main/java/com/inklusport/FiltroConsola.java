package com.inklusport;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import io.quarkus.logging.LoggingFilter;

/**
 * 
 * FiltroConsola
 */
@LoggingFilter(name = "filtro-consola")
public final class FiltroConsola implements Filter {
    
    /**    (non-Javadoc)
     * El método isLoggable devuelve true si el registro puede pasar, 
     * y false si debe descartarse.
     * @see java.util.logging.Filter#isLoggable(java.util.logging.LogRecord)
     */
    @Override 
    public boolean isLoggable(LogRecord record) {
        String mensaje = record.getMessage();

        return mensaje == null
                || !mensaje.contains("DIAGNOSTICO_INTERNO");
    }
}
