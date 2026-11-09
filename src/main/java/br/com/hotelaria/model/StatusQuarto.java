package br.com.hotelaria.model;

public enum StatusQuarto {
    DISPONIVEL("Disponível"),
    OCUPADO("Ocupado"),
    LIMPEZA("Em limpeza"),
    MANUTENCAO("Em manutenção");

    private final String descricao;

    StatusQuarto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
