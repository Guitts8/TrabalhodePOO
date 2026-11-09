package br.com.hotelaria.model;

public enum TipoQuarto {
    STANDARD("Standard"),
    LUXO("Luxo"),
    SUITE("Suíte");

    private final String descricao;

    TipoQuarto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public Quarto novoQuarto() {
        return switch (this) {
            case STANDARD -> new QuartoStandard();
            case LUXO -> new QuartoLuxo();
            case SUITE -> new Suite();
        };
    }

    @Override
    public String toString() {
        return descricao;
    }
}
