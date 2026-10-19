package com.inklusport;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrearEventoRequest {

    @NotBlank(message = "El nombre del evento es obligatorio")
    public String nombre;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor que cero")
    public Integer capacidad;
}
