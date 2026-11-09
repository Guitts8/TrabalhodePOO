package br.com.hotelaria.model;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.util.Validador;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(length = 20)
    private String telefone;

    @Column(length = 100)
    private String email;

    private int estrelas;

    @Embedded
    private Endereco endereco = new Endereco();

    public Hotel() {
    }

    public Hotel(String nome, String telefone, String email, int estrelas, Endereco endereco) {
        setNome(nome);
        setTelefone(telefone);
        setEmail(email);
        setEstrelas(estrelas);
        setEndereco(endereco);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (Validador.isVazio(nome)) {
            throw new RegraNegocioException("Informe o nome do hotel.");
        }
        this.nome = nome.trim();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone == null ? null : telefone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (Validador.isVazio(email)) {
            this.email = null;
            return;
        }
        if (!Validador.isEmailValido(email)) {
            throw new RegraNegocioException("E-mail do hotel inválido: " + email);
        }
        this.email = email.trim().toLowerCase();
    }

    public int getEstrelas() {
        return estrelas;
    }

    public void setEstrelas(int estrelas) {
        if (estrelas < 1 || estrelas > 5) {
            throw new RegraNegocioException("A categoria do hotel deve ser de 1 a 5 estrelas.");
        }
        this.estrelas = estrelas;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new RegraNegocioException("Informe o endereço do hotel.");
        }
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hotel outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
