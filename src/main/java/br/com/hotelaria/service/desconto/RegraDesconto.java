package br.com.hotelaria.service.desconto;

import br.com.hotelaria.model.Reserva;

import java.math.BigDecimal;

public interface RegraDesconto {
    BigDecimal calcularDesconto(Reserva reserva);

    String getDescricao();
}
