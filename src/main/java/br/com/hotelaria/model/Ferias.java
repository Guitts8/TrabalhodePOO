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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "ferias")
public class Ferias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    protected Ferias() {
    }

    public Ferias(Funcionario funcionario, LocalDate dataInicio, LocalDate dataFim) {
        this.funcionario = funcionario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public long getDias() {
        return ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
    }

    public boolean conflitaCom(LocalDate inicio, LocalDate fim) {
        return !dataInicio.isAfter(fim) && !dataFim.isBefore(inicio);
    }

    public boolean isEmAndamento() {
        LocalDate hoje = LocalDate.now();
        return !hoje.isBefore(dataInicio) && !hoje.isAfter(dataFim);
    }

    public String getSituacao() {
        LocalDate hoje = LocalDate.now();
        if (hoje.isBefore(dataInicio)) {
            return "Programada";
        }
        if (hoje.isAfter(dataFim)) {
            return "Concluída";
        }
        return "Em andamento";
    }

    public Long getId() {
        return id;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }
}
