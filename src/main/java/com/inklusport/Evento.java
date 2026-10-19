package com.inklusport;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Evento {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Min (value = 1, message = "El número de asistentes es obligatorio")
    private int capacidad;
}
