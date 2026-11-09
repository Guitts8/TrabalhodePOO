package br.com.hotelaria.api;

import br.com.hotelaria.api.dto.HotelRequest;
import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.service.HotelService;
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
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/hoteis")
@Tag(name = "Hotéis", description = "Unidades da rede")
public class HotelRestController {
    private final HotelService hotelService;

    public HotelRestController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    @Operation(summary = "Lista os hotéis da rede")
    public List<Hotel> listar() {
        return hotelService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um hotel pelo código")
    public Hotel buscar(@PathVariable("id") Long id) {
        return hotelService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um hotel")
    public ResponseEntity<Hotel> cadastrar(@RequestBody HotelRequest dados) {
        Hotel hotel = new Hotel();
        dados.copiarPara(hotel);
        Hotel salvo = hotelService.salvar(hotel);
        return ResponseEntity.created(URI.create("/api/hoteis/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Altera os dados de um hotel")
    public Hotel atualizar(@PathVariable("id") Long id, @RequestBody HotelRequest dados) {
        Hotel hotel = hotelService.buscarPorId(id);
        dados.copiarPara(hotel);
        return hotelService.salvar(hotel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um hotel (só se não tiver quartos nem funcionários)")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        hotelService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/quartos")
    @Operation(summary = "Lista os quartos do hotel")
    public List<Quarto> quartos(@PathVariable("id") Long id) {
        return hotelService.listarQuartos(id);
    }

    @GetMapping("/{id}/funcionarios")
    @Operation(summary = "Lista os funcionários do hotel")
    public List<Funcionario> funcionarios(@PathVariable("id") Long id) {
        return hotelService.listarFuncionarios(id);
    }
}
