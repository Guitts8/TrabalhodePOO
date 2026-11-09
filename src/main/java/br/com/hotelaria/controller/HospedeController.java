package br.com.hotelaria.controller;

import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.service.HospedeService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HospedeController {
    private final HospedeService hospedeService;

    public HospedeController(HospedeService hospedeService) {
        this.hospedeService = hospedeService;
    }

    public List<Hospede> pesquisar(String texto) {
        return hospedeService.pesquisar(texto);
    }

    public void salvar(Hospede hospede) {
        hospedeService.salvar(hospede);
    }

    public void excluir(Hospede hospede) {
        hospedeService.excluir(hospede.getId());
    }

    public List<Reserva> historico(Hospede hospede) {
        return hospedeService.historico(hospede.getId());
    }
}
