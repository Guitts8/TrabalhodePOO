package br.com.hotelaria.model;

public enum Escala {
    CINCO_POR_DOIS("5x2", 5),
    SEIS_POR_UM("6x1", 6),
    DOZE_POR_TRINTA_E_SEIS("12x36", 3.5);

    private final String descricao;
    private final double diasPorSemana;

    Escala(String descricao, double diasPorSemana) {
        this.descricao = descricao;
        this.diasPorSemana = diasPorSemana;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getDiasPorSemana() {
        return diasPorSemana;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
