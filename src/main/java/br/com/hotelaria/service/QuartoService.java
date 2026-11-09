package br.com.hotelaria.service;

import br.com.hotelaria.exception.RecursoNaoEncontradoException;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.StatusQuarto;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.repository.QuartoRepository;
import br.com.hotelaria.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuartoService {
    private final QuartoRepository quartoRepository;
    private final ReservaRepository reservaRepository;

    public QuartoService(QuartoRepository quartoRepository, ReservaRepository reservaRepository) {
        this.quartoRepository = quartoRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Quarto> listar() {
        return quartoRepository.findAllByOrderByHotelNomeAscNumeroAsc();
    }

    public List<Quarto> listarPorHotel(Long hotelId) {
        return quartoRepository.findByHotelIdOrderByNumeroAsc(hotelId);
    }

    public Quarto buscarPorId(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe o código do quarto.");
        }
        return quartoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Quarto não encontrado (código " + id + ")."));
    }

    @Transactional
    public Quarto salvar(Quarto quarto) {
        if (quarto.getHotel() == null) {
            throw new RegraNegocioException("Selecione o hotel do quarto.");
        }
        Long id = quarto.getId() == null ? 0L : quarto.getId();
        if (quartoRepository.existsByHotelIdAndNumeroIgnoreCaseAndIdNot(quarto.getHotel().getId(), quarto.getNumero(), id)) {
            throw new RegraNegocioException("Já existe um quarto com o número " + quarto.getNumero()
                    + " no " + quarto.getHotel().getNome() + ".");
        }
        return quartoRepository.save(quarto);
    }

    @Transactional
    public void excluir(Long id) {
        Quarto quarto = buscarPorId(id);
        if (reservaRepository.existsByQuartoId(id)) {
            throw new RegraNegocioException("O quarto " + quarto.getNumero() + " tem reservas registradas e não pode ser excluído. "
                    + "Se ele não vai mais ser usado, coloque-o em manutenção.");
        }
        quartoRepository.delete(quarto);
    }

    public List<Quarto> buscarDisponiveis(Long hotelId, LocalDate entrada, LocalDate saida, int hospedes,
                                          Long ignorarReservaId) {
        if (hotelId == null) {
            throw new RegraNegocioException("Selecione o hotel.");
        }
        Reserva.validarPeriodo(entrada, saida);
        if (hospedes < 1) {
            throw new RegraNegocioException("Informe a quantidade de hóspedes.");
        }

        List<Quarto> disponiveis = new ArrayList<>();
        for (Quarto quarto : quartoRepository.findByHotelIdOrderByNumeroAsc(hotelId)) {
            if (quarto.getStatus() != StatusQuarto.MANUTENCAO
                    && quarto.comportaHospedes(hospedes)
                    && buscarConflito(quarto, entrada, saida, ignorarReservaId) == null) {
                disponiveis.add(quarto);
            }
        }
        return disponiveis;
    }

    public Reserva buscarConflito(Quarto quarto, LocalDate entrada, LocalDate saida, Long ignorarReservaId) {
        List<Reserva> ativas = reservaRepository.findByQuartoIdAndStatusIn(quarto.getId(), StatusReserva.ATIVAS);
        for (Reserva reserva : ativas) {
            if (!reserva.getId().equals(ignorarReservaId) && reserva.conflitaCom(entrada, saida)) {
                return reserva;
            }
        }
        return null;
    }

    @Transactional
    public Quarto liberar(Long id) {
        Quarto quarto = buscarPorId(id);
        quarto.liberar();
        return quartoRepository.save(quarto);
    }

    @Transactional
    public Quarto colocarEmManutencao(Long id) {
        Quarto quarto = buscarPorId(id);
        quarto.colocarEmManutencao();
        return quartoRepository.save(quarto);
    }
}
