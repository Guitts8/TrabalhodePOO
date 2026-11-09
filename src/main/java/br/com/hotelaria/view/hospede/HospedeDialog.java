package br.com.hotelaria.view.hospede;

import br.com.hotelaria.controller.HospedeController;
import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.componentes.Componentes;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import java.awt.Component;

public class HospedeDialog extends FormularioDialog {
    private final HospedeController controller;
    private final Hospede hospede;

    private final JTextField nome = Componentes.texto();
    private final JFormattedTextField cpf = Componentes.cpf();
    private final JFormattedTextField nascimento = Componentes.data();
    private final JTextField telefone = Componentes.texto("(00) 00000-0000");
    private final JTextField email = Componentes.texto("nome@email.com");
    private final JTextField cidade = Componentes.texto();
    private final JTextField uf = Componentes.texto("SP");

    public HospedeDialog(Component pai, Hospede hospede, HospedeController controller) {
        super(pai, hospede == null ? "Novo hóspede" : "Editar hóspede", "Campos com * são obrigatórios", 2);
        this.controller = controller;
        this.hospede = hospede;

        secao("Dados pessoais");
        campo("Nome completo *", nome, 2);
        campo("CPF *", cpf);
        campo("Data de nascimento *", nascimento);
        secao("Contato");
        campo("Telefone", telefone);
        campo("E-mail", email);
        campo("Cidade", cidade);
        campo("UF", uf);

        if (hospede != null) {
            nome.setText(hospede.getNome());
            cpf.setText(hospede.getCpfFormatado());
            nascimento.setText(Formatos.data(hospede.getDataNascimento()));
            telefone.setText(hospede.getTelefone());
            email.setText(hospede.getEmail());
            cidade.setText(hospede.getCidade());
            uf.setText(hospede.getUf());
        }
    }

    @Override
    protected void salvar() {
        Hospede dados = hospede == null ? new Hospede() : hospede;
        dados.setNome(nome.getText());
        dados.setCpf(cpf.getText());
        dados.setDataNascimento(Formatos.lerData(nascimento.getText(), "Data de nascimento"));
        dados.setTelefone(telefone.getText());
        dados.setEmail(email.getText());
        dados.setCidade(cidade.getText());
        dados.setUf(uf.getText());
        controller.salvar(dados);
    }
}
