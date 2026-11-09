package br.com.hotelaria.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@DiscriminatorValue("GERENTE")
public class Gerente extends Funcionario {
    private static final BigDecimal GRATIFICACAO = new BigDecimal("0.30");

    public Gerente() {
    }

    @Override
    public Cargo getCargo() {
        return Cargo.GERENTE;
    }

    @Override
    public BigDecimal calcularSalario() {
        BigDecimal base = getSalarioBase();
        return base.add(base.multiply(GRATIFICACAO)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getComposicaoSalario() {
        return "Salário base + 30% de gratificação de função";
    }
}
