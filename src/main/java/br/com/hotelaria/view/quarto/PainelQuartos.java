package br.com.hotelaria.view.quarto;

import br.com.hotelaria.controller.QuartoController;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.StatusQuarto;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.Mensagens;
import br.com.hotelaria.view.PainelCadastro;
import br.com.hotelaria.view.componentes.Componentes;

import javax.swing.JComboBox;
import java.util.List;

public class PainelQuartos extends PainelCadastro<Quarto> {
    private final QuartoController controller;
    private final JComboBox<Object> filtroHotel = new JComboBox<>();
    private final JComboBox<Object> filtroSituacao = new JComboBox<>();
    private boolean carregandoFiltros;

    public PainelQuartos(QuartoController controller) {
        super("Quartos", "Quartos de cada hotel, tipos, valores e situação atual", "Número, tipo ou característica");
        this.controller = controller;

        modelo.coluna("Hotel", q -> q.getHotel().getNome())
                .coluna("Nº", Quarto::getNumero)
                .coluna("Andar", Quarto::getAndar)
                .coluna("Tipo", Quarto::getTipo)
                .coluna("Capacidade", q -> q.getCapacidade() + " pessoa(s)")
                .coluna("Diária base", q -> Formatos.moeda(q.getValorDiaria()))
                .coluna("Diária final", q -> Formatos.moeda(q.calcularDiaria()))
                .coluna("Características", Quarto::getCaracteristicas)
                .coluna("Situação", Quarto::getStatus);
        configurarColunas(165, 45, 50, 75, 90, 95, 95, 330, 120);
        Componentes.centralizar(tabela, 1, 2);
        Componentes.alinharDireita(tabela, 5, 6);
        Componentes.coresDeSituacao(tabela, 8);

        filtroSituacao.addItem("Todas");
        for (StatusQuarto situacao : StatusQuarto.values()) {
            filtroSituacao.addItem(situacao);
        }
        filtroHotel.addActionListener(e -> recarregarTabela());
        filtroSituacao.addActionListener(e -> recarregarTabela());
        adicionarFiltro("Hotel:", filtroHotel);
        adicionarFiltro("Situação:", filtroSituacao);

        adicionarAcao("Consultar disponibilidade", () -> new DisponibilidadeDialog(this, controller).setVisible(true));
        adicionarAcao("Liberar quarto", () -> comSelecionado(this::liberar));
        adicionarAcao("Colocar em manutenção", () -> comSelecionado(this::colocarEmManutencao));
    }

    @Override
    public void atualizar() {
        executar(this::carregarHoteis);
        super.atualizar();
    }

    private void recarregarTabela() {
        if (!carregandoFiltros) {
            super.atualizar();
        }
    }

    private void carregarHoteis() {
        Object escolhido = filtroHotel.getSelectedItem();
        carregandoFiltros = true;
        try {
            filtroHotel.removeAllItems();
            filtroHotel.addItem("Todos os hotéis");
            for (Hotel hotel : controller.listarHoteis()) {
                filtroHotel.addItem(hotel);
            }
            if (escolhido != null) {
                filtroHotel.setSelectedItem(escolhido);
            }
        } finally {
            carregandoFiltros = false;
        }
    }

    @Override
    protected List<Quarto> buscarDados(String filtro) {
        Hotel hotel = filtroHotel.getSelectedItem() instanceof Hotel h ? h : null;
        StatusQuarto situacao = filtroSituacao.getSelectedItem() instanceof StatusQuarto s ? s : null;
        return controller.listar(hotel, situacao, filtro);
    }

    @Override
    protected void novo() {
        new QuartoDialog(this, null, controller).abrir();
        atualizar();
    }

    @Override
    protected void editar(Quarto quarto) {
        new QuartoDialog(this, quarto, controller).abrir();
        atualizar();
    }

    @Override
    protected void excluir(Quarto quarto) {
        controller.excluir(quarto);
    }

    @Override
    protected String descrever(Quarto quarto) {
        return "o quarto " + quarto.getNumero() + " do " + quarto.getHotel().getNome();
    }

    private void liberar(Quarto quarto) {
        controller.liberar(quarto);
        super.atualizar();
        Mensagens.info(this, "O quarto " + quarto.getNumero() + " está disponível novamente.");
    }

    private void colocarEmManutencao(Quarto quarto) {
        if (Mensagens.confirmar(this, "Colocar o quarto " + quarto.getNumero() + " em manutenção? "
                + "Ele não poderá receber novas reservas até ser liberado.")) {
            controller.colocarEmManutencao(quarto);
            super.atualizar();
        }
    }
}
