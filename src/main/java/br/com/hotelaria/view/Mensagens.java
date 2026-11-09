package br.com.hotelaria.view;

import javax.swing.JOptionPane;
import java.awt.Component;

public final class Mensagens {
    private Mensagens() {
    }

    public static void info(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, formatar(mensagem), "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, formatar(mensagem), "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static void erro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, formatar(mensagem), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component pai, String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(pai, formatar(mensagem), "Confirmação",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return resposta == JOptionPane.YES_OPTION;
    }

    private static String formatar(String mensagem) {
        if (mensagem == null) {
            return "";
        }
        if (mensagem.length() < 70 && !mensagem.contains("\n")) {
            return mensagem;
        }
        String texto = mensagem.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
        return "<html><body style='width: 360px'>" + texto + "</body></html>";
    }
}
