package br.com.hotelaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Funcionario {
    @Column(length = 60)
    private String setor;

    public Administrador() {
    }

    @Override
    public Cargo getCargo() {
        return Cargo.ADMINISTRADOR;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor == null ? null : setor.trim();
    }
}
