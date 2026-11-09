package br.com.hotelaria.view.quarto;

import br.com.hotelaria.controller.QuartoController;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.QuartoLuxo;
import br.com.hotelaria.model.QuartoStandard;
import br.com.hotelaria.model.Suite;
import br.com.hotelaria.model.TipoQuarto;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.util.Validador;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.Tema;
import br.com.hotelaria.view.componentes.Componentes;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.math.BigDecimal;

public class QuartoDialog extends FormularioDialog {
    private final QuartoController controller;
    private final Quarto quarto;

    private final JComboBox<Hotel> hotel;
    private final JComboBox<TipoQuarto> tipo = new JComboBox<>(TipoQuarto.values());
    private final JTextField numero = Componentes.texto("Ex: 101");
    private final JSpinner andar = Componentes.numero(1, 0, 50);
    private final JSpinner capacidade = Componentes.numero(2, 1, 10);
    private final JTextField valorDiaria = Componentes.texto("0,00");
    private final JTextField descricao = Componentes.texto("Observação sobre o quarto (opcional)");

    private final CardLayout cartoesTipo = new CardLayout();
    private final JPanel painelTipo = new JPanel(cartoesTipo);
    private final JCheckBox camaCasal = new JCheckBox("Cama de casal (sem marcar = camas de solteiro)");
    private final JCheckBox vistaMar = new JCheckBox("Vista para o mar (+20% na diária)");
    private final JCheckBox hidromassagem = new JCheckBox("Banheira de hidromassagem (+10% na diária)");
    private final JTextField valorHospedeAdicional = Componentes.texto("0,00");
    private final JLabel previa = Componentes.destaque(" ", Tema.PRIMARIA);

    public QuartoDialog(Component pai, Quarto quarto, QuartoController controller) {
        super(pai, quarto == null ? "Novo quarto" : "Editar quarto " + quarto.getNumero(),
                "O tipo do quarto define as características e o cálculo da diária", 2);
        this.controller = controller;
        this.quarto = quarto;
        this.hotel = Componentes.combo(controller.listarHoteis());

        secao("Dados do quarto");
        campo("Hotel *", hotel, 2);
        campo("Tipo *", tipo);
        campo("Número *", numero);
        campo("Andar", andar);
        campo("Capacidade (pessoas) *", capacidade);
        campo("Valor da diária base (R$) *", valorDiaria);
        campo("Descrição", descricao);

        secao("Características do tipo");
        montarCartoesDoTipo();
        componente(painelTipo, 2);
        componente(previa, 2);

        tipo.addActionListener(e -> trocarTipo());
        vistaMar.addActionListener(e -> atualizarPrevia());
        hidromassagem.addActionListener(e -> atualizarPrevia());
        valorDiaria.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarPrevia();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarPrevia();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarPrevia();
            }
        });

        if (quarto != null) {
            preencherCampos();
            tipo.setEnabled(false);
        } else {
            hotel.setSelectedIndex(-1);
        }
        trocarTipo();
    }

    private void montarCartoesDoTipo() {
        JPanel standard = new JPanel(new GridLayout(0, 1, 0, 4));
        standard.add(camaCasal);
        standard.add(Componentes.subtitulo("Standard: a diária é o próprio valor base."));

        JPanel luxo = new JPanel(new GridLayout(0, 1, 0, 4));
        luxo.add(vistaMar);
        luxo.add(hidromassagem);

        JPanel suite = new JPanel(new BorderLayout(0, 4));
        JPanel linha = new JPanel(new GridLayout(1, 2, 12, 0));
        linha.add(Componentes.subtitulo("Valor por hóspede extra, por noite (R$):"));
        linha.add(valorHospedeAdicional);
        suite.add(linha, BorderLayout.NORTH);
        suite.add(Componentes.subtitulo("A diária cobre 2 pessoas e o café da manhã já está incluso."), BorderLayout.SOUTH);

        painelTipo.add(standard, TipoQuarto.STANDARD.name());
        painelTipo.add(luxo, TipoQuarto.LUXO.name());
        painelTipo.add(suite, TipoQuarto.SUITE.name());
    }

    private void trocarTipo() {
        TipoQuarto escolhido = (TipoQuarto) tipo.getSelectedItem();
        cartoesTipo.show(painelTipo, escolhido.name());
        atualizarPrevia();
    }

    private void atualizarPrevia() {
        try {
            Quarto exemplo = ((TipoQuarto) tipo.getSelectedItem()).novoQuarto();
            exemplo.setValorDiaria(Formatos.lerValor(valorDiaria.getText(), "valor da diária"));
            preencherCamposDoTipo(exemplo);
            previa.setText("Diária calculada para este quarto: " + Formatos.moeda(exemplo.calcularDiaria()));
        } catch (RegraNegocioException e) {
            previa.setText("Digite o valor da diária para ver o cálculo.");
        }
    }

    private void preencherCampos() {
        hotel.setSelectedItem(quarto.getHotel());
        tipo.setSelectedItem(quarto.getTipo());
        numero.setText(quarto.getNumero());
        andar.setValue(quarto.getAndar());
        capacidade.setValue(quarto.getCapacidade());
        valorDiaria.setText(Formatos.valor(quarto.getValorDiaria()));
        descricao.setText(quarto.getDescricao());

        if (quarto instanceof QuartoStandard standard) {
            camaCasal.setSelected(standard.isCamaCasal());
        } else if (quarto instanceof QuartoLuxo luxo) {
            vistaMar.setSelected(luxo.isVistaMar());
            hidromassagem.setSelected(luxo.isHidromassagem());
        } else if (quarto instanceof Suite suite) {
            valorHospedeAdicional.setText(Formatos.valor(suite.getValorHospedeAdicional()));
        }
    }

    private void preencherCamposDoTipo(Quarto destino) {
        if (destino instanceof QuartoStandard standard) {
            standard.setCamaCasal(camaCasal.isSelected());
        } else if (destino instanceof QuartoLuxo luxo) {
            luxo.setVistaMar(vistaMar.isSelected());
            luxo.setHidromassagem(hidromassagem.isSelected());
        } else if (destino instanceof Suite suite) {
            String texto = valorHospedeAdicional.getText();
            suite.setValorHospedeAdicional(Validador.isVazio(texto)
                    ? BigDecimal.ZERO : Formatos.lerValor(texto, "valor por hóspede extra"));
        }
    }

    @Override
    protected void salvar() {
        Quarto dados = quarto == null ? controller.novoQuarto((TipoQuarto) tipo.getSelectedItem()) : quarto;
        dados.setHotel((Hotel) hotel.getSelectedItem());
        dados.setNumero(numero.getText());
        dados.setAndar(Componentes.valor(andar));
        dados.setCapacidade(Componentes.valor(capacidade));
        dados.setValorDiaria(Formatos.lerValor(valorDiaria.getText(), "valor da diária"));
        dados.setDescricao(descricao.getText());
        preencherCamposDoTipo(dados);
        controller.salvar(dados);
    }
}
