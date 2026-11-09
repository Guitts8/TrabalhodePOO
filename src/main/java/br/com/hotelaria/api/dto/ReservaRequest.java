package br.com.hotelaria.api.dto;

import java.time.LocalDate;

public record ReservaRequest(Long hospedeId, Long quartoId, LocalDate dataEntrada, LocalDate dataSaida,
                             Integer quantidadeHospedes, String observacoes) {
    public int hospedes() {
        return quantidadeHospedes == null ? 1 : quantidadeHospedes;
    }
}
