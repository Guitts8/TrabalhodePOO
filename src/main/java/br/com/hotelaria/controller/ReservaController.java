package br.com.hotelaria.controller;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.ConsumoServico;
import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.Servico;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.service.HospedeService;
import br.com.hotelaria.service.HotelService;
import br.com.hotelaria.service.QuartoService;
import br.com.hotelaria.service.ReservaService;
import br.com.hotelaria.service.ServicoService;
import br.com.hotelaria.util.Validador;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ReservaController {
    private final ReservaService reservaService;
    private final HospedeService hospedeService;
    private final HotelService hotelService;
    private final QuartoService quartoService;
    private final ServicoService servicoService;

    public ReservaController(ReservaService reservaService, HospedeService hospedeService, HotelService hotelService,
                             QuartoService quartoService, ServicoService servicoService) {
        this.reservaService = reservaService;
        this.hospedeService = hospedeService;
        this.hotelService = hotelService;
        this.quartoService = quartoService;
        this.servicoService = servicoService;
    }

    public List<Reserva> listar(StatusReserva situacao, String filtro) {
        List<Reserva> reservas = reservaService.listarPorStatus(situacao);
        if (Validador.isVazio(filtro)) {
            return reservas;
        }
        String texto = filtro.trim().toLowerCase();
        List<Reserva> encontradas = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getHospede().getNome().toLowerCase().contains(texto)
                    || String.valueOf(reserva.getId()).equals(texto)
                    || reserva.getQuarto().getHotel().getNome().toLowerCase().contains(texto)
                    || reserva.getQuarto().getNumero().equalsIgnoreCase(texto)) {
                encontradas.add(reserva);
            }
        }
        return encontradas;
    }

    public List<Hospede> listarHospedes() {
        return hospedeService.listar();
    }

    public List<Hotel> listarHoteis() {
        return hotelService.listar();
    }

    public List<Quarto> quartosDoHotel(Hotel hotel) {
        if (hotel == null) {
            return List.of();
        }
        return quartoService.listarPorHotel(hotel.getId());
    }

    public List<Servico> servicosAtivos() {
        return servicoService.listarAtivos();
    }

    public Set<Long> quartosLivres(Hotel hotel, LocalDate entrada, LocalDate saida, int hospedes, Reserva emEdicao) {
        Long ignorar = emEdicao == null ? null : emEdicao.getId();
        Set<Long> ids = new HashSet<>();
        for (Quarto quarto : quartoService.buscarDisponiveis(hotel.getId(), entrada, saida, hospedes, ignorar)) {
            ids.add(quarto.getId());
        }
        return ids;
    }

    public Reserva simular(Hospede hospede, Quarto quarto, LocalDate entrada, LocalDate saida, int hospedes,
                           Reserva emEdicao) {
        validarSelecao(hospede, quarto);
        Long id = emEdicao == null ? null : emEdicao.getId();
        return reservaService.simular(hospede.getId(), quarto.getId(), entrada, saida, hospedes, id);
    }

    public Reserva salvar(Reserva emEdicao, Hospede hospede, Quarto quarto, LocalDate entrada, LocalDate saida,
                          int hospedes, String observacoes) {
        validarSelecao(hospede, quarto);
        if (emEdicao == null) {
            return reservaService.criar(hospede.getId(), quarto.getId(), entrada, saida, hospedes, observacoes);
        }
        return reservaService.alterar(emEdicao.getId(), hospede.getId(), quarto.getId(), entrada, saida, hospedes,
                observacoes);
    }

    public void excluir(Reserva reserva) {
        reservaService.excluir(reserva.getId());
    }

    public Reserva confirmar(Reserva reserva) {
        return reservaService.confirmar(reserva.getId());
    }

    public Reserva cancelar(Reserva reserva) {
        return reservaService.cancelar(reserva.getId());
    }

    public Reserva fazerCheckIn(Reserva reserva) {
        return reservaService.fazerCheckIn(reserva.getId());
    }

    public Reserva fazerCheckOut(Reserva reserva) {
        return reservaService.fazerCheckOut(reserva.getId());
    }

    public Reserva recarregar(Reserva reserva) {
        return reservaService.buscarPorId(reserva.getId());
    }

    public Reserva lancarServico(Reserva reserva, Servico servico, int quantidade) {
        if (servico == null) {
            throw new RegraNegocioException("Selecione o serviço que será lançado.");
        }
        return reservaService.adicionarServico(reserva.getId(), servico.getId(), quantidade);
    }

    public Reserva removerServico(Reserva reserva, ConsumoServico consumo) {
        return reservaService.removerServico(reserva.getId(), consumo.getId());
    }

    private void validarSelecao(Hospede hospede, Quarto quarto) {
        if (hospede == null) {
            throw new RegraNegocioException("Selecione o hóspede responsável pela reserva.");
        }
        if (quarto == null) {
            throw new RegraNegocioException("Selecione o quarto.");
        }
    }
}
