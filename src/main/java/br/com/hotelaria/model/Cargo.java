package br.com.hotelaria.model;

public enum Cargo {
    GERENTE("Gerente"),
    RECEPCIONISTA("Recepcionista"),
    ADMINISTRADOR("Administrador");

    private final String descricao;

    Cargo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public Funcionario novoFuncionario() {
        return switch (this) {
            case GERENTE -> new Gerente();
            case RECEPCIONISTA -> new Recepcionista();
            case ADMINISTRADOR -> new Administrador();
        };
    }

    @Override
    public String toString() {
        return descricao;
    }
}
