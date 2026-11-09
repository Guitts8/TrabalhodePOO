package br.com.hotelaria.service.desconto;

import br.com.hotelaria.model.Reserva;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DescontoLongaPermanencia implements RegraDesconto {
    private static final int DIARIAS_MINIMAS = 7;
    private static final BigDecimal PERCENTUAL = new BigDecimal("0.10");

    @Override
    public BigDecimal calcularDesconto(Reserva reserva) {
        if (reserva.getQuantidadeDiarias() < DIARIAS_MINIMAS) {
            return BigDecimal.ZERO;
        }
        return reserva.getValorHospedagem().multiply(PERCENTUAL).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescricao() {
        return "Longa permanência, 7+ diárias (10%)";
    }
}
