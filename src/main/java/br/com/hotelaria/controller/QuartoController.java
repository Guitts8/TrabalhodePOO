package br.com.hotelaria.controller;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.StatusQuarto;
import br.com.hotelaria.model.TipoQuarto;
import br.com.hotelaria.service.HotelService;
import br.com.hotelaria.service.QuartoService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuartoController {
    private final QuartoService quartoService;
    private final HotelService hotelService;

    public QuartoController(QuartoService quartoService, HotelService hotelService) {
        this.quartoService = quartoService;
        this.hotelService = hotelService;
    }

    public List<Quarto> listar(Hotel hotel, StatusQuarto situacao, String filtro) {
        List<Quarto> quartos = hotel == null ? quartoService.listar() : quartoService.listarPorHotel(hotel.getId());
        String texto = filtro == null ? "" : filtro.trim().toLowerCase();
        List<Quarto> resultado = new ArrayList<>();
        for (Quarto quarto : quartos) {
            if (situacao != null && quarto.getStatus() != situacao) {
                continue;
            }
            if (!texto.isEmpty()
                    && !quarto.getNumero().toLowerCase().contains(texto)
                    && !quarto.getTipo().getDescricao().toLowerCase().contains(texto)
                    && !quarto.getCaracteristicas().toLowerCase().contains(texto)) {
                continue;
            }
            resultado.add(quarto);
        }
        return resultado;
    }

    public List<Hotel> listarHoteis() {
        return hotelService.listar();
    }

    public Quarto novoQuarto(TipoQuarto tipo) {
        if (tipo == null) {
            throw new RegraNegocioException("Selecione o tipo do quarto.");
        }
        return tipo.novoQuarto();
    }

    public void salvar(Quarto quarto) {
        quartoService.salvar(quarto);
    }

    public void excluir(Quarto quarto) {
        quartoService.excluir(quarto.getId());
    }

    public List<Quarto> buscarDisponiveis(Hotel hotel, LocalDate entrada, LocalDate saida, int hospedes) {
        if (hotel == null) {
            throw new RegraNegocioException("Selecione o hotel.");
        }
        return quartoService.buscarDisponiveis(hotel.getId(), entrada, saida, hospedes, null);
    }

    public void liberar(Quarto quarto) {
        quartoService.liberar(quarto.getId());
    }

    public void colocarEmManutencao(Quarto quarto) {
        quartoService.colocarEmManutencao(quarto.getId());
    }
}
