package br.com.hotelaria.view;

import br.com.hotelaria.controller.FuncionarioController;
import br.com.hotelaria.controller.HospedeController;
import br.com.hotelaria.controller.HotelController;
import br.com.hotelaria.controller.InicioController;
import br.com.hotelaria.controller.QuartoController;
import br.com.hotelaria.controller.ReservaController;
import br.com.hotelaria.controller.ServicoController;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.funcionario.PainelFuncionarios;
import br.com.hotelaria.view.hospede.PainelHospedes;
import br.com.hotelaria.view.hotel.PainelHoteis;
import br.com.hotelaria.view.inicio.PainelInicio;
import br.com.hotelaria.view.quarto.PainelQuartos;
import br.com.hotelaria.view.reserva.PainelReservas;
import br.com.hotelaria.view.servico.PainelServicos;
import com.formdev.flatlaf.FlatClientProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Lazy
public class TelaPrincipal extends JFrame {
    private final CardLayout cartoes = new CardLayout();
    private final JPanel conteudo = new JPanel(cartoes);
    private final JPanel menu = new JPanel();
    private final Map<String, Atualizavel> telas = new HashMap<>();
    private final Map<String, BotaoMenu> botoes = new LinkedHashMap<>();

    public TelaPrincipal(InicioController inicioController, HotelController hotelController,
                         QuartoController quartoController, ReservaController reservaController,
                         HospedeController hospedeController, ServicoController servicoController,
                         FuncionarioController funcionarioController,
                         @Value("${server.port:8080}") int porta,
                         @Value("${spring.datasource.url}") String urlBanco) {
        super("Rede Hoteleira - Sistema de Gerenciamento");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1320, 800);
        setMinimumSize(new Dimension(1120, 680));
        setLocationRelativeTo(null);

        montarMenu();
        conteudo.setBackground(Tema.FUNDO);
        add(menu, BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);
        add(montarBarraDeStatus(porta, urlBanco), BorderLayout.SOUTH);

        adicionarTela("inicio", "Início", new PainelInicio(inicioController));
        adicionarTela("hoteis", "Hotéis", new PainelHoteis(hotelController));
        adicionarTela("quartos", "Quartos", new PainelQuartos(quartoController));
        adicionarTela("reservas", "Reservas", new PainelReservas(reservaController));
        adicionarTela("hospedes", "Hóspedes", new PainelHospedes(hospedeController));
        adicionarTela("servicos", "Serviços", new PainelServicos(servicoController));
        adicionarTela("funcionarios", "Funcionários", new PainelFuncionarios(funcionarioController));

        menu.add(Box.createVerticalGlue());
        JLabel rodapeMenu = new JLabel("Hoje: " + Formatos.data(LocalDate.now()));
        rodapeMenu.setForeground(Tema.TEXTO_SECUNDARIO);
        rodapeMenu.setBorder(new EmptyBorder(0, 24, 18, 0));
        menu.add(rodapeMenu);

        mostrar("inicio");
    }

    public void mostrar(String nome) {
        cartoes.show(conteudo, nome);
        botoes.forEach((chave, botao) -> botao.setSelecionado(chave.equals(nome)));
        Atualizavel tela = telas.get(nome);
        if (tela != null) {
            tela.atualizar();
        }
    }

    private void adicionarTela(String nome, String textoMenu, JPanel tela) {
        conteudo.add(tela, nome);
        if (tela instanceof Atualizavel atualizavel) {
            telas.put(nome, atualizavel);
        }
        BotaoMenu botao = new BotaoMenu(textoMenu);
        botao.addActionListener(e -> mostrar(nome));
        botoes.put(nome, botao);
        menu.add(botao);
    }

    private void montarMenu() {
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Tema.MENU_FUNDO);
        menu.setPreferredSize(new Dimension(220, 0));

        JLabel nome = new JLabel("Rede Hoteleira");
        nome.setFont(Tema.fonte(Font.BOLD, 20f));
        nome.setForeground(Color.WHITE);
        nome.setBorder(new EmptyBorder(26, 24, 2, 0));
        JLabel descricao = new JLabel("Sistema de gerenciamento");
        descricao.setForeground(Tema.TEXTO_SECUNDARIO);
        descricao.setBorder(new EmptyBorder(0, 24, 26, 0));
        menu.add(nome);
        menu.add(descricao);
    }

    private JPanel montarBarraDeStatus(int porta, String urlBanco) {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);
        barra.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Tema.BORDA), new EmptyBorder(4, 16, 4, 12)));

        String base = "http://localhost:" + porta;
        JLabel api = new JLabel("API REST ativa em " + base + "/api");
        api.setForeground(Tema.VERDE);
        api.setFont(Tema.fonte(Font.BOLD, 12f));

        JPanel links = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        links.setOpaque(false);
        links.add(api);
        links.add(link("Abrir Swagger", base + "/swagger-ui.html"));
        links.add(link("Console do banco (H2)", base + "/h2-console"));
        barra.add(links, BorderLayout.WEST);

        JLabel banco = new JLabel("Banco: " + resumirUrl(urlBanco));
        banco.setToolTipText(urlBanco);
        banco.setForeground(Tema.TEXTO_SECUNDARIO);
        banco.setFont(Tema.fonte(Font.PLAIN, 12f));
        barra.add(banco, BorderLayout.EAST);
        return barra;
    }

    private static String resumirUrl(String url) {
        String semParametros = url.contains("?") ? url.substring(0, url.indexOf('?')) : url;
        if (semParametros.length() > 50) {
            return "..." + semParametros.substring(semParametros.length() - 47);
        }
        return semParametros;
    }

    private JButton link(String texto, String endereco) {
        JButton botao = new JButton(texto);
        botao.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        botao.setForeground(Tema.PRIMARIA);
        botao.setFont(Tema.fonte(Font.PLAIN, 12f));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.addActionListener(e -> TratadorErros.executar(this, () -> {
            try {
                Desktop.getDesktop().browse(new URI(endereco));
            } catch (Exception ex) {
                Mensagens.info(this, "Não foi possível abrir o navegador. Acesse: " + endereco);
            }
        }));
        return botao;
    }

    private static class BotaoMenu extends JButton {
        private boolean selecionado;
        private boolean mouseEmCima;

        BotaoMenu(String texto) {
            super(texto);
            setHorizontalAlignment(LEFT);
            setForeground(Tema.MENU_TEXTO);
            setFont(Tema.fonte(Font.PLAIN, 14.5f));
            setBorder(new EmptyBorder(11, 24, 11, 16));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    mouseEmCima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    mouseEmCima = false;
                    repaint();
                }
            });
        }

        void setSelecionado(boolean selecionado) {
            this.selecionado = selecionado;
            setForeground(selecionado ? Color.WHITE : Tema.MENU_TEXTO);
            setFont(Tema.fonte(selecionado ? Font.BOLD : Font.PLAIN, 14.5f));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (selecionado || mouseEmCima) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(selecionado ? Tema.MENU_SELECIONADO : new Color(255, 255, 255, 14));
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (selecionado) {
                    g2.setColor(Tema.PRIMARIA);
                    g2.fillRect(0, 0, 4, getHeight());
                }
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
