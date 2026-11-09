package br.com.hotelaria.controller;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Cargo;
import br.com.hotelaria.model.Ferias;
import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.service.FuncionarioService;
import br.com.hotelaria.service.HotelService;
import br.com.hotelaria.util.Validador;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class FuncionarioController {
    private final FuncionarioService funcionarioService;
    private final HotelService hotelService;

    public FuncionarioController(FuncionarioService funcionarioService, HotelService hotelService) {
        this.funcionarioService = funcionarioService;
        this.hotelService = hotelService;
    }

    public List<Funcionario> listar(Hotel hotel, String filtro) {
        List<Funcionario> funcionarios = hotel == null
                ? funcionarioService.listar()
                : funcionarioService.listarPorHotel(hotel.getId());
        if (Validador.isVazio(filtro)) {
            return funcionarios;
        }
        String texto = filtro.toLowerCase();
        String numeros = Validador.somenteNumeros(filtro);
        List<Funcionario> encontrados = new ArrayList<>();
        for (Funcionario funcionario : funcionarios) {
            if (funcionario.getNome().toLowerCase().contains(texto)
                    || funcionario.getCargo().getDescricao().toLowerCase().contains(texto)
                    || (!numeros.isEmpty() && funcionario.getCpf().contains(numeros))) {
                encontrados.add(funcionario);
            }
        }
        return encontrados;
    }

    public List<Hotel> listarHoteis() {
        return hotelService.listar();
    }

    public Funcionario novoFuncionario(Cargo cargo) {
        if (cargo == null) {
            throw new RegraNegocioException("Selecione o cargo.");
        }
        return cargo.novoFuncionario();
    }

    public void salvar(Funcionario funcionario) {
        funcionarioService.salvar(funcionario);
    }

    public void excluir(Funcionario funcionario) {
        funcionarioService.excluir(funcionario.getId());
    }

    public Set<Long> idsEmFerias() {
        return funcionarioService.buscarIdsEmFerias();
    }

    public BigDecimal calcularFolha(List<Funcionario> funcionarios) {
        return funcionarioService.calcularFolha(funcionarios);
    }

    public List<Ferias> listarFerias(Funcionario funcionario) {
        return funcionarioService.listarFerias(funcionario.getId());
    }

    public long diasDeFeriasNoAno(Funcionario funcionario, int ano) {
        return funcionarioService.diasDeFeriasNoAno(funcionario.getId(), ano);
    }

    public void agendarFerias(Funcionario funcionario, LocalDate inicio, LocalDate fim) {
        funcionarioService.agendarFerias(funcionario.getId(), inicio, fim);
    }

    public void cancelarFerias(Funcionario funcionario, Ferias ferias) {
        funcionarioService.cancelarFerias(funcionario.getId(), ferias.getId());
    }
}
