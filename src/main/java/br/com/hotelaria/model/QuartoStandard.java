package br.com.hotelaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STANDARD")
public class QuartoStandard extends Quarto {
    @Column(name = "cama_casal")
    private boolean camaCasal;

    public QuartoStandard() {
    }

    @Override
    public TipoQuarto getTipo() {
        return TipoQuarto.STANDARD;
    }

    @Override
    public String getCaracteristicas() {
        return camaCasal ? "Cama de casal" : "Camas de solteiro";
    }

    public boolean isCamaCasal() {
        return camaCasal;
    }

    public void setCamaCasal(boolean camaCasal) {
        this.camaCasal = camaCasal;
    }
}
