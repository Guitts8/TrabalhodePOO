package br.com.hotelaria.view.funcionario;

import br.com.hotelaria.controller.FuncionarioController;
import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.PainelCadastro;
import br.com.hotelaria.view.componentes.Componentes;

import javax.swing.JComboBox;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PainelFuncionarios extends PainelCadastro<Funcionario> {
    private final FuncionarioController controller;
    private final JComboBox<Object> filtroHotel = new JComboBox<>();
    private boolean carregandoFiltros;
    private Set<Long> emFerias = new HashSet<>();
    private BigDecimal folha = BigDecimal.ZERO;

    public PainelFuncionarios(FuncionarioController controller) {
        super("Funcionários", "Funcionários de cada unidade, jornada de trabalho, salários e férias", "Nome, cargo ou CPF");
        this.controller = controller;

        modelo.coluna("Nome", Funcionario::getNome)
                .coluna("Cargo", Funcionario::getCargo)
                .coluna("Hotel", f -> f.getHotel().getNome())
                .coluna("Horário", f -> f.getJornada() == null ? "" : f.getJornada().toString())
                .coluna("Escala", f -> f.getJornada() == null ? "" : f.getJornada().getEscala())
                .coluna("Salário base", f -> Formatos.moeda(f.getSalarioBase()))
                .coluna("Salário final", f -> Formatos.moeda(f.calcularSalario()))
                .coluna("Contratação", f -> Formatos.data(f.getDataContratacao()))
                .coluna("Situação", f -> emFerias.contains(f.getId()) ? "Em férias" : "Trabalhando");
        configurarColunas(185, 110, 165, 110, 60, 100, 100, 95, 110);
        Componentes.centralizar(tabela, 4, 7);
        Componentes.alinharDireita(tabela, 5, 6);
        Componentes.coresDeSituacao(tabela, 8);

        filtroHotel.addActionListener(e -> {
            if (!carregandoFiltros) {
                super.atualizar();
            }
        });
        adicionarFiltro("Hotel:", filtroHotel);

        adicionarAcao("Férias", () -> comSelecionado(funcionario -> {
            new FeriasDialog(this, funcionario, controller).setVisible(true);
            atualizar();
        }));
    }

    @Override
    public void atualizar() {
        executar(this::carregarHoteis);
        super.atualizar();
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
    protected List<Funcionario> buscarDados(String filtro) {
        Hotel hotel = filtroHotel.getSelectedItem() instanceof Hotel h ? h : null;
        List<Funcionario> funcionarios = controller.listar(hotel, filtro);
        emFerias = controller.idsEmFerias();
        folha = controller.calcularFolha(funcionarios);
        return funcionarios;
    }

    @Override
    protected String textoTotal(List<Funcionario> dados) {
        return dados.size() + " funcionário(s)   |   Folha de pagamento mensal (já com os adicionais de cada cargo): "
                + Formatos.moeda(folha);
    }

    @Override
    protected void novo() {
        new FuncionarioDialog(this, null, controller).abrir();
        atualizar();
    }

    @Override
    protected void editar(Funcionario funcionario) {
        new FuncionarioDialog(this, funcionario, controller).abrir();
        atualizar();
    }

    @Override
    protected void excluir(Funcionario funcionario) {
        controller.excluir(funcionario);
    }

    @Override
    protected String descrever(Funcionario funcionario) {
        return funcionario.getNome() + " (" + funcionario.getCargo() + ")";
    }
}
