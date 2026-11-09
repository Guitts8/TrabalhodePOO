package br.com.hotelaria.api;

import br.com.hotelaria.api.dto.QuartoRequest;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.QuartoLuxo;
import br.com.hotelaria.model.QuartoStandard;
import br.com.hotelaria.model.Suite;
import br.com.hotelaria.model.TipoQuarto;
import br.com.hotelaria.service.HotelService;
import br.com.hotelaria.service.QuartoService;
import br.com.hotelaria.util.ConversorEnum;
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

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/quartos")
@Tag(name = "Quartos", description = "Quartos dos hotéis, disponibilidade e situação")
public class QuartoRestController {
    private final QuartoService quartoService;
    private final HotelService hotelService;

    public QuartoRestController(QuartoService quartoService, HotelService hotelService) {
        this.quartoService = quartoService;
        this.hotelService = hotelService;
    }

    @GetMapping
    @Operation(summary = "Lista os quartos (pode filtrar por hotel)")
    public List<Quarto> listar(@RequestParam(name = "hotelId", required = false) Long hotelId) {
        if (hotelId != null) {
            return quartoService.listarPorHotel(hotelId);
        }
        return quartoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um quarto pelo código")
    public Quarto buscar(@PathVariable("id") Long id) {
        return quartoService.buscarPorId(id);
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Quartos livres de um hotel no período (datas no formato aaaa-mm-dd)")
    public List<Quarto> disponiveis(@RequestParam("hotelId") Long hotelId,
                                    @RequestParam("entrada") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entrada,
                                    @RequestParam("saida") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate saida,
                                    @RequestParam(name = "hospedes", defaultValue = "1") int hospedes) {
        return quartoService.buscarDisponiveis(hotelId, entrada, saida, hospedes, null);
    }

    @PostMapping
    @Operation(summary = "Cadastra um quarto (tipo: STANDARD, LUXO ou SUITE)")
    public ResponseEntity<Quarto> cadastrar(@RequestBody QuartoRequest dados) {
        TipoQuarto tipo = ConversorEnum.converter(TipoQuarto.class, dados.tipo(), "tipo");
        Quarto quarto = tipo.novoQuarto();
        preencher(quarto, dados);
        Quarto salvo = quartoService.salvar(quarto);
        return ResponseEntity.created(URI.create("/api/quartos/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera um quarto (o tipo não pode ser trocado)")
    public Quarto atualizar(@PathVariable("id") Long id, @RequestBody QuartoRequest dados) {
        Quarto quarto = quartoService.buscarPorId(id);
        if (dados.tipo() != null && !dados.tipo().equalsIgnoreCase(quarto.getTipo().name())) {
            throw new RegraNegocioException("Não é possível trocar o tipo de um quarto já cadastrado.");
        }
        preencher(quarto, dados);
        return quartoService.salvar(quarto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um quarto (só se nunca teve reservas)")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        quartoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/liberar")
    @Operation(summary = "Libera o quarto depois da limpeza ou manutenção")
    public Quarto liberar(@PathVariable("id") Long id) {
        return quartoService.liberar(id);
    }

    @PostMapping("/{id}/manutencao")
    @Operation(summary = "Coloca o quarto em manutenção")
    public Quarto manutencao(@PathVariable("id") Long id) {
        return quartoService.colocarEmManutencao(id);
    }

    private void preencher(Quarto quarto, QuartoRequest dados) {
        quarto.setHotel(hotelService.buscarPorId(dados.hotelId()));
        quarto.setNumero(dados.numero());
        quarto.setAndar(dados.andar() == null ? 0 : dados.andar());
        quarto.setCapacidade(dados.capacidade() == null ? 0 : dados.capacidade());
        quarto.setValorDiaria(dados.valorDiaria());
        quarto.setDescricao(dados.descricao());

        if (quarto instanceof QuartoStandard standard) {
            standard.setCamaCasal(Boolean.TRUE.equals(dados.camaCasal()));
        } else if (quarto instanceof QuartoLuxo luxo) {
            luxo.setVistaMar(Boolean.TRUE.equals(dados.vistaMar()));
            luxo.setHidromassagem(Boolean.TRUE.equals(dados.hidromassagem()));
        } else if (quarto instanceof Suite suite) {
            suite.setValorHospedeAdicional(dados.valorHospedeAdicional() == null
                    ? BigDecimal.ZERO : dados.valorHospedeAdicional());
        }
    }
}
