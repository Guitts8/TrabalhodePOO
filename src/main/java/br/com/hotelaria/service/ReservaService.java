package br.com.hotelaria.service;

import br.com.hotelaria.exception.RecursoNaoEncontradoException;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.ConsumoServico;
import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.Servico;
import br.com.hotelaria.model.StatusQuarto;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.repository.ConsumoServicoRepository;
import br.com.hotelaria.repository.ReservaRepository;
import br.com.hotelaria.service.desconto.RegraDesconto;
import br.com.hotelaria.util.Formatos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final ConsumoServicoRepository consumoRepository;
    private final QuartoService quartoService;
    private final HospedeService hospedeService;
    private final ServicoService servicoService;

    private final List<RegraDesconto> regrasDesconto;

    public ReservaService(ReservaRepository reservaRepository, ConsumoServicoRepository consumoRepository,
                          QuartoService quartoService, HospedeService hospedeService, ServicoService servicoService,
                          List<RegraDesconto> regrasDesconto) {
        this.reservaRepository = reservaRepository;
        this.consumoRepository = consumoRepository;
        this.quartoService = quartoService;
        this.hospedeService = hospedeService;
        this.servicoService = servicoService;
        this.regrasDesconto = regrasDesconto;
    }

    public List<Reserva> listar() {
        return reservaRepository.findAllByOrderByDataEntradaDesc();
    }

    public List<Reserva> listarPorStatus(StatusReserva status) {
        if (status == null) {
            return listar();
        }
        return reservaRepository.findByStatusOrderByDataEntradaDesc(status);
    }

    public Reserva buscarPorId(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe o número da reserva.");
        }
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada (número " + id + ")."));
    }

    public Reserva simular(Long hospedeId, Long quartoId, LocalDate entrada, LocalDate saida, int hospedes,
                           Long reservaId) {
        Hospede hospede = hospedeService.buscarPorId(hospedeId);
        Quarto quarto = quartoService.buscarPorId(quartoId);
        Reserva reserva = new Reserva(hospede, quarto, entrada, saida, hospedes);
        validar(reserva, reservaId);
        calcularValores(reserva);
        return reserva;
    }

    @Transactional
    public Reserva criar(Long hospedeId, Long quartoId, LocalDate entrada, LocalDate saida, int hospedes,
                         String observacoes) {
        Hospede hospede = hospedeService.buscarPorId(hospedeId);
        Quarto quarto = quartoService.buscarPorId(quartoId);
        Reserva reserva = new Reserva(hospede, quarto, entrada, saida, hospedes);
        reserva.setObservacoes(observacoes);
        validar(reserva, null);
        calcularValores(reserva);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva alterar(Long id, Long hospedeId, Long quartoId, LocalDate entrada, LocalDate saida, int hospedes,
                           String observacoes) {
        Reserva reserva = buscarPorId(id);
        reserva.alterarDados(hospedeService.buscarPorId(hospedeId), quartoService.buscarPorId(quartoId),
                entrada, saida, hospedes);
        reserva.setObservacoes(observacoes);
        validar(reserva, id);
        calcularValores(reserva);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public void excluir(Long id) {
        Reserva reserva = buscarPorId(id);
        if (reserva.getStatus() != StatusReserva.PENDENTE && reserva.getStatus() != StatusReserva.CANCELADA) {
            throw new RegraNegocioException("Só é possível excluir reservas pendentes ou canceladas. "
                    + "Reservas confirmadas devem ser canceladas antes, e as finalizadas ficam no histórico do hóspede.");
        }
        reservaRepository.delete(reserva);
    }

    @Transactional
    public Reserva confirmar(Long id) {
        Reserva reserva = buscarPorId(id);
        reserva.confirmar();
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva cancelar(Long id) {
        Reserva reserva = buscarPorId(id);
        reserva.cancelar();
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva fazerCheckIn(Long id) {
        Reserva reserva = buscarPorId(id);
        reserva.fazerCheckIn(LocalDateTime.now());
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva fazerCheckOut(Long id) {
        Reserva reserva = buscarPorId(id);
        reserva.fazerCheckOut(LocalDateTime.now());
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva adicionarServico(Long reservaId, Long servicoId, int quantidade) {
        Reserva reserva = buscarPorId(reservaId);
        Servico servico = servicoService.buscarPorId(servicoId);
        ConsumoServico consumo = reserva.adicionarConsumo(servico, quantidade, LocalDateTime.now());
        consumoRepository.save(consumo);
        return reserva;
    }

    @Transactional
    public Reserva removerServico(Long reservaId, Long consumoId) {
        Reserva reserva = buscarPorId(reservaId);
        reserva.removerConsumo(consumoId);
        return reserva;
    }

    private void validar(Reserva reserva, Long ignorarReservaId) {
        Reserva.validarPeriodo(reserva.getDataEntrada(), reserva.getDataSaida());
        if (reserva.getDataEntrada().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("A data de entrada não pode ser anterior a hoje.");
        }

        Hospede hospede = reserva.getHospede();
        if (!hospede.isMaiorDeIdade()) {
            throw new RegraNegocioException(hospede.getNome() + " é menor de idade. "
                    + "O responsável pela reserva precisa ter 18 anos ou mais.");
        }

        Quarto quarto = reserva.getQuarto();
        if (reserva.getQuantidadeHospedes() < 1) {
            throw new RegraNegocioException("A reserva precisa ter pelo menos 1 hóspede.");
        }
        if (!quarto.comportaHospedes(reserva.getQuantidadeHospedes())) {
            throw new RegraNegocioException("O quarto " + quarto.getNumero() + " comporta no máximo "
                    + quarto.getCapacidade() + " hóspede(s).");
        }
        if (quarto.getStatus() == StatusQuarto.MANUTENCAO) {
            throw new RegraNegocioException("O quarto " + quarto.getNumero() + " está em manutenção e não pode ser reservado.");
        }

        Reserva conflito = quartoService.buscarConflito(quarto, reserva.getDataEntrada(), reserva.getDataSaida(),
                ignorarReservaId);
        if (conflito != null) {
            throw new RegraNegocioException("O quarto " + quarto.getNumero() + " já está reservado de "
                    + Formatos.data(conflito.getDataEntrada()) + " a " + Formatos.data(conflito.getDataSaida())
                    + " (reserva nº " + conflito.getId() + "). Escolha outro quarto ou outras datas.");
        }
    }

    public void calcularValores(Reserva reserva) {
        reserva.calcularValorHospedagem();
        for (RegraDesconto regra : regrasDesconto) {
            reserva.aplicarDesconto(regra.calcularDesconto(reserva), regra.getDescricao());
        }
    }
}
