package br.com.hotelaria.api;

import br.com.hotelaria.api.dto.ConsumoRequest;
import br.com.hotelaria.api.dto.ReservaRequest;
import br.com.hotelaria.api.dto.SimulacaoResponse;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.service.ReservaService;
import br.com.hotelaria.util.ConversorEnum;
import br.com.hotelaria.util.Validador;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Reservas, check-in, check-out e serviços consumidos")
public class ReservaRestController {
    private final ReservaService reservaService;

    public ReservaRestController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    @Operation(summary = "Lista as reservas (status: PENDENTE, CONFIRMADA, EM_ANDAMENTO, FINALIZADA, CANCELADA)")
    public List<Reserva> listar(@RequestParam(name = "status", required = false) String status) {
        if (Validador.isVazio(status)) {
            return reservaService.listar();
        }
        return reservaService.listarPorStatus(ConversorEnum.converter(StatusReserva.class, status, "status"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma reserva pelo número")
    public Reserva buscar(@PathVariable("id") Long id) {
        return reservaService.buscarPorId(id);
    }

    @GetMapping("/simulacao")
    @Operation(summary = "Calcula o valor de uma reserva sem gravar")
    public SimulacaoResponse simular(@RequestParam("hospedeId") Long hospedeId,
                                     @RequestParam("quartoId") Long quartoId,
                                     @RequestParam("entrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entrada,
                                     @RequestParam("saida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate saida,
                                     @RequestParam(name = "hospedes", defaultValue = "1") int hospedes) {
        Reserva simulada = reservaService.simular(hospedeId, quartoId, entrada, saida, hospedes, null);
        return SimulacaoResponse.de(simulada);
    }

    @PostMapping
    @Operation(summary = "Cria uma reserva (fica pendente)")
    public ResponseEntity<Reserva> criar(@RequestBody ReservaRequest dados) {
        Reserva reserva = reservaService.criar(dados.hospedeId(), dados.quartoId(), dados.dataEntrada(),
                dados.dataSaida(), dados.hospedes(), dados.observacoes());
        return ResponseEntity.created(URI.create("/api/reservas/" + reserva.getId())).body(reserva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera uma reserva pendente ou confirmada")
    public Reserva alterar(@PathVariable("id") Long id, @RequestBody ReservaRequest dados) {
        return reservaService.alterar(id, dados.hospedeId(), dados.quartoId(), dados.dataEntrada(),
                dados.dataSaida(), dados.hospedes(), dados.observacoes());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma reserva pendente ou cancelada")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        reservaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirmar")
    @Operation(summary = "Confirma uma reserva pendente")
    public Reserva confirmar(@PathVariable("id") Long id) {
        return reservaService.confirmar(id);
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancela uma reserva pendente ou confirmada")
    public Reserva cancelar(@PathVariable("id") Long id) {
        return reservaService.cancelar(id);
    }

    @PostMapping("/{id}/check-in")
    @Operation(summary = "Faz o check-in (reserva vai para EM_ANDAMENTO e o quarto para OCUPADO)")
    public Reserva checkIn(@PathVariable("id") Long id) {
        return reservaService.fazerCheckIn(id);
    }

    @PostMapping("/{id}/check-out")
    @Operation(summary = "Faz o check-out (reserva FINALIZADA e quarto em LIMPEZA)")
    public Reserva checkOut(@PathVariable("id") Long id) {
        return reservaService.fazerCheckOut(id);
    }

    @PostMapping("/{id}/servicos")
    @Operation(summary = "Lança um serviço consumido na reserva")
    public Reserva adicionarServico(@PathVariable("id") Long id, @RequestBody ConsumoRequest dados) {
        int quantidade = dados.quantidade() == null ? 1 : dados.quantidade();
        return reservaService.adicionarServico(id, dados.servicoId(), quantidade);
    }

    @DeleteMapping("/{id}/servicos/{consumoId}")
    @Operation(summary = "Remove um serviço lançado na reserva")
    public Reserva removerServico(@PathVariable("id") Long id, @PathVariable("consumoId") Long consumoId) {
        return reservaService.removerServico(id, consumoId);
    }
}
