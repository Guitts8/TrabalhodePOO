package br.com.hotelaria.api;

import br.com.hotelaria.service.ResumoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resumo")
@Tag(name = "Resumo", description = "Números gerais da rede")
public class ResumoRestController {
    private final ResumoService resumoService;

    public ResumoRestController(ResumoService resumoService) {
        this.resumoService = resumoService;
    }

    @GetMapping
    @Operation(summary = "Totais de hotéis, quartos, hóspedes e movimento do dia")
    public ResumoService.Resumo resumo() {
        return resumoService.gerarResumo();
    }

    @GetMapping("/ocupacao")
    @Operation(summary = "Ocupação atual de cada hotel")
    public List<ResumoService.OcupacaoHotel> ocupacao() {
        return resumoService.ocupacaoPorHotel();
    }
}
