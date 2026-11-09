package br.com.hotelaria.api.dto;

import br.com.hotelaria.model.CategoriaServico;
import br.com.hotelaria.model.Servico;
import br.com.hotelaria.util.ConversorEnum;

import java.math.BigDecimal;

public record ServicoRequest(String nome, String descricao, BigDecimal preco, String categoria, Boolean ativo) {
    public void copiarPara(Servico servico) {
        servico.setNome(nome);
        servico.setDescricao(descricao);
        servico.setPreco(preco);
        servico.setCategoria(ConversorEnum.converter(CategoriaServico.class, categoria, "categoria"));
        servico.setAtivo(ativo == null || ativo);
    }
}
