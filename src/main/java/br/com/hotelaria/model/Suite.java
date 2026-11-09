package br.com.hotelaria.model;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.util.Formatos;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@DiscriminatorValue("SUITE")
public class Suite extends Quarto {
    private static final int HOSPEDES_INCLUSOS = 2;

    @Column(name = "valor_hospede_adicional", precision = 10, scale = 2)
    private BigDecimal valorHospedeAdicional = BigDecimal.ZERO;

    public Suite() {
    }

    @Override
    public TipoQuarto getTipo() {
        return TipoQuarto.SUITE;
    }

    @Override
    public BigDecimal calcularValorHospedagem(long diarias, int hospedes) {
        BigDecimal total = super.calcularValorHospedagem(diarias, hospedes);
        int extras = hospedes - HOSPEDES_INCLUSOS;
        if (extras > 0) {
            BigDecimal adicional = valorHospedeAdicional.multiply(BigDecimal.valueOf(extras * diarias));
            total = total.add(adicional);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean incluiCafeDaManha() {
        return true;
    }

    @Override
    public String getCaracteristicas() {
        return "Café da manhã incluso, " + Formatos.moeda(valorHospedeAdicional) + " por hóspede extra/noite";
    }

    public BigDecimal getValorHospedeAdicional() {
        return valorHospedeAdicional;
    }

    public void setValorHospedeAdicional(BigDecimal valorHospedeAdicional) {
        if (valorHospedeAdicional == null || valorHospedeAdicional.signum() < 0) {
            throw new RegraNegocioException("O valor por hóspede adicional não pode ser negativo.");
        }
        this.valorHospedeAdicional = valorHospedeAdicional;
    }
}
