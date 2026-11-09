package br.com.hotelaria.view.hotel;

import br.com.hotelaria.controller.HotelController;
import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.Tema;
import br.com.hotelaria.view.componentes.Componentes;
import br.com.hotelaria.view.componentes.ModeloTabela;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class HotelDetalhesDialog extends JDialog {
    public HotelDetalhesDialog(Component pai, Hotel hotel, HotelController controller) {
        super(FormularioDialog.janelaDe(pai), hotel.getNome(), ModalityType.APPLICATION_MODAL);

        List<Quarto> quartos = controller.quartosDoHotel(hotel);
        List<Funcionario> funcionarios = controller.funcionariosDoHotel(hotel);

        JPanel conteudo = new JPanel(new BorderLayout(0, 14));
        conteudo.setBorder(new EmptyBorder(20, 22, 16, 22));

        JPanel cabecalho = new JPanel(new GridLayout(0, 1, 0, 3));
        JLabel nome = new JLabel(hotel.getNome() + "  (" + hotel.getEstrelas() + " estrelas)");
        nome.setFont(Tema.fonte(Font.BOLD, 18f));
        cabecalho.add(nome);
        cabecalho.add(Componentes.subtitulo(hotel.getEndereco().toString()));
        String contato = "Telefone: " + (hotel.getTelefone() == null ? "-" : hotel.getTelefone())
                + "     E-mail: " + (hotel.getEmail() == null ? "-" : hotel.getEmail());
        cabecalho.add(Componentes.subtitulo(contato));
        conteudo.add(cabecalho, BorderLayout.NORTH);

        ModeloTabela<Quarto> modeloQuartos = new ModeloTabela<Quarto>()
                .coluna("Número", Quarto::getNumero)
                .coluna("Andar", Quarto::getAndar)
                .coluna("Tipo", Quarto::getTipo)
                .coluna("Capacidade", q -> q.getCapacidade() + " pessoa(s)")
                .coluna("Diária", q -> Formatos.moeda(q.calcularDiaria()))
                .coluna("Características", Quarto::getCaracteristicas)
                .coluna("Situação", Quarto::getStatus);
        modeloQuartos.setLinhas(quartos);
        JTable tabelaQuartos = Componentes.tabela(modeloQuartos);
        Componentes.larguras(tabelaQuartos, 65, 50, 80, 90, 95, 330, 120);
        Componentes.centralizar(tabelaQuartos, 0, 1);
        Componentes.alinharDireita(tabelaQuartos, 4);
        Componentes.coresDeSituacao(tabelaQuartos, 6);

        ModeloTabela<Funcionario> modeloFuncionarios = new ModeloTabela<Funcionario>()
                .coluna("Nome", Funcionario::getNome)
                .coluna("Cargo", Funcionario::getCargo)
                .coluna("Jornada", f -> f.getJornada() + " (" + f.getJornada().getEscala() + ")")
                .coluna("Contratação", f -> Formatos.data(f.getDataContratacao()))
                .coluna("Salário", f -> Formatos.moeda(f.calcularSalario()));
        modeloFuncionarios.setLinhas(funcionarios);
        JTable tabelaFuncionarios = Componentes.tabela(modeloFuncionarios);
        Componentes.larguras(tabelaFuncionarios, 200, 120, 170, 100, 110);
        Componentes.alinharDireita(tabelaFuncionarios, 4);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Quartos (" + quartos.size() + ")", Componentes.rolagem(tabelaQuartos));
        abas.addTab("Funcionários (" + funcionarios.size() + ")", Componentes.rolagem(tabelaFuncionarios));
        conteudo.add(abas, BorderLayout.CENTER);

        JButton fechar = Tema.botao("Fechar");
        fechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.add(fechar);
        conteudo.add(rodape, BorderLayout.SOUTH);

        setContentPane(conteudo);
        setSize(920, 520);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
