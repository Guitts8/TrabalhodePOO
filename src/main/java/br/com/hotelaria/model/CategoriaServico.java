package br.com.hotelaria.model;

public enum CategoriaServico {
    CAFE_DA_MANHA("Café da manhã"),
    SERVICO_DE_QUARTO("Serviço de quarto"),
    LAVANDERIA("Lavanderia"),
    ESTACIONAMENTO("Estacionamento"),
    LAZER("Lazer e bem-estar"),
    OUTROS("Outros");

    private final String descricao;

    CategoriaServico(String descricao) {
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
