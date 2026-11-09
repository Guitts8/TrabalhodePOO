package br.com.hotelaria.model;

import br.com.hotelaria.exception.RecursoNaoEncontradoException;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.util.Formatos;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "reserva")
public class Reserva {
    public static final int MAXIMO_DIARIAS = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hospede_id")
    private Hospede hospede;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quarto_id")
    private Quarto quarto;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;

    @Column(name = "data_saida", nullable = false)
    private LocalDate dataSaida;

    @Column(name = "quantidade_hospedes")
    private int quantidadeHospedes = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReserva status = StatusReserva.PENDENTE;

    @Column(name = "valor_diaria", precision = 10, scale = 2)
    private BigDecimal valorDiaria = BigDecimal.ZERO;

    @Column(name = "valor_hospedagem", precision = 10, scale = 2)
    private BigDecimal valorHospedagem = BigDecimal.ZERO;

    @Column(name = "valor_desconto", precision = 10, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "descricao_desconto", length = 200)
    private String descricaoDesconto;

    @Column(length = 255)
    private String observacoes;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_check_in")
    private LocalDateTime dataCheckIn;

    @Column(name = "data_check_out")
    private LocalDateTime dataCheckOut;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("dataConsumo")
    private List<ConsumoServico> consumos = new ArrayList<>();

    protected Reserva() {
    }

    public Reserva(Hospede hospede, Quarto quarto, LocalDate dataEntrada, LocalDate dataSaida, int quantidadeHospedes) {
        this.hospede = hospede;
        this.quarto = quarto;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.quantidadeHospedes = quantidadeHospedes;
    }

    public static void validarPeriodo(LocalDate entrada, LocalDate saida) {
        if (entrada == null || saida == null) {
            throw new RegraNegocioException("Informe a data de entrada e a data de saída.");
        }
        if (!saida.isAfter(entrada)) {
            throw new RegraNegocioException("A data de saída deve ser depois da data de entrada.");
        }
        if (ChronoUnit.DAYS.between(entrada, saida) > MAXIMO_DIARIAS) {
            throw new RegraNegocioException("Uma reserva pode ter no máximo " + MAXIMO_DIARIAS + " diárias.");
        }
    }

    public long getQuantidadeDiarias() {
        if (dataEntrada == null || dataSaida == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dataEntrada, dataSaida);
    }

    public boolean conflitaCom(LocalDate entrada, LocalDate saida) {
        return dataEntrada.isBefore(saida) && dataSaida.isAfter(entrada);
    }

    public void calcularValorHospedagem() {
        valorDiaria = quarto.calcularDiaria();
        valorHospedagem = quarto.calcularValorHospedagem(getQuantidadeDiarias(), quantidadeHospedes);
        valorDesconto = BigDecimal.ZERO;
        descricaoDesconto = null;
    }

    public void aplicarDesconto(BigDecimal valor, String descricao) {
        if (valor == null || valor.signum() <= 0) {
            return;
        }
        valorDesconto = valorDesconto.add(valor).min(valorHospedagem);
        descricaoDesconto = descricaoDesconto == null ? descricao : descricaoDesconto + "; " + descricao;
    }

    public BigDecimal getValorServicos() {
        BigDecimal total = BigDecimal.ZERO;
        for (ConsumoServico consumo : consumos) {
            total = total.add(consumo.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotal() {
        return valorHospedagem.subtract(valorDesconto).add(getValorServicos());
    }

    public void alterarDados(Hospede hospede, Quarto quarto, LocalDate entrada, LocalDate saida, int quantidadeHospedes) {
        if (!podeSerAlterada()) {
            throw new RegraNegocioException("Só é possível alterar reservas pendentes ou confirmadas. Esta reserva está "
                    + status.getDescricao().toLowerCase() + ".");
        }
        this.hospede = hospede;
        this.quarto = quarto;
        this.dataEntrada = entrada;
        this.dataSaida = saida;
        this.quantidadeHospedes = quantidadeHospedes;
    }

    public void confirmar() {
        if (status != StatusReserva.PENDENTE) {
            throw new RegraNegocioException("Só é possível confirmar reservas pendentes. Situação atual: " + status + ".");
        }
        status = StatusReserva.CONFIRMADA;
    }

    public void cancelar() {
        if (status != StatusReserva.PENDENTE && status != StatusReserva.CONFIRMADA) {
            throw new RegraNegocioException("Esta reserva não pode ser cancelada, pois está "
                    + status.getDescricao().toLowerCase() + ".");
        }
        status = StatusReserva.CANCELADA;
    }

    public void fazerCheckIn(LocalDateTime dataHora) {
        if (status != StatusReserva.CONFIRMADA) {
            throw new RegraNegocioException("O check-in só pode ser feito em reservas confirmadas. Situação atual: " + status + ".");
        }
        LocalDate dia = dataHora.toLocalDate();
        if (dia.isBefore(dataEntrada)) {
            throw new RegraNegocioException("O check-in desta reserva só pode ser feito a partir de "
                    + Formatos.data(dataEntrada) + ".");
        }
        if (!dia.isBefore(dataSaida)) {
            throw new RegraNegocioException("O período desta reserva já terminou. Altere as datas ou cancele a reserva.");
        }
        quarto.ocupar();
        status = StatusReserva.EM_ANDAMENTO;
        dataCheckIn = dataHora;
    }

    public void fazerCheckOut(LocalDateTime dataHora) {
        if (status != StatusReserva.EM_ANDAMENTO) {
            throw new RegraNegocioException("Só é possível fazer check-out de hospedagens em andamento. Situação atual: "
                    + status + ".");
        }
        quarto.desocupar();
        status = StatusReserva.FINALIZADA;
        dataCheckOut = dataHora;
    }

    public boolean podeSerAlterada() {
        return status == StatusReserva.PENDENTE || status == StatusReserva.CONFIRMADA;
    }

    public boolean isAtiva() {
        return status.isAtiva();
    }

    public ConsumoServico adicionarConsumo(Servico servico, int quantidade, LocalDateTime dataHora) {
        if (status == StatusReserva.FINALIZADA || status == StatusReserva.CANCELADA) {
            throw new RegraNegocioException("Não é possível lançar serviços em uma reserva "
                    + status.getDescricao().toLowerCase() + ".");
        }
        if (servico == null) {
            throw new RegraNegocioException("Selecione o serviço.");
        }
        if (!servico.isAtivo()) {
            throw new RegraNegocioException("O serviço " + servico.getNome() + " está inativo e não pode ser lançado.");
        }
        if (quantidade < 1) {
            throw new RegraNegocioException("A quantidade deve ser pelo menos 1.");
        }

        BigDecimal valor = servico.getPreco();
        String observacao = null;

        if (servico.getCategoria() == CategoriaServico.CAFE_DA_MANHA && quarto.incluiCafeDaManha()) {
            valor = BigDecimal.ZERO;
            observacao = "Incluso na diária da suíte";
        }
        ConsumoServico consumo = new ConsumoServico(this, servico, quantidade, valor, dataHora, observacao);
        consumos.add(consumo);
        return consumo;
    }

    public void removerConsumo(Long consumoId) {
        if (status == StatusReserva.FINALIZADA || status == StatusReserva.CANCELADA) {
            throw new RegraNegocioException("Não é possível alterar os serviços de uma reserva "
                    + status.getDescricao().toLowerCase() + ".");
        }
        boolean removeu = consumos.removeIf(c -> c.getId() != null && c.getId().equals(consumoId));
        if (!removeu) {
            throw new RecursoNaoEncontradoException("Serviço lançado não encontrado nesta reserva.");
        }
    }

    public Long getId() {
        return id;
    }

    public Hospede getHospede() {
        return hospede;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    public int getQuantidadeHospedes() {
        return quantidadeHospedes;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public BigDecimal getValorDiaria() {
        return valorDiaria;
    }

    public BigDecimal getValorHospedagem() {
        return valorHospedagem;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public String getDescricaoDesconto() {
        return descricaoDesconto;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataCheckIn() {
        return dataCheckIn;
    }

    public LocalDateTime getDataCheckOut() {
        return dataCheckOut;
    }

    public List<ConsumoServico> getConsumos() {
        return Collections.unmodifiableList(consumos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reserva outra)) {
            return false;
        }
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
