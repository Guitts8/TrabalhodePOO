package br.com.hotelaria.view.componentes;

import br.com.hotelaria.view.Tema;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.MaskFormatter;
import java.awt.Color;
import java.awt.Font;
import java.text.ParseException;
import java.util.List;

public final class Componentes {
    private Componentes() {
    }

    public static JTextField texto() {
        return new JTextField(18);
    }

    public static JTextField texto(String dica) {
        JTextField campo = texto();
        campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, dica);
        return campo;
    }

    public static JFormattedTextField data() {
        return mascara("##/##/####");
    }

    public static JFormattedTextField hora() {
        return mascara("##:##");
    }

    public static JFormattedTextField cpf() {
        return mascara("###.###.###-##");
    }

    public static JFormattedTextField mascara(String formato) {
        try {
            MaskFormatter formatador = new MaskFormatter(formato);
            formatador.setPlaceholderCharacter('_');
            JFormattedTextField campo = new JFormattedTextField(formatador);
            campo.setColumns(12);

            campo.setFocusLostBehavior(JFormattedTextField.PERSIST);
            return campo;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Máscara inválida: " + formato, e);
        }
    }

    public static JSpinner numero(int valor, int minimo, int maximo) {
        return new JSpinner(new SpinnerNumberModel(valor, minimo, maximo, 1));
    }

    public static int valor(JSpinner spinner) {
        return ((Number) spinner.getValue()).intValue();
    }

    public static <E> JComboBox<E> combo(List<E> itens) {
        JComboBox<E> combo = new JComboBox<>();
        for (E item : itens) {
            combo.addItem(item);
        }
        return combo;
    }

    public static JTextArea areaTexto(int linhas) {
        JTextArea area = new JTextArea(linhas, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    public static JLabel titulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(Tema.fonte(Font.BOLD, 22f));
        rotulo.setForeground(Tema.TEXTO);
        return rotulo;
    }

    public static JLabel subtitulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(Tema.fonte(Font.PLAIN, 13f));
        rotulo.setForeground(Tema.TEXTO_SECUNDARIO);
        return rotulo;
    }

    public static JLabel destaque(String texto, Color cor) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(Tema.fonte(Font.BOLD, 14f));
        rotulo.setForeground(cor);
        return rotulo;
    }

    public static JTable tabela(ModeloTabela<?> modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getTableHeader().setFont(Tema.fonte(Font.BOLD, 12.5f));
        return tabela;
    }

    public static JScrollPane rolagem(JTable tabela) {
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        rolagem.getViewport().setBackground(Color.WHITE);
        return rolagem;
    }

    public static void larguras(JTable tabela, int... larguras) {
        ((ModeloTabela<?>) tabela.getModel()).fireTableStructureChanged();
        for (int i = 0; i < larguras.length && i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
    }

    public static void alinharDireita(JTable tabela, int... colunas) {
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int coluna : colunas) {
            tabela.getColumnModel().getColumn(coluna).setCellRenderer(direita);
        }
    }

    public static void centralizar(JTable tabela, int... colunas) {
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        for (int coluna : colunas) {
            tabela.getColumnModel().getColumn(coluna).setCellRenderer(centro);
        }
    }

    public static void coresDeSituacao(JTable tabela, int coluna) {
        tabela.getColumnModel().getColumn(coluna).setCellRenderer(new RenderizadorSituacao());
    }
}
