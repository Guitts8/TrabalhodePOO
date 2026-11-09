package br.com.hotelaria.controller;

import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.service.ResumoService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class InicioController {
    private final ResumoService resumoService;

    public InicioController(ResumoService resumoService) {
        this.resumoService = resumoService;
    }

    public ResumoService.Resumo resumo() {
        return resumoService.gerarResumo();
    }

    public List<ResumoService.OcupacaoHotel> ocupacaoPorHotel() {
        return resumoService.ocupacaoPorHotel();
    }

    public List<Reserva> chegadasDeHoje() {
        return resumoService.checkInsPrevistos(LocalDate.now());
    }

    public List<Reserva> saidasDeHoje() {
        return resumoService.checkOutsPrevistos(LocalDate.now());
    }
}
