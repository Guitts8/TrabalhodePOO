package br.com.hotelaria.service;

import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.StatusQuarto;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.repository.HotelRepository;
import br.com.hotelaria.repository.QuartoRepository;
import br.com.hotelaria.repository.ReservaRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResumoService {
    public record Resumo(long hoteis, long quartos, long quartosDisponiveis, long quartosOcupados,
                         long hospedesNoMomento, long checkInsHoje, long checkOutsHoje, long reservasPendentes) {
    }

    public record OcupacaoHotel(String hotel, long quartos, long ocupados, long disponiveis) {
        @JsonProperty("taxaOcupacao")
        public double taxaOcupacao() {
            return quartos == 0 ? 0 : (double) ocupados / quartos;
        }
    }

    private final HotelRepository hotelRepository;
    private final QuartoRepository quartoRepository;
    private final ReservaRepository reservaRepository;

    public ResumoService(HotelRepository hotelRepository, QuartoRepository quartoRepository,
                         ReservaRepository reservaRepository) {
        this.hotelRepository = hotelRepository;
        this.quartoRepository = quartoRepository;
        this.reservaRepository = reservaRepository;
    }

    public Resumo gerarResumo() {
        LocalDate hoje = LocalDate.now();
        return new Resumo(
                hotelRepository.count(),
                quartoRepository.count(),
                quartoRepository.countByStatus(StatusQuarto.DISPONIVEL),
                quartoRepository.countByStatus(StatusQuarto.OCUPADO),
                reservaRepository.somarHospedes(StatusReserva.EM_ANDAMENTO),
                checkInsPrevistos(hoje).size(),
                checkOutsPrevistos(hoje).size(),
                reservaRepository.countByStatus(StatusReserva.PENDENTE));
    }

    public List<OcupacaoHotel> ocupacaoPorHotel() {
        List<OcupacaoHotel> lista = new ArrayList<>();
        for (Hotel hotel : hotelRepository.findAllByOrderByNomeAsc()) {
            long total = quartoRepository.countByHotelId(hotel.getId());
            long ocupados = quartoRepository.countByHotelIdAndStatus(hotel.getId(), StatusQuarto.OCUPADO);
            long disponiveis = quartoRepository.countByHotelIdAndStatus(hotel.getId(), StatusQuarto.DISPONIVEL);
            lista.add(new OcupacaoHotel(hotel.getNome(), total, ocupados, disponiveis));
        }
        return lista;
    }

    public List<Reserva> checkInsPrevistos(LocalDate dia) {
        return reservaRepository.findByDataEntradaAndStatusInOrderById(dia,
                List.of(StatusReserva.PENDENTE, StatusReserva.CONFIRMADA));
    }

    public List<Reserva> checkOutsPrevistos(LocalDate dia) {
        return reservaRepository.findByDataSaidaAndStatusOrderById(dia, StatusReserva.EM_ANDAMENTO);
    }
}
