package br.com.hotelaria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "consumo_servico")
public class ConsumoServico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id")
    private Servico servico;

    private int quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "data_consumo", nullable = false)
    private LocalDateTime dataConsumo;

    @Column(length = 100)
    private String observacao;

    protected ConsumoServico() {
    }

    public ConsumoServico(Reserva reserva, Servico servico, int quantidade, BigDecimal valorUnitario,
                          LocalDateTime dataConsumo, String observacao) {
        this.reserva = reserva;
        this.servico = servico;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.dataConsumo = dataConsumo;
        this.observacao = observacao;
    }

    public BigDecimal getValorTotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public Long getId() {
        return id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public Servico getServico() {
        return servico;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public LocalDateTime getDataConsumo() {
        return dataConsumo;
    }

    public String getObservacao() {
        return observacao;
    }
}
