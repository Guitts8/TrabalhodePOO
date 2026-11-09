package br.com.hotelaria.api;

import br.com.hotelaria.api.dto.ServicoRequest;
import br.com.hotelaria.model.Servico;
import br.com.hotelaria.service.ServicoService;
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
@RequestMapping("/api/servicos")
@Tag(name = "Serviços", description = "Serviços oferecidos aos hóspedes")
public class ServicoRestController {
    private final ServicoService servicoService;

    public ServicoRestController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    @Operation(summary = "Lista os serviços (ativos=true mostra só os ativos)")
    public List<Servico> listar(@RequestParam(name = "ativos", defaultValue = "false") boolean ativos) {
        return ativos ? servicoService.listarAtivos() : servicoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um serviço pelo código")
    public Servico buscar(@PathVariable("id") Long id) {
        return servicoService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um serviço (categoria: CAFE_DA_MANHA, SERVICO_DE_QUARTO, LAVANDERIA, ESTACIONAMENTO, LAZER, OUTROS)")
    public ResponseEntity<Servico> cadastrar(@RequestBody ServicoRequest dados) {
        Servico servico = new Servico();
        dados.copiarPara(servico);
        Servico salvo = servicoService.salvar(servico);
        return ResponseEntity.created(URI.create("/api/servicos/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera um serviço")
    public Servico atualizar(@PathVariable("id") Long id, @RequestBody ServicoRequest dados) {
        Servico servico = servicoService.buscarPorId(id);
        dados.copiarPara(servico);
        return servicoService.salvar(servico);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um serviço (só se nunca foi usado)")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        servicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
