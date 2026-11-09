package br.com.hotelaria.view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class TelaCarregamento extends JWindow {
    public TelaCarregamento() {
        JPanel painel = new JPanel(new BorderLayout(0, 18));
        painel.setBackground(Tema.MENU_FUNDO);
        painel.setBorder(new EmptyBorder(30, 36, 28, 36));

        JLabel nome = new JLabel("Rede Hoteleira");
        nome.setFont(Tema.fonte(Font.BOLD, 26f));
        nome.setForeground(Color.WHITE);
        JLabel texto = new JLabel("Iniciando o sistema e conectando ao banco de dados...");
        texto.setForeground(Tema.MENU_TEXTO);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 6));
        textos.setOpaque(false);
        textos.add(nome);
        textos.add(texto);

        JProgressBar barra = new JProgressBar();
        barra.setIndeterminate(true);
        barra.setPreferredSize(new Dimension(360, 6));

        painel.add(textos, BorderLayout.CENTER);
        painel.add(barra, BorderLayout.SOUTH);
        setContentPane(painel);
        pack();
        setLocationRelativeTo(null);
    }
}
