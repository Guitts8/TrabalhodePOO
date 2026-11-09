package br.com.hotelaria.api.dto;

import br.com.hotelaria.model.Reserva;

import java.math.BigDecimal;

public record SimulacaoResponse(String quarto, String tipo, long diarias, BigDecimal valorDiaria,
                                BigDecimal valorHospedagem, BigDecimal desconto, String descricaoDesconto,
                                BigDecimal totalPrevisto) {
    public static SimulacaoResponse de(Reserva reserva) {
        return new SimulacaoResponse(
                reserva.getQuarto().getNumero(),
                reserva.getQuarto().getTipo().name(),
                reserva.getQuantidadeDiarias(),
                reserva.getValorDiaria(),
                reserva.getValorHospedagem(),
                reserva.getValorDesconto(),
                reserva.getDescricaoDesconto(),
                reserva.getValorTotal());
    }
}
