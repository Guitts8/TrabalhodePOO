package br.com.hotelaria.model;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.util.Validador;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "servico")
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaServico categoria;

    private boolean ativo = true;

    public Servico() {
    }

    public Servico(String nome, String descricao, BigDecimal preco, CategoriaServico categoria) {
        setNome(nome);
        setDescricao(descricao);
        setPreco(preco);
        setCategoria(categoria);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (Validador.isVazio(nome)) {
            throw new RegraNegocioException("Informe o nome do serviço.");
        }
        this.nome = nome.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O preço do serviço deve ser maior que zero.");
        }
        this.preco = preco;
    }

    public CategoriaServico getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaServico categoria) {
        if (categoria == null) {
            throw new RegraNegocioException("Selecione a categoria do serviço.");
        }
        this.categoria = categoria;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return nome + " - " + Formatos.moeda(preco);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Servico outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
