package br.com.hotelaria.view.funcionario;

import br.com.hotelaria.controller.FuncionarioController;
import br.com.hotelaria.model.Ferias;
import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.Mensagens;
import br.com.hotelaria.view.Tema;
import br.com.hotelaria.view.TratadorErros;
import br.com.hotelaria.view.componentes.Componentes;
import br.com.hotelaria.view.componentes.ModeloTabela;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;

public class FeriasDialog extends JDialog {
    private final FuncionarioController controller;
    private final Funcionario funcionario;
    private final ModeloTabela<Ferias> modelo = new ModeloTabela<>();
    private final JTable tabela = Componentes.tabela(modelo);
    private final JFormattedTextField inicio = Componentes.data();
    private final JFormattedTextField fim = Componentes.data();
    private final JLabel resumo = Componentes.destaque(" ", Tema.PRIMARIA);

    public FeriasDialog(Component pai, Funcionario funcionario, FuncionarioController controller) {
        super(FormularioDialog.janelaDe(pai), "Férias de " + funcionario.getNome(), ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.funcionario = funcionario;

        JPanel conteudo = new JPanel(new BorderLayout(0, 12));
        conteudo.setBorder(new EmptyBorder(20, 22, 16, 22));

        JPanel cabecalho = new JPanel(new GridLayout(0, 1, 0, 3));
        JLabel nome = new JLabel(funcionario.getNome() + " - " + funcionario.getCargo());
        nome.setFont(Tema.fonte(Font.BOLD, 18f));
        cabecalho.add(nome);
        cabecalho.add(Componentes.subtitulo(funcionario.getHotel().getNome() + "   |   Contratação: "
                + Formatos.data(funcionario.getDataContratacao()) + "   |   Direito a férias a partir de "
                + Formatos.data(funcionario.getDataDireitoFerias())));
        cabecalho.add(resumo);
        conteudo.add(cabecalho, BorderLayout.NORTH);

        modelo.coluna("Início", f -> Formatos.data(f.getDataInicio()))
                .coluna("Fim", f -> Formatos.data(f.getDataFim()))
                .coluna("Dias", Ferias::getDias)
                .coluna("Situação", Ferias::getSituacao);
        Componentes.larguras(tabela, 120, 120, 70, 140);
        Componentes.centralizar(tabela, 0, 1, 2);
        Componentes.coresDeSituacao(tabela, 3);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.add(Componentes.rolagem(tabela), BorderLayout.CENTER);
        centro.add(Componentes.subtitulo("Regras: períodos de 5 a 30 dias, até 30 dias por ano, "
                + "só após 12 meses de contratação e sem cruzar outro período."), BorderLayout.SOUTH);
        conteudo.add(centro, BorderLayout.CENTER);

        JButton agendar = Tema.botaoPrimario("Agendar férias");
        agendar.addActionListener(e -> TratadorErros.executar(this, this::agendar));
        JButton remover = Tema.botao("Remover selecionado");
        remover.addActionListener(e -> TratadorErros.executar(this, this::remover));
        JButton fechar = Tema.botao("Fechar");
        fechar.addActionListener(e -> dispose());

        JPanel novoPeriodo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        novoPeriodo.add(Componentes.subtitulo("Início:"));
        novoPeriodo.add(inicio);
        novoPeriodo.add(Componentes.subtitulo("Fim:"));
        novoPeriodo.add(fim);
        novoPeriodo.add(agendar);
        novoPeriodo.add(remover);
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.add(novoPeriodo, BorderLayout.WEST);
        rodape.add(fechar, BorderLayout.EAST);
        conteudo.add(rodape, BorderLayout.SOUTH);

        setContentPane(conteudo);
        setSize(860, 480);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        carregar();
    }

    private void carregar() {
        modelo.setLinhas(controller.listarFerias(funcionario));
        int ano = LocalDate.now().getYear();
        resumo.setText("Dias de férias marcados em " + ano + ": "
                + controller.diasDeFeriasNoAno(funcionario, ano) + " de 30");
    }

    private void agendar() {
        LocalDate dataInicio = Formatos.lerData(inicio.getText(), "Data de início");
        LocalDate dataFim = Formatos.lerData(fim.getText(), "Data de fim");
        controller.agendarFerias(funcionario, dataInicio, dataFim);
        inicio.setText("");
        fim.setText("");
        carregar();
        Mensagens.info(this, "Férias agendadas de " + Formatos.data(dataInicio) + " a " + Formatos.data(dataFim) + ".");
    }

    private void remover() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Mensagens.aviso(this, "Selecione na tabela o período que deseja remover.");
            return;
        }
        Ferias ferias = modelo.getLinha(linha);
        if (Mensagens.confirmar(this, "Remover as férias de " + Formatos.data(ferias.getDataInicio()) + " a "
                + Formatos.data(ferias.getDataFim()) + "?")) {
            controller.cancelarFerias(funcionario, ferias);
            carregar();
        }
    }
}
