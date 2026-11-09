package br.com.hotelaria.model;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.util.Validador;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@MappedSuperclass
public abstract class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(length = 20)
    private String telefone;

    @Column(length = 100)
    private String email;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (Validador.isVazio(nome) || nome.trim().length() < 3) {
            throw new RegraNegocioException("Informe o nome completo (mínimo de 3 letras).");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (!Validador.isCpfValido(cpf)) {
            throw new RegraNegocioException("CPF inválido: " + (cpf == null ? "" : cpf.trim()));
        }
        this.cpf = Validador.somenteNumeros(cpf);
    }

    public String getCpfFormatado() {
        return Validador.formatarCpf(cpf);
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento != null && dataNascimento.isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data de nascimento não pode ser no futuro.");
        }
        this.dataNascimento = dataNascimento;
    }

    public int getIdade() {
        if (dataNascimento == null) {
            return 0;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
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
            throw new RegraNegocioException("E-mail inválido: " + email.trim());
        }
        this.email = email.trim().toLowerCase();
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pessoa outra = (Pessoa) o;
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
