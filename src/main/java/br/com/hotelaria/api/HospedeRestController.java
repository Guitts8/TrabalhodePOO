package br.com.hotelaria.api;

import br.com.hotelaria.api.dto.HospedeRequest;
import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.service.HospedeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.List;

@RestController
@RequestMapping("/api/hospedes")
@Tag(name = "Hóspedes", description = "Cadastro, pesquisa e histórico de hospedagens")
public class HospedeRestController {
    private final HospedeService hospedeService;

    public HospedeRestController(HospedeService hospedeService) {
        this.hospedeService = hospedeService;
    }

    @GetMapping
    @Operation(summary = "Lista os hóspedes ou pesquisa por nome, CPF ou e-mail (parâmetro busca)")
    public List<Hospede> listar(@RequestParam(name = "busca", required = false) String busca) {
        return hospedeService.pesquisar(busca);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um hóspede pelo código")
    public Hospede buscar(@PathVariable("id") Long id) {
        return hospedeService.buscarPorId(id);
    }

    @GetMapping("/{id}/historico")
    @Operation(summary = "Histórico de reservas do hóspede, com quartos e serviços consumidos")
    public List<Reserva> historico(@PathVariable("id") Long id) {
        return hospedeService.historico(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um hóspede")
    public ResponseEntity<Hospede> cadastrar(@RequestBody HospedeRequest dados) {
        Hospede hospede = new Hospede();
        dados.copiarPara(hospede);
        Hospede salvo = hospedeService.salvar(hospede);
        return ResponseEntity.created(URI.create("/api/hospedes/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera os dados de um hóspede")
    public Hospede atualizar(@PathVariable("id") Long id, @RequestBody HospedeRequest dados) {
        Hospede hospede = hospedeService.buscarPorId(id);
        dados.copiarPara(hospede);
        return hospedeService.salvar(hospede);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um hóspede (só se não tiver reservas)")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        hospedeService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
