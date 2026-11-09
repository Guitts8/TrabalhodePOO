package br.com.hotelaria.api.dto;

import java.time.LocalDate;

public record FeriasRequest(LocalDate dataInicio, LocalDate dataFim) {
}
