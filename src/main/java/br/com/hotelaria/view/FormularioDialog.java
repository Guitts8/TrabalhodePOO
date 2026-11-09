package br.com.hotelaria.view;

import br.com.hotelaria.view.componentes.Componentes;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;

public abstract class FormularioDialog extends JDialog {
    protected final JButton botaoSalvar = Tema.botaoPrimario("Salvar");

    private final JPanel campos = new JPanel(new GridBagLayout());
    private final int colunas;
    private int linha;
    private int coluna;
    private boolean salvo;

    protected FormularioDialog(Component pai, String titulo, String subtitulo, int colunas) {
        super(janelaDe(pai), titulo, ModalityType.APPLICATION_MODAL);
        this.colunas = colunas;

        JPanel conteudo = new JPanel(new BorderLayout(0, 16));
        conteudo.setBorder(new EmptyBorder(20, 22, 18, 22));

        JPanel cabecalho = new JPanel(new GridLayout(0, 1, 0, 2));
        JLabel rotuloTitulo = new JLabel(titulo);
        rotuloTitulo.setFont(Tema.fonte(Font.BOLD, 18f));
        cabecalho.add(rotuloTitulo);
        if (subtitulo != null) {
            cabecalho.add(Componentes.subtitulo(subtitulo));
        }
        conteudo.add(cabecalho, BorderLayout.NORTH);
        conteudo.add(campos, BorderLayout.CENTER);

        JButton cancelar = Tema.botao("Cancelar");
        cancelar.addActionListener(e -> dispose());
        botaoSalvar.addActionListener(e -> {
            if (TratadorErros.executar(this, this::salvar)) {
                salvo = true;
                dispose();
            }
        });
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rodape.add(cancelar);
        rodape.add(botaoSalvar);
        conteudo.add(rodape, BorderLayout.SOUTH);

        setContentPane(conteudo);
        getRootPane().setDefaultButton(botaoSalvar);
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    protected abstract void salvar();

    public boolean abrir() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return salvo;
    }

    protected void campo(String rotulo, JComponent componente) {
        campo(rotulo, componente, 1);
    }

    protected void campo(String rotulo, JComponent componente, int largura) {
        JPanel celula = new JPanel(new BorderLayout(0, 4));
        celula.setOpaque(false);
        JLabel rotuloCampo = new JLabel(rotulo);
        rotuloCampo.setFont(Tema.fonte(Font.BOLD, 12f));
        rotuloCampo.setForeground(Tema.TEXTO_SECUNDARIO);
        celula.add(rotuloCampo, BorderLayout.NORTH);
        celula.add(componente, BorderLayout.CENTER);
        posicionar(celula, largura);
    }

    protected void componente(JComponent componente, int largura) {
        posicionar(componente, largura);
    }

    protected void secao(String titulo) {
        if (coluna > 0) {
            linha++;
            coluna = 0;
        }
        JLabel rotulo = new JLabel(titulo.toUpperCase());
        rotulo.setFont(Tema.fonte(Font.BOLD, 11.5f));
        rotulo.setForeground(Tema.PRIMARIA);
        GridBagConstraints c = restricoes();
        c.gridx = 0;
        c.gridy = linha;
        c.gridwidth = colunas;
        c.insets = new Insets(linha == 0 ? 0 : 14, 6, 2, 6);
        campos.add(rotulo, c);
        linha++;
    }

    private void posicionar(JComponent componente, int largura) {
        int ocupa = Math.min(largura, colunas);
        if (coluna + ocupa > colunas) {
            linha++;
            coluna = 0;
        }
        GridBagConstraints c = restricoes();
        c.gridx = coluna;
        c.gridy = linha;
        c.gridwidth = ocupa;
        campos.add(componente, c);
        coluna += ocupa;
        if (coluna >= colunas) {
            linha++;
            coluna = 0;
        }
    }

    private GridBagConstraints restricoes() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.insets = new Insets(4, 6, 6, 6);
        return c;
    }

    public static Window janelaDe(Component componente) {
        if (componente == null) {
            return null;
        }
        if (componente instanceof Window janela) {
            return janela;
        }
        return SwingUtilities.getWindowAncestor(componente);
    }
}
