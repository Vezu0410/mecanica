package com.garageautobot.garagemautobot.entities;

public enum PeriodoAgendamento {
    MANHA,
    TARDE;

    public String getDescricao() {
        return switch (this) {
            case MANHA -> "Manhã";
            case TARDE -> "Tarde";
        };
    }
}