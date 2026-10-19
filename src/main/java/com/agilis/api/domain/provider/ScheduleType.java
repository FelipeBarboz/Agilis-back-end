package com.agilis.api.domain.provider;

public enum ScheduleType {
    STORE_HOURS, // segue o horário da loja, com cota de dias/horas mensais
    FIXED,       // horário e dias fixos e recorrentes
    FLEXIBLE     // totalmente editável, dia a dia
}