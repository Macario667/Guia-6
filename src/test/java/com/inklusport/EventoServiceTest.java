package com.inklusport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.ConstraintViolationException;

@QuarkusTest
class EventoServiceTest {

    @Inject
    EventoService eventoService;

    @Test
    void deveAceptarEventoValido() {
        CrearEventoRequest request = new CrearEventoRequest();
        request.nombre = "Copa Africana de Naciones";
        request.capacidad = 20;

        String resultado = eventoService.validarEvento(request);

        assertEquals("Evento válido: Copa Africana de Naciones", resultado);
    }

    @Test
    void debeRechazarEventoSinNombre() {
        CrearEventoRequest request= new CrearEventoRequest();
        request.nombre = " ";
        request.capacidad = 20;

        assertThrows(
            ConstraintViolationException.class,
            () -> eventoService.validarEvento(request)
        );
    }

    @Test
    void debeRechazarCapacidadCero() {
        CrearEventoRequest request = new CrearEventoRequest();
        request.nombre = "Copa Africana de Naciones";
        request.capacidad = 0;

        assertThrows(
            ConstraintViolationException.class,
            () -> eventoService.validarEvento(request)
        );
    }

    @Test
    void debeCalcularCuposDisponibles() {
        Integer resultado = eventoService.calcularCuposDisponibles(20, 15);

        assertEquals(5, resultado);
    }

    @Test
    void debeRechazarCuposDisponiblesNegativos() {
        assertThrows(
            ConstraintViolationException.class,
            () -> eventoService.calcularCuposDisponibles(5, 8)
        );
    }
}
