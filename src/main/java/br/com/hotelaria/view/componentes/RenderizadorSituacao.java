package br.com.hotelaria.view.componentes;

import br.com.hotelaria.model.StatusQuarto;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.view.Tema;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RenderizadorSituacao extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado,
                                                   boolean foco, int linha, int coluna) {
        super.getTableCellRendererComponent(tabela, valor, selecionado, foco, linha, coluna);
        Color cor = corDa(valor);
        setIcon(new Bolinha(cor));
        setIconTextGap(8);
        setFont(getFont().deriveFont(Font.BOLD));
        if (!selecionado) {
            setForeground(cor);
        }
        return this;
    }

    public static Color corDa(Object valor) {
        if (valor instanceof StatusReserva status) {
            return switch (status) {
                case PENDENTE -> Tema.LARANJA;
                case CONFIRMADA -> Tema.AZUL;
                case EM_ANDAMENTO -> Tema.VERDE;
                case FINALIZADA -> Tema.CINZA;
                case CANCELADA -> Tema.VERMELHO;
            };
        }
        if (valor instanceof StatusQuarto status) {
            return switch (status) {
                case DISPONIVEL -> Tema.VERDE;
                case OCUPADO -> Tema.VERMELHO;
                case LIMPEZA -> Tema.AMARELO;
                case MANUTENCAO -> Tema.CINZA;
            };
        }
        String texto = String.valueOf(valor);
        return switch (texto) {
            case "Ativo", "Trabalhando", "Concluída", "Disponível" -> Tema.VERDE;
            case "Em férias", "Em andamento", "Programada" -> Tema.AZUL;
            case "Inativo", "Ocupado no período" -> Tema.VERMELHO;
            default -> Tema.TEXTO;
        };
    }

    private static class Bolinha implements Icon {
        private final Color cor;

        Bolinha(Color cor) {
            this.cor = cor;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.fillOval(x, y, getIconWidth(), getIconHeight());
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 9;
        }

        @Override
        public int getIconHeight() {
            return 9;
        }
    }
}
