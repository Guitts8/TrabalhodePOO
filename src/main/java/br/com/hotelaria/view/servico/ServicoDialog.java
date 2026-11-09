package br.com.hotelaria.view.servico;

import br.com.hotelaria.controller.ServicoController;
import br.com.hotelaria.model.CategoriaServico;
import br.com.hotelaria.model.Servico;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.componentes.Componentes;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Component;

public class ServicoDialog extends FormularioDialog {
    private final ServicoController controller;
    private final Servico servico;

    private final JTextField nome = Componentes.texto();
    private final JComboBox<CategoriaServico> categoria = new JComboBox<>(CategoriaServico.values());
    private final JTextField preco = Componentes.texto("0,00");
    private final JCheckBox ativo = new JCheckBox("Serviço ativo (pode ser lançado nas reservas)", true);
    private final JTextField descricao = Componentes.texto("Ex: valor por pessoa, por peça, por dia...");

    public ServicoDialog(Component pai, Servico servico, ServicoController controller) {
        super(pai, servico == null ? "Novo serviço" : "Editar serviço", "Campos com * são obrigatórios", 2);
        this.controller = controller;
        this.servico = servico;

        campo("Nome *", nome, 2);
        campo("Categoria *", categoria);
        campo("Preço (R$) *", preco);
        campo("Descrição", descricao, 2);
        componente(ativo, 2);
        componente(Componentes.subtitulo("Na suíte, o serviço da categoria Café da manhã é lançado com valor zero (já incluso)."), 2);

        if (servico != null) {
            nome.setText(servico.getNome());
            categoria.setSelectedItem(servico.getCategoria());
            preco.setText(Formatos.valor(servico.getPreco()));
            ativo.setSelected(servico.isAtivo());
            descricao.setText(servico.getDescricao());
        }
    }

    @Override
    protected void salvar() {
        Servico dados = servico == null ? new Servico() : servico;
        dados.setNome(nome.getText());
        dados.setCategoria((CategoriaServico) categoria.getSelectedItem());
        dados.setPreco(Formatos.lerValor(preco.getText(), "preço"));
        dados.setAtivo(ativo.isSelected());
        dados.setDescricao(descricao.getText());
        controller.salvar(dados);
    }
}
