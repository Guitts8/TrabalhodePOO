package br.com.hotelaria.view.hotel;

import br.com.hotelaria.controller.HotelController;
import br.com.hotelaria.model.Endereco;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.componentes.Componentes;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import java.awt.Component;

public class HotelDialog extends FormularioDialog {
    static final String[] UFS = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA",
            "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

    private final HotelController controller;
    private final Hotel hotel;

    private final JTextField nome = Componentes.texto();
    private final JTextField telefone = Componentes.texto("(00) 0000-0000");
    private final JTextField email = Componentes.texto("contato@hotel.com.br");
    private final JSpinner estrelas = Componentes.numero(3, 1, 5);
    private final JTextField logradouro = Componentes.texto("Rua, avenida...");
    private final JTextField numero = Componentes.texto();
    private final JTextField bairro = Componentes.texto();
    private final JTextField cidade = Componentes.texto();
    private final JComboBox<String> uf = new JComboBox<>(UFS);
    private final JTextField cep = Componentes.texto("00000-000");

    public HotelDialog(Component pai, Hotel hotel, HotelController controller) {
        super(pai, hotel == null ? "Novo hotel" : "Editar hotel", "Campos com * são obrigatórios", 2);
        this.controller = controller;
        this.hotel = hotel;

        secao("Dados da unidade");
        campo("Nome do hotel *", nome, 2);
        campo("Telefone", telefone);
        campo("E-mail", email);
        campo("Categoria (estrelas) *", estrelas);

        secao("Endereço");
        campo("Logradouro", logradouro, 2);
        campo("Número", numero);
        campo("Bairro", bairro);
        campo("Cidade *", cidade);
        campo("UF *", uf);
        campo("CEP", cep);

        if (hotel != null) {
            preencherCampos();
        } else {
            uf.setSelectedIndex(-1);
        }
    }

    private void preencherCampos() {
        nome.setText(hotel.getNome());
        telefone.setText(hotel.getTelefone());
        email.setText(hotel.getEmail());
        estrelas.setValue(hotel.getEstrelas());
        Endereco endereco = hotel.getEndereco();
        if (endereco != null) {
            logradouro.setText(endereco.getLogradouro());
            numero.setText(endereco.getNumero());
            bairro.setText(endereco.getBairro());
            cidade.setText(endereco.getCidade());
            uf.setSelectedItem(endereco.getUf());
            cep.setText(endereco.getCep());
        }
    }

    @Override
    protected void salvar() {
        Hotel dados = hotel == null ? new Hotel() : hotel;

        dados.setNome(nome.getText());
        dados.setTelefone(telefone.getText());
        dados.setEmail(email.getText());
        dados.setEstrelas(Componentes.valor(estrelas));
        dados.setEndereco(new Endereco(logradouro.getText(), numero.getText(), bairro.getText(),
                cidade.getText(), (String) uf.getSelectedItem(), cep.getText()));
        controller.salvar(dados);
    }
}
