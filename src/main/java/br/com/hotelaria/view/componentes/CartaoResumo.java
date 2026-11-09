package br.com.hotelaria.view.componentes;

import br.com.hotelaria.view.Tema;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public class CartaoResumo extends JPanel {
    private final Color cor;
    private final JLabel valor = new JLabel("-");
    private final JLabel detalhe = new JLabel(" ");

    public CartaoResumo(String titulo, Color cor) {
        this.cor = cor;
        setOpaque(false);
        setLayout(new BorderLayout(0, 2));
        setBorder(new EmptyBorder(14, 22, 14, 14));

        JLabel rotuloTitulo = new JLabel(titulo);
        rotuloTitulo.setFont(Tema.fonte(Font.PLAIN, 13f));
        rotuloTitulo.setForeground(Tema.TEXTO_SECUNDARIO);

        valor.setFont(Tema.fonte(Font.BOLD, 28f));
        valor.setForeground(Tema.TEXTO);

        detalhe.setFont(Tema.fonte(Font.PLAIN, 12f));
        detalhe.setForeground(Tema.TEXTO_SECUNDARIO);

        add(rotuloTitulo, BorderLayout.NORTH);
        add(valor, BorderLayout.CENTER);
        add(detalhe, BorderLayout.SOUTH);
    }

    public void setValor(String texto, String textoDetalhe) {
        valor.setText(texto);
        detalhe.setText(textoDetalhe == null || textoDetalhe.isEmpty() ? " " : textoDetalhe);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D fundo = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.setColor(Color.WHITE);
        g2.fill(fundo);
        g2.setColor(Tema.BORDA);
        g2.draw(fundo);

        g2.clip(fundo);
        g2.setColor(cor);
        g2.fillRect(0, 0, 6, getHeight());
        g2.dispose();
    }
}
