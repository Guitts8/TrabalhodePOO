package br.com.hotelaria.controller;

import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.service.HotelService;
import br.com.hotelaria.util.Validador;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    public List<Hotel> listar() {
        return hotelService.listar();
    }

    public List<Hotel> pesquisar(String filtro) {
        List<Hotel> hoteis = hotelService.listar();
        if (Validador.isVazio(filtro)) {
            return hoteis;
        }
        String texto = filtro.toLowerCase();
        List<Hotel> encontrados = new ArrayList<>();
        for (Hotel hotel : hoteis) {
            if (hotel.getNome().toLowerCase().contains(texto)
                    || hotel.getEndereco().getCidadeUf().toLowerCase().contains(texto)) {
                encontrados.add(hotel);
            }
        }
        return encontrados;
    }

    public void salvar(Hotel hotel) {
        hotelService.salvar(hotel);
    }

    public void excluir(Hotel hotel) {
        hotelService.excluir(hotel.getId());
    }

    public List<Quarto> quartosDoHotel(Hotel hotel) {
        return hotelService.listarQuartos(hotel.getId());
    }

    public List<Funcionario> funcionariosDoHotel(Hotel hotel) {
        return hotelService.listarFuncionarios(hotel.getId());
    }

    public long contarQuartos(Hotel hotel) {
        return hotelService.contarQuartos(hotel.getId());
    }

    public long contarFuncionarios(Hotel hotel) {
        return hotelService.contarFuncionarios(hotel.getId());
    }
}
