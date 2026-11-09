package br.com.hotelaria.controller;

import br.com.hotelaria.model.Servico;
import br.com.hotelaria.service.ServicoService;
import br.com.hotelaria.util.Validador;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ServicoController {
    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    public List<Servico> pesquisar(String filtro) {
        List<Servico> servicos = servicoService.listar();
        if (Validador.isVazio(filtro)) {
            return servicos;
        }
        String texto = filtro.toLowerCase();
        List<Servico> encontrados = new ArrayList<>();
        for (Servico servico : servicos) {
            if (servico.getNome().toLowerCase().contains(texto)
                    || servico.getCategoria().getDescricao().toLowerCase().contains(texto)) {
                encontrados.add(servico);
            }
        }
        return encontrados;
    }

    public void salvar(Servico servico) {
        servicoService.salvar(servico);
    }

    public void excluir(Servico servico) {
        servicoService.excluir(servico.getId());
    }

    public void alternarSituacao(Servico servico) {
        servicoService.alterarSituacao(servico.getId(), !servico.isAtivo());
    }
}
