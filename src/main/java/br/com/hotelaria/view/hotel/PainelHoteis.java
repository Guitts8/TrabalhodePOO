package br.com.hotelaria.view.hotel;

import br.com.hotelaria.controller.HotelController;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.view.PainelCadastro;
import br.com.hotelaria.view.componentes.Componentes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PainelHoteis extends PainelCadastro<Hotel> {
    private final HotelController controller;

    private final Map<Long, Long> quartosPorHotel = new HashMap<>();
    private final Map<Long, Long> funcionariosPorHotel = new HashMap<>();

    public PainelHoteis(HotelController controller) {
        super("Hotéis", "Unidades da rede, com seus quartos e funcionários", "Pesquisar por nome ou cidade");
        this.controller = controller;

        modelo.coluna("Nome", Hotel::getNome)
                .coluna("Cidade/UF", h -> h.getEndereco().getCidadeUf())
                .coluna("Telefone", Hotel::getTelefone)
                .coluna("E-mail", Hotel::getEmail)
                .coluna("Categoria", h -> h.getEstrelas() + (h.getEstrelas() == 1 ? " estrela" : " estrelas"))
                .coluna("Quartos", h -> quartosPorHotel.getOrDefault(h.getId(), 0L))
                .coluna("Funcionários", h -> funcionariosPorHotel.getOrDefault(h.getId(), 0L));
        configurarColunas(220, 160, 120, 250, 90, 70, 95);
        Componentes.centralizar(tabela, 4, 5, 6);

        adicionarAcao("Ver quartos e funcionários", () -> comSelecionado(this::abrirDetalhes));
    }

    @Override
    protected List<Hotel> buscarDados(String filtro) {
        List<Hotel> hoteis = controller.pesquisar(filtro);
        quartosPorHotel.clear();
        funcionariosPorHotel.clear();
        for (Hotel hotel : hoteis) {
            quartosPorHotel.put(hotel.getId(), controller.contarQuartos(hotel));
            funcionariosPorHotel.put(hotel.getId(), controller.contarFuncionarios(hotel));
        }
        return hoteis;
    }

    @Override
    protected void novo() {
        new HotelDialog(this, null, controller).abrir();
        atualizar();
    }

    @Override
    protected void editar(Hotel hotel) {
        new HotelDialog(this, hotel, controller).abrir();
        atualizar();
    }

    @Override
    protected void excluir(Hotel hotel) {
        controller.excluir(hotel);
    }

    @Override
    protected String descrever(Hotel hotel) {
        return hotel.getNome();
    }

    private void abrirDetalhes(Hotel hotel) {
        new HotelDetalhesDialog(this, hotel, controller).setVisible(true);
    }
}
