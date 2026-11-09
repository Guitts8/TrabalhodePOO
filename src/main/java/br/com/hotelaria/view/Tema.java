package br.com.hotelaria.view;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.JButton;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Map;

public final class Tema {
    public static final Color PRIMARIA = new Color(0x2563EB);
    public static final Color MENU_FUNDO = new Color(0x0F172A);
    public static final Color MENU_SELECIONADO = new Color(0x1E293B);
    public static final Color MENU_TEXTO = new Color(0xCBD5E1);
    public static final Color FUNDO = new Color(0xF1F5F9);
    public static final Color BORDA = new Color(0xE2E8F0);
    public static final Color TEXTO = new Color(0x0F172A);
    public static final Color TEXTO_SECUNDARIO = new Color(0x64748B);

    public static final Color VERDE = new Color(0x16A34A);
    public static final Color VERMELHO = new Color(0xDC2626);
    public static final Color LARANJA = new Color(0xEA580C);
    public static final Color AMARELO = new Color(0xCA8A04);
    public static final Color AZUL = new Color(0x2563EB);
    public static final Color ROXO = new Color(0x7C3AED);
    public static final Color CINZA = new Color(0x64748B);

    private Tema() {
    }

    public static void aplicar() {
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", "#2563EB"));
        FlatLightLaf.setup();

        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("Table.alternateRowColor", new Color(0xF8FAFC));
        UIManager.put("Table.gridColor", BORDA);
        UIManager.put("TableHeader.height", 34);
        UIManager.put("TableHeader.background", new Color(0xF8FAFC));

        UIManager.put("OptionPane.yesButtonText", "Sim");
        UIManager.put("OptionPane.noButtonText", "Não");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "OK");
    }

    public static Font fonte(int estilo, float tamanho) {
        return UIManager.getFont("Label.font").deriveFont(estilo, tamanho);
    }

    public static JButton botaoPrimario(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(PRIMARIA);
        botao.setForeground(Color.WHITE);
        botao.setFont(fonte(Font.BOLD, 13f));
        return botao;
    }

    public static JButton botao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(fonte(Font.PLAIN, 13f));
        return botao;
    }
}
