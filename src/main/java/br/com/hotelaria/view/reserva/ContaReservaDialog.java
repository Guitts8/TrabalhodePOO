package br.com.hotelaria.view.reserva;

import br.com.hotelaria.controller.ReservaController;
import br.com.hotelaria.model.ConsumoServico;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.Servico;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.Mensagens;
import br.com.hotelaria.view.Tema;
import br.com.hotelaria.view.TratadorErros;
import br.com.hotelaria.view.componentes.Componentes;
import br.com.hotelaria.view.componentes.ModeloTabela;
import br.com.hotelaria.view.componentes.RenderizadorSituacao;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class ContaReservaDialog extends JDialog {
    private final ReservaController controller;
    private Reserva reserva;

    private final ModeloTabela<ConsumoServico> modelo = new ModeloTabela<>();
    private final JTable tabela = Componentes.tabela(modelo);
    private final JLabel titulo = new JLabel();
    private final JLabel detalhes = Componentes.subtitulo(" ");
    private final JLabel situacao = new JLabel();
    private final JComboBox<Servico> servico;
    private final JSpinner quantidade = Componentes.numero(1, 1, 99);
    private final JButton lancar = Tema.botaoPrimario("Lançar serviço");
    private final JButton remover = Tema.botao("Remover selecionado");

    private final JLabel valorDiarias = valor();
    private final JLabel valorDesconto = valor();
    private final JLabel descricaoDesconto = Componentes.subtitulo(" ");
    private final JLabel valorServicos = valor();
    private final JLabel valorTotal = valor();
    private final JLabel datas = Componentes.subtitulo(" ");

    public ContaReservaDialog(Component pai, Reserva reserva, ReservaController controller) {
        super(FormularioDialog.janelaDe(pai), "Conta da reserva nº " + reserva.getId(), ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.reserva = reserva;
        this.servico = Componentes.combo(controller.servicosAtivos());

        JPanel conteudo = new JPanel(new BorderLayout(16, 14));
        conteudo.setBorder(new EmptyBorder(20, 22, 16, 22));

        JPanel cabecalho = new JPanel(new GridLayout(0, 1, 0, 3));
        titulo.setFont(Tema.fonte(Font.BOLD, 18f));
        situacao.setFont(Tema.fonte(Font.BOLD, 13f));
        cabecalho.add(titulo);
        cabecalho.add(detalhes);
        cabecalho.add(situacao);
        conteudo.add(cabecalho, BorderLayout.NORTH);

        modelo.coluna("Data", c -> Formatos.dataHora(c.getDataConsumo()))
                .coluna("Serviço", c -> c.getServico().getNome())
                .coluna("Categoria", c -> c.getServico().getCategoria())
                .coluna("Qtd", ConsumoServico::getQuantidade)
                .coluna("Valor unit.", c -> Formatos.moeda(c.getValorUnitario()))
                .coluna("Total", c -> Formatos.moeda(c.getValorTotal()))
                .coluna("Observação", ConsumoServico::getObservacao);
        Componentes.larguras(tabela, 115, 150, 120, 40, 85, 90, 170);
        Componentes.centralizar(tabela, 3);
        Componentes.alinharDireita(tabela, 4, 5);

        JPanel barraLancamento = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barraLancamento.add(Componentes.subtitulo("Serviço:"));
        barraLancamento.add(servico);
        barraLancamento.add(Componentes.subtitulo("Qtd:"));
        barraLancamento.add(quantidade);
        barraLancamento.add(lancar);
        barraLancamento.add(remover);
        lancar.addActionListener(e -> TratadorErros.executar(this, this::lancarServico));
        remover.addActionListener(e -> TratadorErros.executar(this, this::removerServico));

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        JLabel rotuloServicos = new JLabel("Serviços consumidos");
        rotuloServicos.setFont(Tema.fonte(Font.BOLD, 14f));
        centro.add(rotuloServicos, BorderLayout.NORTH);
        centro.add(Componentes.rolagem(tabela), BorderLayout.CENTER);
        centro.add(barraLancamento, BorderLayout.SOUTH);
        conteudo.add(centro, BorderLayout.CENTER);

        conteudo.add(montarResumo(), BorderLayout.EAST);

        JButton fechar = Tema.botao("Fechar");
        fechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.add(fechar);
        conteudo.add(rodape, BorderLayout.SOUTH);

        setContentPane(conteudo);
        setSize(1080, 560);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        atualizarTela();
    }

    private JPanel montarResumo() {
        JPanel resumo = new JPanel(new GridBagLayout());
        resumo.setBackground(new Color(0xF8FAFC));
        resumo.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Tema.BORDA), new EmptyBorder(14, 16, 14, 16)));
        resumo.setPreferredSize(new Dimension(320, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 8, 0);

        JLabel tituloResumo = new JLabel("Resumo da conta");
        tituloResumo.setFont(Tema.fonte(Font.BOLD, 14f));
        resumo.add(tituloResumo, c);

        c.gridy++;
        resumo.add(linha("Hospedagem", valorDiarias), c);
        c.gridy++;
        resumo.add(linha("Descontos", valorDesconto), c);
        c.gridy++;
        resumo.add(descricaoDesconto, c);
        c.gridy++;
        resumo.add(linha("Serviços", valorServicos), c);
        c.gridy++;
        resumo.add(new JSeparator(), c);
        c.gridy++;
        JLabel rotuloTotal = new JLabel("TOTAL");
        rotuloTotal.setFont(Tema.fonte(Font.BOLD, 15f));
        valorTotal.setFont(Tema.fonte(Font.BOLD, 20f));
        valorTotal.setForeground(Tema.PRIMARIA);
        resumo.add(linha(rotuloTotal, valorTotal), c);
        c.gridy++;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        resumo.add(datas, c);
        return resumo;
    }

    private void atualizarTela() {
        titulo.setText("Reserva nº " + reserva.getId() + " - " + reserva.getHospede().getNome());
        detalhes.setText(reserva.getQuarto().getHotel().getNome() + "   |   Quarto " + reserva.getQuarto().getNumero()
                + " (" + reserva.getQuarto().getTipo() + ")   |   " + Formatos.data(reserva.getDataEntrada()) + " a "
                + Formatos.data(reserva.getDataSaida()) + "   |   " + reserva.getQuantidadeHospedes() + " hóspede(s)");
        situacao.setText("Situação: " + reserva.getStatus());
        situacao.setForeground(RenderizadorSituacao.corDa(reserva.getStatus()));

        modelo.setLinhas(reserva.getConsumos());

        valorDiarias.setText(Formatos.moeda(reserva.getValorHospedagem()));
        valorDesconto.setText("- " + Formatos.moeda(reserva.getValorDesconto()));
        descricaoDesconto.setText(reserva.getDescricaoDesconto() == null
                ? " " : "<html><body style='width: 230px'>" + reserva.getDescricaoDesconto() + "</body></html>");
        valorServicos.setText(Formatos.moeda(reserva.getValorServicos()));
        valorTotal.setText(Formatos.moeda(reserva.getValorTotal()));

        String textoDatas = "<html>" + reserva.getQuantidadeDiarias() + " diária(s) de " + Formatos.moeda(reserva.getValorDiaria());
        if (reserva.getDataCheckIn() != null) {
            textoDatas += "<br>Check-in: " + Formatos.dataHora(reserva.getDataCheckIn());
        }
        if (reserva.getDataCheckOut() != null) {
            textoDatas += "<br>Check-out: " + Formatos.dataHora(reserva.getDataCheckOut());
        }
        datas.setText(textoDatas + "</html>");

        boolean aberta = reserva.getStatus() != StatusReserva.FINALIZADA && reserva.getStatus() != StatusReserva.CANCELADA;
        servico.setEnabled(aberta);
        quantidade.setEnabled(aberta);
        lancar.setEnabled(aberta);
        remover.setEnabled(aberta);
        if (reserva.getStatus() == StatusReserva.FINALIZADA) {
            setTitle("Conta final da reserva nº " + reserva.getId());
        }
    }

    private void lancarServico() {
        reserva = controller.lancarServico(reserva, (Servico) servico.getSelectedItem(), Componentes.valor(quantidade));
        quantidade.setValue(1);
        atualizarTela();
    }

    private void removerServico() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Mensagens.aviso(this, "Selecione na tabela o serviço que deseja remover.");
            return;
        }
        ConsumoServico consumo = modelo.getLinha(linha);
        if (Mensagens.confirmar(this, "Remover " + consumo.getServico().getNome() + " (x" + consumo.getQuantidade()
                + ") desta conta?")) {
            reserva = controller.removerServico(reserva, consumo);
            atualizarTela();
        }
    }

    private static JLabel valor() {
        JLabel rotulo = new JLabel("-");
        rotulo.setFont(Tema.fonte(Font.BOLD, 13f));
        return rotulo;
    }

    private static JPanel linha(String texto, JLabel valor) {
        return linha(Componentes.subtitulo(texto), valor);
    }

    private static JPanel linha(JLabel rotulo, JLabel valor) {
        JPanel linha = new JPanel(new BorderLayout(8, 0));
        linha.setOpaque(false);
        linha.add(rotulo, BorderLayout.WEST);
        linha.add(valor, BorderLayout.EAST);
        return linha;
    }
}
