package br.com.hotelaria.view.reserva;

import br.com.hotelaria.controller.ReservaController;
import br.com.hotelaria.exception.RecursoNaoEncontradoException;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.StatusQuarto;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.Tema;
import br.com.hotelaria.view.componentes.Componentes;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class ReservaDialog extends FormularioDialog {
    private final ReservaController controller;
    private final Reserva reserva;
    private Reserva reservaSalva;
    private Set<Long> quartosLivres;
    private boolean carregando;

    private final JComboBox<Hospede> hospede;
    private final JComboBox<Hotel> hotel;
    private final JComboBox<Quarto> quarto = new JComboBox<>();
    private final JFormattedTextField entrada = Componentes.data();
    private final JFormattedTextField saida = Componentes.data();
    private final JSpinner quantidade = Componentes.numero(1, 1, 10);
    private final JTextField observacoes = Componentes.texto("Ex: chegada prevista às 15h");

    private final JLabel textoDiarias = new JLabel("-");
    private final JLabel textoHospedagem = new JLabel("-");
    private final JLabel textoDesconto = new JLabel("-");
    private final JLabel textoTotal = new JLabel("-");
    private final JLabel aviso = new JLabel(" ");

    public ReservaDialog(Component pai, Reserva reserva, ReservaController controller) {
        super(pai, reserva == null ? "Nova reserva" : "Alterar reserva nº " + reserva.getId(),
                "Escolha o hóspede, o período e o quarto. O valor é calculado automaticamente.", 2);
        this.controller = controller;
        this.reserva = reserva;
        this.hospede = Componentes.combo(controller.listarHospedes());
        this.hotel = Componentes.combo(controller.listarHoteis());
        quarto.setRenderer(new RenderizadorQuarto());

        secao("Hóspede e período");
        campo("Hóspede responsável *", hospede, 2);
        campo("Data de entrada *", entrada);
        campo("Data de saída *", saida);
        campo("Quantidade de hóspedes *", quantidade);

        secao("Quarto");
        campo("Hotel *", hotel, 2);
        campo("Quarto *", quarto, 2);
        JButton verificar = Tema.botao("Verificar disponibilidade e calcular valor");
        verificar.addActionListener(e -> calcular());
        componente(verificar, 2);

        secao("Valores da hospedagem");
        componente(montarPainelValores(), 2);
        componente(aviso, 2);

        secao("Observações");
        componente(observacoes, 2);

        hotel.addActionListener(e -> {
            carregarQuartos();
            calcular();
        });
        quarto.addActionListener(e -> calcular());
        hospede.addActionListener(e -> calcular());
        quantidade.addChangeListener(e -> calcular());
        FocusAdapter aoSairDoCampo = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calcular();
            }
        };
        entrada.addFocusListener(aoSairDoCampo);
        saida.addFocusListener(aoSairDoCampo);

        carregando = true;
        if (reserva != null) {
            hospede.setSelectedItem(reserva.getHospede());
            entrada.setText(Formatos.data(reserva.getDataEntrada()));
            saida.setText(Formatos.data(reserva.getDataSaida()));
            quantidade.setValue(reserva.getQuantidadeHospedes());
            hotel.setSelectedItem(reserva.getQuarto().getHotel());
            observacoes.setText(reserva.getObservacoes());
        } else {
            hospede.setSelectedIndex(-1);
            hotel.setSelectedIndex(-1);
            entrada.setText(Formatos.data(LocalDate.now()));
            saida.setText(Formatos.data(LocalDate.now().plusDays(1)));
        }
        carregando = false;
        carregarQuartos();
        calcular();
    }

    public Reserva getReservaSalva() {
        return reservaSalva;
    }

    private JPanel montarPainelValores() {
        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 6));
        painel.setBackground(new Color(0xF8FAFC));
        painel.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Tema.BORDA), new EmptyBorder(10, 14, 10, 14)));
        painel.add(Componentes.subtitulo("Diárias"));
        painel.add(textoDiarias);
        painel.add(Componentes.subtitulo("Hospedagem"));
        painel.add(textoHospedagem);
        painel.add(Componentes.subtitulo("Descontos"));
        painel.add(textoDesconto);
        JLabel rotuloTotal = new JLabel("Total previsto (sem serviços)");
        rotuloTotal.setFont(Tema.fonte(Font.BOLD, 13f));
        painel.add(rotuloTotal);
        textoTotal.setFont(Tema.fonte(Font.BOLD, 15f));
        textoTotal.setForeground(Tema.PRIMARIA);
        painel.add(textoTotal);
        return painel;
    }

    private void carregarQuartos() {
        if (carregando) {
            return;
        }
        carregando = true;
        try {
            quarto.removeAllItems();
            for (Quarto item : controller.quartosDoHotel((Hotel) hotel.getSelectedItem())) {
                quarto.addItem(item);
            }
            if (reserva != null && reserva.getQuarto().getHotel().equals(hotel.getSelectedItem())) {
                quarto.setSelectedItem(reserva.getQuarto());
            } else {
                quarto.setSelectedIndex(-1);
            }
        } finally {
            carregando = false;
        }
    }

    private void calcular() {
        if (carregando) {
            return;
        }
        try {
            LocalDate dataEntrada = Formatos.lerData(entrada.getText(), "Data de entrada");
            LocalDate dataSaida = Formatos.lerData(saida.getText(), "Data de saída");
            int pessoas = Componentes.valor(quantidade);
            Hotel hotelEscolhido = (Hotel) hotel.getSelectedItem();

            quartosLivres = null;
            if (hotelEscolhido != null && dataEntrada != null && dataSaida != null) {
                quartosLivres = controller.quartosLivres(hotelEscolhido, dataEntrada, dataSaida, pessoas, reserva);
            }
            quarto.repaint();

            Hospede hospedeEscolhido = (Hospede) hospede.getSelectedItem();
            Quarto quartoEscolhido = (Quarto) quarto.getSelectedItem();
            if (hospedeEscolhido == null || quartoEscolhido == null) {
                limparValores();
                mostrarAviso("Selecione o hóspede, o hotel e o quarto para ver o valor.", Tema.TEXTO_SECUNDARIO);
                return;
            }
            Reserva simulada = controller.simular(hospedeEscolhido, quartoEscolhido, dataEntrada, dataSaida, pessoas, reserva);
            mostrarValores(simulada);
            mostrarAviso("Quarto livre no período. Pode salvar a reserva.", Tema.VERDE);
        } catch (RegraNegocioException | RecursoNaoEncontradoException e) {
            limparValores();
            mostrarAviso(e.getMessage(), Tema.VERMELHO);
        }
    }

    private void mostrarValores(Reserva simulada) {
        textoDiarias.setText(simulada.getQuantidadeDiarias() + " x " + Formatos.moeda(simulada.getValorDiaria()));
        String hospedagem = Formatos.moeda(simulada.getValorHospedagem());
        BigDecimal semAdicional = simulada.getValorDiaria().multiply(BigDecimal.valueOf(simulada.getQuantidadeDiarias()));
        if (simulada.getValorHospedagem().compareTo(semAdicional) > 0) {
            hospedagem += "  (inclui hóspedes extras da suíte)";
        }
        textoHospedagem.setText(hospedagem);
        if (simulada.getValorDesconto().signum() > 0) {
            textoDesconto.setText("- " + Formatos.moeda(simulada.getValorDesconto()) + "  " + simulada.getDescricaoDesconto());
            textoDesconto.setForeground(Tema.VERDE);
        } else {
            textoDesconto.setText("Nenhum");
            textoDesconto.setForeground(Tema.TEXTO);
        }
        textoTotal.setText(Formatos.moeda(simulada.getValorTotal()));
    }

    private void limparValores() {
        textoDiarias.setText("-");
        textoHospedagem.setText("-");
        textoDesconto.setText("-");
        textoDesconto.setForeground(Tema.TEXTO);
        textoTotal.setText("-");
    }

    private void mostrarAviso(String texto, Color cor) {
        aviso.setForeground(cor);
        aviso.setText("<html><body style='width: 480px'>" + texto + "</body></html>");
        if (isShowing()) {
            pack();
        }
    }

    @Override
    protected void salvar() {
        LocalDate dataEntrada = Formatos.lerData(entrada.getText(), "Data de entrada");
        LocalDate dataSaida = Formatos.lerData(saida.getText(), "Data de saída");
        reservaSalva = controller.salvar(reserva, (Hospede) hospede.getSelectedItem(), (Quarto) quarto.getSelectedItem(),
                dataEntrada, dataSaida, Componentes.valor(quantidade), observacoes.getText());
    }

    private class RenderizadorQuarto extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                      boolean selecionado, boolean foco) {
            super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
            if (valor instanceof Quarto item) {
                String texto = item.getNumero() + " - " + item.getTipo() + "  |  até " + item.getCapacidade()
                        + " pessoa(s)  |  " + Formatos.moeda(item.calcularDiaria()) + " por diária";
                String problema = problemaDoQuarto(item);
                if (problema != null) {
                    texto += "  -  " + problema;
                    if (!selecionado) {
                        setForeground(Tema.VERMELHO);
                    }
                } else if (quartosLivres != null) {
                    texto += "  -  livre no período";
                    if (!selecionado) {
                        setForeground(Tema.VERDE);
                    }
                }
                setText(texto);
            } else if (valor == null) {
                setText("Selecione o hotel e depois o quarto");
            }
            return this;
        }

        private String problemaDoQuarto(Quarto item) {
            if (item.getStatus() == StatusQuarto.MANUTENCAO) {
                return "em manutenção";
            }
            if (item.getCapacidade() < Componentes.valor(quantidade)) {
                return "não comporta " + Componentes.valor(quantidade) + " pessoas";
            }
            if (quartosLivres != null && !quartosLivres.contains(item.getId())) {
                return "ocupado no período";
            }
            return null;
        }
    }
}
