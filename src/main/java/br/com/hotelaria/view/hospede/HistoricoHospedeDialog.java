package br.com.hotelaria.view.hospede;

import br.com.hotelaria.controller.HospedeController;
import br.com.hotelaria.model.ConsumoServico;
import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.Tema;
import br.com.hotelaria.view.componentes.Componentes;
import br.com.hotelaria.view.componentes.ModeloTabela;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;

public class HistoricoHospedeDialog extends JDialog {
    private final ModeloTabela<ConsumoServico> modeloConsumos = new ModeloTabela<>();
    private final JLabel tituloConsumos = new JLabel("Serviços consumidos (selecione uma reserva acima)");

    public HistoricoHospedeDialog(Component pai, Hospede hospede, HospedeController controller) {
        super(FormularioDialog.janelaDe(pai), "Histórico de " + hospede.getNome(), ModalityType.APPLICATION_MODAL);

        List<Reserva> reservas = controller.historico(hospede);
        long finalizadas = 0;
        BigDecimal totalGasto = BigDecimal.ZERO;
        long noites = 0;
        for (Reserva reserva : reservas) {
            if (reserva.getStatus() == StatusReserva.FINALIZADA) {
                finalizadas++;
                totalGasto = totalGasto.add(reserva.getValorTotal());
                noites += reserva.getQuantidadeDiarias();
            }
        }

        JPanel conteudo = new JPanel(new BorderLayout(0, 12));
        conteudo.setBorder(new EmptyBorder(20, 22, 16, 22));

        JPanel cabecalho = new JPanel(new GridLayout(0, 1, 0, 3));
        JLabel nome = new JLabel(hospede.getNome());
        nome.setFont(Tema.fonte(Font.BOLD, 18f));
        cabecalho.add(nome);
        cabecalho.add(Componentes.subtitulo("CPF " + hospede.getCpfFormatado() + "     " + hospede.getIdade() + " anos     "
                + (hospede.getEmail() == null ? "" : hospede.getEmail() + "     ")
                + (hospede.getTelefone() == null ? "" : hospede.getTelefone())));
        cabecalho.add(Componentes.destaque(reservas.size() + " reserva(s) no total  |  " + finalizadas
                + " hospedagem(ns) finalizada(s), " + noites + " noite(s)  |  Total gasto: "
                + Formatos.moeda(totalGasto), Tema.PRIMARIA));
        conteudo.add(cabecalho, BorderLayout.NORTH);

        ModeloTabela<Reserva> modeloReservas = new ModeloTabela<Reserva>()
                .coluna("Nº", Reserva::getId)
                .coluna("Hotel", r -> r.getQuarto().getHotel().getNome())
                .coluna("Quarto", r -> r.getQuarto().getNumero() + " - " + r.getQuarto().getTipo())
                .coluna("Entrada", r -> Formatos.data(r.getDataEntrada()))
                .coluna("Saída", r -> Formatos.data(r.getDataSaida()))
                .coluna("Diárias", Reserva::getQuantidadeDiarias)
                .coluna("Serviços", r -> Formatos.moeda(r.getValorServicos()))
                .coluna("Total", r -> Formatos.moeda(r.getValorTotal()))
                .coluna("Situação", Reserva::getStatus);
        modeloReservas.setLinhas(reservas);
        JTable tabelaReservas = Componentes.tabela(modeloReservas);
        Componentes.larguras(tabelaReservas, 45, 170, 120, 90, 90, 60, 95, 105, 120);
        Componentes.centralizar(tabelaReservas, 0, 3, 4, 5);
        Componentes.alinharDireita(tabelaReservas, 6, 7);
        Componentes.coresDeSituacao(tabelaReservas, 8);

        modeloConsumos.coluna("Data", c -> Formatos.dataHora(c.getDataConsumo()))
                .coluna("Serviço", c -> c.getServico().getNome())
                .coluna("Qtd", ConsumoServico::getQuantidade)
                .coluna("Valor unit.", c -> Formatos.moeda(c.getValorUnitario()))
                .coluna("Total", c -> Formatos.moeda(c.getValorTotal()))
                .coluna("Observação", ConsumoServico::getObservacao);
        JTable tabelaConsumos = Componentes.tabela(modeloConsumos);
        Componentes.larguras(tabelaConsumos, 120, 170, 45, 95, 95, 200);
        Componentes.centralizar(tabelaConsumos, 2);
        Componentes.alinharDireita(tabelaConsumos, 3, 4);

        tabelaReservas.getSelectionModel().addListSelectionListener(e -> {
            int linha = tabelaReservas.getSelectedRow();
            if (!e.getValueIsAdjusting() && linha >= 0) {
                Reserva reserva = modeloReservas.getLinha(linha);
                modeloConsumos.setLinhas(reserva.getConsumos());
                tituloConsumos.setText("Serviços consumidos na reserva nº " + reserva.getId()
                        + " (" + reserva.getConsumos().size() + ")");
            }
        });

        tituloConsumos.setFont(Tema.fonte(Font.BOLD, 13f));
        JPanel parteConsumos = new JPanel(new BorderLayout(0, 6));
        parteConsumos.add(tituloConsumos, BorderLayout.NORTH);
        parteConsumos.add(Componentes.rolagem(tabelaConsumos), BorderLayout.CENTER);

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 12));
        centro.add(Componentes.rolagem(tabelaReservas));
        centro.add(parteConsumos);
        conteudo.add(centro, BorderLayout.CENTER);

        JButton fechar = Tema.botao("Fechar");
        fechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.add(fechar);
        conteudo.add(rodape, BorderLayout.SOUTH);

        setContentPane(conteudo);
        setSize(980, 620);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if (!reservas.isEmpty()) {
            tabelaReservas.setRowSelectionInterval(0, 0);
        }
    }
}
