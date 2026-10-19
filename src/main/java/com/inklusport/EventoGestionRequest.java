package com.inklusport;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public class EventoGestionRequest {

    @Null(
        groups = ValidationGroups.Crear.class,
        message = "El ID debe estar vacío al crear un evento"
    )
    @NotNull(
        groups = ValidationGroups.Actualizar.class,
        message = "El ID es obligatorio para actualizar un evento"
    )
    public Long id;

    @NotBlank(message = "{evento.nombre.obligatorio}")
    public String nombre;

    @NotNull(message = "{evento.capacidad.obligatoria}")
    @Min(value = 1, message = "{evento.capacidad.minima}")
    public Integer capacidad;
}
