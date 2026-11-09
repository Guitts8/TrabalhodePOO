package br.com.hotelaria.model;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.util.Validador;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Entity
@Table(name = "quarto", uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "numero"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", length = 20)
public abstract class Quarto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String numero;

    private int andar;

    private int capacidade;

    @Column(name = "valor_diaria", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDiaria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusQuarto status = StatusQuarto.DISPONIVEL;

    @Column(length = 255)
    private String descricao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    public abstract TipoQuarto getTipo();

    public abstract String getCaracteristicas();

    @JsonProperty("diariaComAdicionais")
    public BigDecimal calcularDiaria() {
        return valorDiaria.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularValorHospedagem(long diarias, int hospedes) {
        return calcularDiaria().multiply(BigDecimal.valueOf(diarias)).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean incluiCafeDaManha() {
        return false;
    }

    public boolean comportaHospedes(int quantidade) {
        return quantidade >= 1 && quantidade <= capacidade;
    }

    public void ocupar() {
        if (status != StatusQuarto.DISPONIVEL) {
            throw new RegraNegocioException("O quarto " + numero + " não pode receber hóspedes agora, pois está "
                    + status.getDescricao().toLowerCase() + ".");
        }
        status = StatusQuarto.OCUPADO;
    }

    public void desocupar() {
        if (status == StatusQuarto.OCUPADO) {
            status = StatusQuarto.LIMPEZA;
        }
    }

    public void liberar() {
        if (status == StatusQuarto.OCUPADO) {
            throw new RegraNegocioException("O quarto " + numero + " está ocupado. Faça o check-out da hospedagem antes de liberar.");
        }
        status = StatusQuarto.DISPONIVEL;
    }

    public void colocarEmManutencao() {
        if (status == StatusQuarto.OCUPADO) {
            throw new RegraNegocioException("Não é possível colocar em manutenção um quarto ocupado.");
        }
        status = StatusQuarto.MANUTENCAO;
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        if (Validador.isVazio(numero)) {
            throw new RegraNegocioException("Informe o número do quarto.");
        }
        this.numero = numero.trim();
    }

    public int getAndar() {
        return andar;
    }

    public void setAndar(int andar) {
        if (andar < 0) {
            throw new RegraNegocioException("O andar não pode ser negativo.");
        }
        this.andar = andar;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        if (capacidade < 1 || capacidade > 10) {
            throw new RegraNegocioException("A capacidade do quarto deve ser de 1 a 10 pessoas.");
        }
        this.capacidade = capacidade;
    }

    public BigDecimal getValorDiaria() {
        return valorDiaria;
    }

    public void setValorDiaria(BigDecimal valorDiaria) {
        if (valorDiaria == null || valorDiaria.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor da diária deve ser maior que zero.");
        }
        this.valorDiaria = valorDiaria;
    }

    public StatusQuarto getStatus() {
        return status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        if (hotel == null) {
            throw new RegraNegocioException("Selecione o hotel do quarto.");
        }
        this.hotel = hotel;
    }

    @Override
    public String toString() {
        return numero + " - " + getTipo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quarto outro)) {
            return false;
        }
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
