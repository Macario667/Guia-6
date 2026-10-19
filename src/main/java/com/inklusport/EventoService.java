package com.inklusport;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@ApplicationScoped /** registra la clase como un bean CDI administrado por Quarkus. */
public class EventoService {

    /**
     * Solo se ejecuta esta lógica cuando los datos sean válidos
     * @param request
     * @return
     */
    public String validarEvento(@Valid CrearEventoRequest request) {
        return "Evento válido: " + request.nombre;
    }

    @NotNull(message = "La capacidad disponible no puede ser nula")
    @Min(value = 0, message = "La capacidad disponible no puede ser negativa")
    public Integer calcularCuposDisponibles(
        Integer capacidadTotal,
        Integer inscritos
    ) {
        return capacidadTotal - inscritos;
    }
}