package br.com.hotelaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("LUXO")
public class QuartoLuxo extends Quarto {
    private static final BigDecimal ADICIONAL_VISTA_MAR = new BigDecimal("0.20");
    private static final BigDecimal ADICIONAL_HIDROMASSAGEM = new BigDecimal("0.10");

    @Column(name = "vista_mar")
    private boolean vistaMar;

    private boolean hidromassagem;

    public QuartoLuxo() {
    }

    @Override
    public TipoQuarto getTipo() {
        return TipoQuarto.LUXO;
    }

    @Override
    public BigDecimal calcularDiaria() {
        BigDecimal base = getValorDiaria();
        BigDecimal diaria = base;
        if (vistaMar) {
            diaria = diaria.add(base.multiply(ADICIONAL_VISTA_MAR));
        }
        if (hidromassagem) {
            diaria = diaria.add(base.multiply(ADICIONAL_HIDROMASSAGEM));
        }
        return diaria.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getCaracteristicas() {
        List<String> itens = new ArrayList<>();
        if (vistaMar) {
            itens.add("Vista para o mar (+20%)");
        }
        if (hidromassagem) {
            itens.add("Hidromassagem (+10%)");
        }
        if (itens.isEmpty()) {
            return "Luxo sem adicionais";
        }
        return String.join(", ", itens);
    }

    public boolean isVistaMar() {
        return vistaMar;
    }

    public void setVistaMar(boolean vistaMar) {
        this.vistaMar = vistaMar;
    }

    public boolean isHidromassagem() {
        return hidromassagem;
    }

    public void setHidromassagem(boolean hidromassagem) {
        this.hidromassagem = hidromassagem;
    }
}
