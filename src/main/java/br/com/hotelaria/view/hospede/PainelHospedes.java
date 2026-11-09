package br.com.hotelaria.view.hospede;

import br.com.hotelaria.controller.HospedeController;
import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.PainelCadastro;
import br.com.hotelaria.view.componentes.Componentes;

import java.util.List;

public class PainelHospedes extends PainelCadastro<Hospede> {
    private final HospedeController controller;

    public PainelHospedes(HospedeController controller) {
        super("Hóspedes", "Cadastro dos hóspedes da rede e histórico de hospedagens", "Pesquisar por nome, CPF ou e-mail");
        this.controller = controller;

        modelo.coluna("Nome", Hospede::getNome)
                .coluna("CPF", Hospede::getCpfFormatado)
                .coluna("Nascimento", h -> Formatos.data(h.getDataNascimento()))
                .coluna("Idade", h -> h.getIdade() + " anos")
                .coluna("Telefone", Hospede::getTelefone)
                .coluna("E-mail", Hospede::getEmail)
                .coluna("Cidade/UF", Hospede::getCidadeUf);
        configurarColunas(210, 120, 95, 70, 125, 230, 150);
        Componentes.centralizar(tabela, 2, 3);

        adicionarAcao("Histórico de hospedagens", () -> comSelecionado(this::abrirHistorico));
    }

    @Override
    protected List<Hospede> buscarDados(String filtro) {
        return controller.pesquisar(filtro);
    }

    @Override
    protected void novo() {
        new HospedeDialog(this, null, controller).abrir();
        atualizar();
    }

    @Override
    protected void editar(Hospede hospede) {
        new HospedeDialog(this, hospede, controller).abrir();
        atualizar();
    }

    @Override
    protected void excluir(Hospede hospede) {
        controller.excluir(hospede);
    }

    @Override
    protected String descrever(Hospede hospede) {
        return "o hóspede " + hospede.getNome();
    }

    private void abrirHistorico(Hospede hospede) {
        new HistoricoHospedeDialog(this, hospede, controller).setVisible(true);
    }
}
