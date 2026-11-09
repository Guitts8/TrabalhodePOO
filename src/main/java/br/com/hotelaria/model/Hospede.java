package br.com.hotelaria.model;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.util.Validador;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "hospede")
public class Hospede extends Pessoa {
    @Column(length = 60)
    private String cidade;

    @Column(length = 2)
    private String uf;

    public Hospede() {
    }

    public Hospede(String nome, String cpf, LocalDate dataNascimento, String telefone, String email,
                   String cidade, String uf) {
        setNome(nome);
        setCpf(cpf);
        setDataNascimento(dataNascimento);
        setTelefone(telefone);
        setEmail(email);
        setCidade(cidade);
        setUf(uf);
    }

    @JsonIgnore
    public boolean isMaiorDeIdade() {
        return getIdade() >= 18;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade == null ? null : cidade.trim();
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        if (Validador.isVazio(uf)) {
            this.uf = null;
            return;
        }
        if (!uf.trim().matches("[A-Za-z]{2}")) {
            throw new RegraNegocioException("A UF deve ter duas letras (ex: SP).");
        }
        this.uf = uf.trim().toUpperCase();
    }

    @JsonIgnore
    public String getCidadeUf() {
        if (Validador.isVazio(cidade)) {
            return "";
        }
        return uf == null ? cidade : cidade + "/" + uf;
    }
}
