package br.com.hotelaria.api;

import br.com.hotelaria.api.dto.FeriasRequest;
import br.com.hotelaria.api.dto.FuncionarioRequest;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Administrador;
import br.com.hotelaria.model.Cargo;
import br.com.hotelaria.model.Escala;
import br.com.hotelaria.model.Ferias;
import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.model.Jornada;
import br.com.hotelaria.model.Recepcionista;
import br.com.hotelaria.service.FuncionarioService;
import br.com.hotelaria.service.HotelService;
import br.com.hotelaria.util.ConversorEnum;
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

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
@Tag(name = "Funcionários", description = "Funcionários das unidades, jornada e férias")
public class FuncionarioRestController {
    public record Folha(Long hotelId, int quantidadeFuncionarios, BigDecimal total) {
    }

    private final FuncionarioService funcionarioService;
    private final HotelService hotelService;

    public FuncionarioRestController(FuncionarioService funcionarioService, HotelService hotelService) {
        this.funcionarioService = funcionarioService;
        this.hotelService = hotelService;
    }

    @GetMapping
    @Operation(summary = "Lista os funcionários (pode filtrar por hotel)")
    public List<Funcionario> listar(@RequestParam(name = "hotelId", required = false) Long hotelId) {
        if (hotelId != null) {
            return funcionarioService.listarPorHotel(hotelId);
        }
        return funcionarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um funcionário pelo código")
    public Funcionario buscar(@PathVariable("id") Long id) {
        return funcionarioService.buscarPorId(id);
    }

    @GetMapping("/folha")
    @Operation(summary = "Total da folha de pagamento (de um hotel ou da rede toda)")
    public Folha folha(@RequestParam(name = "hotelId", required = false) Long hotelId) {
        List<Funcionario> funcionarios = listar(hotelId);
        return new Folha(hotelId, funcionarios.size(), funcionarioService.calcularFolha(funcionarios));
    }

    @PostMapping
    @Operation(summary = "Cadastra um funcionário (cargo: GERENTE, RECEPCIONISTA ou ADMINISTRADOR)")
    public ResponseEntity<Funcionario> cadastrar(@RequestBody FuncionarioRequest dados) {
        Cargo cargo = ConversorEnum.converter(Cargo.class, dados.cargo(), "cargo");
        Funcionario funcionario = cargo.novoFuncionario();
        preencher(funcionario, dados);
        Funcionario salvo = funcionarioService.salvar(funcionario);
        return ResponseEntity.created(URI.create("/api/funcionarios/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera um funcionário (o cargo não pode ser trocado)")
    public Funcionario atualizar(@PathVariable("id") Long id, @RequestBody FuncionarioRequest dados) {
        Funcionario funcionario = funcionarioService.buscarPorId(id);
        if (dados.cargo() != null && !dados.cargo().equalsIgnoreCase(funcionario.getCargo().name())) {
            throw new RegraNegocioException("Não é possível trocar o cargo de um funcionário já cadastrado.");
        }
        preencher(funcionario, dados);
        return funcionarioService.salvar(funcionario);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um funcionário")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        funcionarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/ferias")
    @Operation(summary = "Lista as férias do funcionário")
    public List<Ferias> ferias(@PathVariable("id") Long id) {
        return funcionarioService.listarFerias(id);
    }

    @PostMapping("/{id}/ferias")
    @Operation(summary = "Agenda um período de férias")
    public ResponseEntity<Ferias> agendarFerias(@PathVariable("id") Long id, @RequestBody FeriasRequest dados) {
        Ferias ferias = funcionarioService.agendarFerias(id, dados.dataInicio(), dados.dataFim());
        return ResponseEntity.created(URI.create("/api/funcionarios/" + id + "/ferias/" + ferias.getId())).body(ferias);
    }

    @DeleteMapping("/{id}/ferias/{feriasId}")
    @Operation(summary = "Cancela um período de férias")
    public ResponseEntity<Void> cancelarFerias(@PathVariable("id") Long id, @PathVariable("feriasId") Long feriasId) {
        funcionarioService.cancelarFerias(id, feriasId);
        return ResponseEntity.noContent().build();
    }

    private void preencher(Funcionario funcionario, FuncionarioRequest dados) {
        funcionario.setNome(dados.nome());
        funcionario.setCpf(dados.cpf());
        funcionario.setDataNascimento(dados.dataNascimento());
        funcionario.setTelefone(dados.telefone());
        funcionario.setEmail(dados.email());
        funcionario.setHotel(hotelService.buscarPorId(dados.hotelId()));
        funcionario.setSalarioBase(dados.salarioBase());
        funcionario.setDataContratacao(dados.dataContratacao());
        Escala escala = ConversorEnum.converter(Escala.class, dados.escala(), "escala");
        funcionario.setJornada(new Jornada(dados.horaEntrada(), dados.horaSaida(), escala));

        if (funcionario instanceof Recepcionista recepcionista) {
            recepcionista.setIdiomas(dados.idiomas());
        } else if (funcionario instanceof Administrador administrador) {
            administrador.setSetor(dados.setor());
        }
    }
}
