package br.com.hotelaria.view.quarto;

import br.com.hotelaria.controller.QuartoController;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.Tema;
import br.com.hotelaria.view.TratadorErros;
import br.com.hotelaria.view.componentes.Componentes;
import br.com.hotelaria.view.componentes.ModeloTabela;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DisponibilidadeDialog extends JDialog {
    private final QuartoController controller;
    private final JComboBox<Hotel> hotel;
    private final JFormattedTextField entrada = Componentes.data();
    private final JFormattedTextField saida = Componentes.data();
    private final JSpinner hospedes = Componentes.numero(2, 1, 10);
    private final ModeloTabela<Quarto> modelo = new ModeloTabela<>();
    private final JLabel resultado = Componentes.subtitulo("Escolha o hotel e o período e clique em Buscar.");

    private long diarias;
    private int pessoas;

    public DisponibilidadeDialog(Component pai, QuartoController controller) {
        super(FormularioDialog.janelaDe(pai), "Consultar disponibilidade", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.hotel = Componentes.combo(controller.listarHoteis());

        entrada.setText(Formatos.data(LocalDate.now()));
        saida.setText(Formatos.data(LocalDate.now().plusDays(2)));

        JPanel conteudo = new JPanel(new BorderLayout(0, 12));
        conteudo.setBorder(new EmptyBorder(20, 22, 16, 22));

        JPanel topo = new JPanel(new BorderLayout(0, 12));
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        JLabel titulo = new JLabel("Quartos livres no período");
        titulo.setFont(Tema.fonte(Font.BOLD, 18f));
        textos.add(titulo);
        textos.add(Componentes.subtitulo("Não aparecem quartos em manutenção, sem capacidade ou com reserva que cruza as datas."));
        topo.add(textos, BorderLayout.NORTH);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.add(Componentes.subtitulo("Hotel:"));
        filtros.add(hotel);
        filtros.add(Componentes.subtitulo("Entrada:"));
        filtros.add(entrada);
        filtros.add(Componentes.subtitulo("Saída:"));
        filtros.add(saida);
        filtros.add(Componentes.subtitulo("Hóspedes:"));
        filtros.add(hospedes);
        JButton buscar = Tema.botaoPrimario("Buscar");
        buscar.addActionListener(e -> TratadorErros.executar(this, this::buscar));
        filtros.add(buscar);
        topo.add(filtros, BorderLayout.CENTER);
        conteudo.add(topo, BorderLayout.NORTH);

        modelo.coluna("Nº", Quarto::getNumero)
                .coluna("Tipo", Quarto::getTipo)
                .coluna("Capacidade", q -> q.getCapacidade() + " pessoa(s)")
                .coluna("Diária", q -> Formatos.moeda(q.calcularDiaria()))
                .coluna("Características", Quarto::getCaracteristicas)
                .coluna("Valor do período", q -> Formatos.moeda(q.calcularValorHospedagem(diarias, pessoas)));
        JTable tabela = Componentes.tabela(modelo);
        Componentes.larguras(tabela, 50, 80, 95, 100, 330, 130);
        Componentes.centralizar(tabela, 0);
        Componentes.alinharDireita(tabela, 3, 5);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.add(Componentes.rolagem(tabela), BorderLayout.CENTER);
        centro.add(resultado, BorderLayout.SOUTH);
        conteudo.add(centro, BorderLayout.CENTER);

        JButton fechar = Tema.botao("Fechar");
        fechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.add(fechar);
        conteudo.add(rodape, BorderLayout.SOUTH);

        setContentPane(conteudo);
        getRootPane().setDefaultButton(buscar);
        setSize(960, 520);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void buscar() {
        LocalDate dataEntrada = Formatos.lerData(entrada.getText(), "Data de entrada");
        LocalDate dataSaida = Formatos.lerData(saida.getText(), "Data de saída");
        pessoas = Componentes.valor(hospedes);
        List<Quarto> livres = controller.buscarDisponiveis((Hotel) hotel.getSelectedItem(), dataEntrada, dataSaida, pessoas);
        diarias = ChronoUnit.DAYS.between(dataEntrada, dataSaida);
        modelo.setLinhas(livres);
        resultado.setText(livres.size() + " quarto(s) livre(s) de " + Formatos.data(dataEntrada) + " a "
                + Formatos.data(dataSaida) + " (" + diarias + " diária(s), " + pessoas + " hóspede(s)).");
    }
}
