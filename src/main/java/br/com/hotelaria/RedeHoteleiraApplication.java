package br.com.hotelaria;

import br.com.hotelaria.view.Tema;
import br.com.hotelaria.view.TelaCarregamento;
import br.com.hotelaria.view.TelaPrincipal;
import br.com.hotelaria.view.TratadorErros;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API da Rede Hoteleira", version = "1.0",
        description = "Hotéis, quartos, hóspedes, reservas, serviços e funcionários da rede"))
public class RedeHoteleiraApplication {
    private static TelaCarregamento carregamento;

    public static void main(String[] args) {
        boolean semInterface = GraphicsEnvironment.isHeadless();

        if (!semInterface) {
            Tema.aplicar();
            naThreadDoSwing(() -> {
                carregamento = new TelaCarregamento();
                carregamento.setVisible(true);
            });
        }

        ConfigurableApplicationContext contexto;
        try {
            contexto = new SpringApplicationBuilder(RedeHoteleiraApplication.class)
                    .headless(semInterface)
                    .run(args);
        } catch (Exception e) {
            if (!semInterface) {
                naThreadDoSwing(() -> {
                    carregamento.dispose();
                    JOptionPane.showMessageDialog(null, "Não foi possível iniciar o sistema.\n\n" + explicarErro(e)
                                    + "\n\nDetalhe técnico: " + causaRaiz(e),
                            "Erro ao iniciar", JOptionPane.ERROR_MESSAGE);
                });
            }
            System.exit(1);
            return;
        }

        if (semInterface) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            Thread.setDefaultUncaughtExceptionHandler((thread, erro) ->
                    SwingUtilities.invokeLater(() -> TratadorErros.mostrar(null, erro)));

            TelaPrincipal tela = contexto.getBean(TelaPrincipal.class);
            tela.setVisible(true);
            carregamento.dispose();
        });
    }

    private static void naThreadDoSwing(Runnable acao) {
        try {
            SwingUtilities.invokeAndWait(acao);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(e.getCause());
        }
    }

    private static String explicarErro(Throwable erro) {
        for (Throwable causa = erro; causa != null; causa = causa.getCause()) {
            String mensagem = String.valueOf(causa.getMessage());
            if (mensagem.contains("Port") && mensagem.contains("already in use")) {
                return "A porta da API já está sendo usada. Provavelmente o sistema já está aberto em outra janela.";
            }
            if (mensagem.contains("already in use") || mensagem.contains("Dialect")) {
                return "Não foi possível abrir o banco de dados. Ele pode estar sendo usado por outra janela do sistema "
                        + "(o H2 em arquivo só aceita um programa por vez) ou o servidor do banco não está ligado.";
            }
            if (causa.getCause() == causa) {
                break;
            }
        }
        return "Verifique se o sistema já não está aberto e se a porta 8080 está livre.";
    }

    private static String causaRaiz(Throwable erro) {
        Throwable causa = erro;
        while (causa.getCause() != null && causa.getCause() != causa) {
            causa = causa.getCause();
        }
        return causa.getMessage() == null ? causa.getClass().getSimpleName() : causa.getMessage();
    }
}
