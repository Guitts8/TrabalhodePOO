package br.com.hotelaria.view.servico;

import br.com.hotelaria.controller.ServicoController;
import br.com.hotelaria.model.Servico;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.PainelCadastro;
import br.com.hotelaria.view.componentes.Componentes;

import java.util.List;

public class PainelServicos extends PainelCadastro<Servico> {
    private final ServicoController controller;

    public PainelServicos(ServicoController controller) {
        super("Serviços", "Serviços oferecidos aos hóspedes e lançados na conta da hospedagem",
                "Pesquisar por nome ou categoria");
        this.controller = controller;

        modelo.coluna("Nome", Servico::getNome)
                .coluna("Categoria", Servico::getCategoria)
                .coluna("Preço", s -> Formatos.moeda(s.getPreco()))
                .coluna("Situação", s -> s.isAtivo() ? "Ativo" : "Inativo")
                .coluna("Descrição", Servico::getDescricao);
        configurarColunas(200, 160, 100, 100, 380);
        Componentes.alinharDireita(tabela, 2);
        Componentes.coresDeSituacao(tabela, 3);

        adicionarAcao("Ativar / inativar", () -> comSelecionado(servico -> {
            controller.alternarSituacao(servico);
            atualizar();
        }));
    }

    @Override
    protected List<Servico> buscarDados(String filtro) {
        return controller.pesquisar(filtro);
    }

    @Override
    protected void novo() {
        new ServicoDialog(this, null, controller).abrir();
        atualizar();
    }

    @Override
    protected void editar(Servico servico) {
        new ServicoDialog(this, servico, controller).abrir();
        atualizar();
    }

    @Override
    protected void excluir(Servico servico) {
        controller.excluir(servico);
    }

    @Override
    protected String descrever(Servico servico) {
        return "o serviço " + servico.getNome();
    }
}
