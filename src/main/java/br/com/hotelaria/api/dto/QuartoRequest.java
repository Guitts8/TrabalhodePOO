package br.com.hotelaria.api.dto;

import java.math.BigDecimal;

public record QuartoRequest(String tipo, Long hotelId, String numero, Integer andar, Integer capacidade,
                            BigDecimal valorDiaria, String descricao,
                            Boolean camaCasal, Boolean vistaMar, Boolean hidromassagem,
                            BigDecimal valorHospedeAdicional) {
}
