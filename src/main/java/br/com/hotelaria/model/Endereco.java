package br.com.hotelaria.model;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.util.Validador;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Endereco {
    @Column(length = 120)
    private String logradouro;

    @Column(length = 10)
    private String numero;

    @Column(length = 60)
    private String bairro;

    @Column(length = 60)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 9)
    private String cep;

    public Endereco() {
    }

    public Endereco(String logradouro, String numero, String bairro, String cidade, String uf, String cep) {
        setLogradouro(logradouro);
        setNumero(numero);
        setBairro(bairro);
        setCidade(cidade);
        setUf(uf);
        setCep(cep);
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro == null ? null : logradouro.trim();
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero == null ? null : numero.trim();
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro == null ? null : bairro.trim();
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        if (Validador.isVazio(cidade)) {
            throw new RegraNegocioException("Informe a cidade do endereço.");
        }
        this.cidade = cidade.trim();
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        if (uf == null || !uf.trim().matches("[A-Za-z]{2}")) {
            throw new RegraNegocioException("Informe a UF com duas letras (ex: SC).");
        }
        this.uf = uf.trim().toUpperCase();
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep == null ? null : cep.trim();
    }

    @JsonIgnore
    public String getCidadeUf() {
        if (cidade == null) {
            return "";
        }
        return uf == null ? cidade : cidade + "/" + uf;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!Validador.isVazio(logradouro)) {
            sb.append(logradouro);
            if (!Validador.isVazio(numero)) {
                sb.append(", ").append(numero);
            }
            if (!Validador.isVazio(bairro)) {
                sb.append(" - ").append(bairro);
            }
            sb.append(", ");
        }
        sb.append(getCidadeUf());
        if (!Validador.isVazio(cep)) {
            sb.append(" - CEP ").append(cep);
        }
        return sb.toString();
    }
}
