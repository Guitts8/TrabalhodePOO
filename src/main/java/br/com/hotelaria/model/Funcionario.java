package br.com.hotelaria.model;

import br.com.hotelaria.exception.RegraNegocioException;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "funcionario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "cargo", length = 20)
public abstract class Funcionario extends Pessoa {
    @Column(name = "salario_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioBase;

    @Column(name = "data_contratacao", nullable = false)
    private LocalDate dataContratacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Embedded
    private Jornada jornada = new Jornada();

    public abstract Cargo getCargo();

    @JsonProperty("salarioCalculado")
    public BigDecimal calcularSalario() {
        return salarioBase;
    }

    public String getComposicaoSalario() {
        return "Salário base";
    }

    public long getMesesDeEmpresa() {
        if (dataContratacao == null) {
            return 0;
        }
        return ChronoUnit.MONTHS.between(dataContratacao, LocalDate.now());
    }

    public LocalDate getDataDireitoFerias() {
        if (dataContratacao == null) {
            return null;
        }
        return dataContratacao.plusMonths(12);
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        if (salarioBase == null || salarioBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O salário base deve ser maior que zero.");
        }
        this.salarioBase = salarioBase;
    }

    public LocalDate getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(LocalDate dataContratacao) {
        if (dataContratacao == null) {
            throw new RegraNegocioException("Informe a data de contratação.");
        }
        if (dataContratacao.isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data de contratação não pode ser no futuro.");
        }
        this.dataContratacao = dataContratacao;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        if (hotel == null) {
            throw new RegraNegocioException("Selecione o hotel em que o funcionário trabalha.");
        }
        this.hotel = hotel;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        if (jornada == null) {
            throw new RegraNegocioException("Informe a jornada de trabalho.");
        }
        jornada.validar();
        this.jornada = jornada;
    }
}
