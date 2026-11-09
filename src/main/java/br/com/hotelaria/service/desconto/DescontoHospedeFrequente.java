package br.com.hotelaria.service.desconto;

import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.repository.ReservaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DescontoHospedeFrequente implements RegraDesconto {
    private static final long HOSPEDAGENS_MINIMAS = 3;
    private static final BigDecimal PERCENTUAL = new BigDecimal("0.05");

    private final ReservaRepository reservaRepository;

    public DescontoHospedeFrequente(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Override
    public BigDecimal calcularDesconto(Reserva reserva) {
        Long hospedeId = reserva.getHospede().getId();
        long finalizadas = reservaRepository.countByHospedeIdAndStatus(hospedeId, StatusReserva.FINALIZADA);
        if (finalizadas < HOSPEDAGENS_MINIMAS) {
            return BigDecimal.ZERO;
        }
        return reserva.getValorHospedagem().multiply(PERCENTUAL).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getDescricao() {
        return "Hóspede frequente (5%)";
    }
}
