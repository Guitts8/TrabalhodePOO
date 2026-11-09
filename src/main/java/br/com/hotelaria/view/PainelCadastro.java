package br.com.hotelaria.view;

import br.com.hotelaria.view.componentes.Componentes;
import br.com.hotelaria.view.componentes.ModeloTabela;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchIcon;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public abstract class PainelCadastro<T> extends JPanel implements Atualizavel {
    protected final ModeloTabela<T> modelo = new ModeloTabela<>();
    protected final JTable tabela = Componentes.tabela(modelo);
    protected final JTextField campoBusca = new JTextField(22);

    private final JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private final JPanel botoesCadastro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    private final JPanel botoesAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private final JLabel rotuloTotal = Componentes.subtitulo(" ");

    protected PainelCadastro(String titulo, String subtitulo, String dicaBusca) {
        super(new BorderLayout(0, 14));
        setBackground(Tema.FUNDO);
        setBorder(new EmptyBorder(22, 26, 18, 26));

        add(montarCabecalho(titulo, subtitulo, dicaBusca), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.setOpaque(false);
        centro.add(Componentes.rolagem(tabela), BorderLayout.CENTER);
        centro.add(rotuloTotal, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        botoesCadastro.setOpaque(false);
        botoesAcoes.setOpaque(false);
        rodape.add(botoesCadastro, BorderLayout.WEST);
        rodape.add(botoesAcoes, BorderLayout.EAST);
        add(rodape, BorderLayout.SOUTH);

        JButton novo = Tema.botaoPrimario("+ Novo");
        novo.addActionListener(e -> executar(this::novo));
        botoesCadastro.add(novo);
        adicionarBotao("Editar", () -> comSelecionado(this::editar));
        adicionarBotao("Excluir", () -> comSelecionado(this::confirmarExclusao));

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabela.getSelectedRow() >= 0) {
                    executar(() -> comSelecionado(PainelCadastro.this::editar));
                }
            }
        });

        Timer atrasoBusca = new Timer(300, e -> atualizar());
        atrasoBusca.setRepeats(false);
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atrasoBusca.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atrasoBusca.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atrasoBusca.restart();
            }
        });
    }

    protected abstract List<T> buscarDados(String filtro);

    protected abstract void novo();

    protected abstract void editar(T item);

    protected abstract void excluir(T item);

    protected abstract String descrever(T item);

    @Override
    public void atualizar() {
        executar(() -> {
            T selecionado = getSelecionado();
            List<T> dados = buscarDados(campoBusca.getText().trim());
            modelo.setLinhas(dados);
            rotuloTotal.setText(textoTotal(dados));

            int indice = selecionado == null ? -1 : dados.indexOf(selecionado);
            if (indice >= 0) {
                tabela.setRowSelectionInterval(indice, indice);
            }
        });
    }

    protected String textoTotal(List<T> dados) {
        return dados.size() == 1 ? "1 registro" : dados.size() + " registros";
    }

    protected void executar(Runnable acao) {
        TratadorErros.executar(this, acao);
    }

    protected T getSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return null;
        }
        return modelo.getLinha(tabela.convertRowIndexToModel(linha));
    }

    protected void comSelecionado(Consumer<T> acao) {
        T item = getSelecionado();
        if (item == null) {
            Mensagens.aviso(this, "Selecione um registro na tabela primeiro.");
            return;
        }
        acao.accept(item);
    }

    private void confirmarExclusao(T item) {
        if (Mensagens.confirmar(this, "Deseja realmente excluir " + descrever(item) + "?")) {
            excluir(item);
            atualizar();
        }
    }

    protected JButton adicionarBotao(String texto, Runnable acao) {
        JButton botao = Tema.botao(texto);
        botao.addActionListener(e -> executar(acao));
        botoesCadastro.add(botao);
        return botao;
    }

    protected JButton adicionarAcao(String texto, Runnable acao) {
        JButton botao = Tema.botao(texto);
        botao.addActionListener(e -> executar(acao));
        botoesAcoes.add(botao);
        return botao;
    }

    protected void adicionarFiltro(String rotulo, JComponent componente) {
        int posicao = filtros.getComponentCount() - 1;
        filtros.add(Componentes.subtitulo(rotulo), posicao);
        filtros.add(componente, posicao + 1);
    }

    protected void configurarColunas(int... larguras) {
        Componentes.larguras(tabela, larguras);
    }

    private JPanel montarCabecalho(String titulo, String subtitulo, String dicaBusca) {
        campoBusca.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, dicaBusca);
        campoBusca.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSearchIcon());
        campoBusca.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        filtros.setOpaque(false);
        filtros.add(campoBusca);

        JPanel direita = new JPanel(new GridBagLayout());
        direita.setOpaque(false);
        direita.add(filtros);

        JPanel linhaTitulo = new JPanel(new BorderLayout(16, 0));
        linhaTitulo.setOpaque(false);
        linhaTitulo.add(Componentes.titulo(titulo), BorderLayout.WEST);
        linhaTitulo.add(direita, BorderLayout.EAST);

        JPanel cabecalho = new JPanel(new BorderLayout(0, 2));
        cabecalho.setOpaque(false);
        cabecalho.add(linhaTitulo, BorderLayout.NORTH);
        cabecalho.add(Componentes.subtitulo(subtitulo), BorderLayout.SOUTH);
        return cabecalho;
    }
}
