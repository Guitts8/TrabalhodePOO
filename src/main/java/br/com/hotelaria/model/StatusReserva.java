package br.com.hotelaria.model;

import java.util.List;

public enum StatusReserva {
    PENDENTE("Pendente"),
    CONFIRMADA("Confirmada"),
    EM_ANDAMENTO("Em andamento"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

    public static final List<StatusReserva> ATIVAS = List.of(PENDENTE, CONFIRMADA, EM_ANDAMENTO);

    private final String descricao;

    StatusReserva(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtiva() {
        return ATIVAS.contains(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
