package br.com.hotelaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@DiscriminatorValue("RECEPCIONISTA")
public class Recepcionista extends Funcionario {
    private static final BigDecimal ADICIONAL_NOTURNO = new BigDecimal("0.20");

    @Column(length = 100)
    private String idiomas;

    public Recepcionista() {
    }

    @Override
    public Cargo getCargo() {
        return Cargo.RECEPCIONISTA;
    }

    @Override
    public BigDecimal calcularSalario() {
        if (getJornada() != null && getJornada().isNoturna()) {
            BigDecimal base = getSalarioBase();
            return base.add(base.multiply(ADICIONAL_NOTURNO)).setScale(2, RoundingMode.HALF_UP);
        }
        return super.calcularSalario();
    }

    @Override
    public String getComposicaoSalario() {
        if (getJornada() != null && getJornada().isNoturna()) {
            return "Salário base + 20% de adicional noturno";
        }
        return "Salário base (turno diurno)";
    }

    public String getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(String idiomas) {
        this.idiomas = idiomas == null ? null : idiomas.trim();
    }
}
