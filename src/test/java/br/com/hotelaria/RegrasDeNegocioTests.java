package br.com.hotelaria;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Escala;
import br.com.hotelaria.model.Gerente;
import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.model.Jornada;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.QuartoLuxo;
import br.com.hotelaria.model.Recepcionista;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.Servico;
import br.com.hotelaria.model.StatusQuarto;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.model.Suite;
import br.com.hotelaria.repository.HospedeRepository;
import br.com.hotelaria.repository.QuartoRepository;
import br.com.hotelaria.repository.ServicoRepository;
import br.com.hotelaria.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class RegrasDeNegocioTests {
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private QuartoRepository quartoRepository;
    @Autowired
    private HospedeRepository hospedeRepository;
    @Autowired
    private ServicoRepository servicoRepository;

    private final LocalDate hoje = LocalDate.now();

    @Test
    void naoDeixaReservarPeriodoQueCruzaComOutraReserva() {
        Quarto quarto = quarto("Hotel Brisa do Mar", "201");
        Hospede carlos = hospede("Carlos Eduardo Lima");
        assertThrows(RegraNegocioException.class, () ->
                reservaService.criar(carlos.getId(), quarto.getId(), hoje.plusDays(1), hoje.plusDays(2), 1, null));
    }

    @Test
    void permiteEntrarNoMesmoDiaEmQueOutroHospedeSai() {
        Quarto quarto = quarto("Hotel Brisa do Mar", "201");
        Hospede carlos = hospede("Carlos Eduardo Lima");
        Reserva nova = reservaService.criar(carlos.getId(), quarto.getId(), hoje.plusDays(3), hoje.plusDays(5), 2, null);
        assertEquals(StatusReserva.PENDENTE, nova.getStatus());
    }

    @Test
    void luxoSomaOsAdicionaisNaDiaria() {
        QuartoLuxo luxo = new QuartoLuxo();
        luxo.setValorDiaria(new BigDecimal("400.00"));
        luxo.setVistaMar(true);
        luxo.setHidromassagem(true);
        assertEquals(new BigDecimal("520.00"), luxo.calcularDiaria());
    }

    @Test
    void suiteCobraHospedeExtraAcimaDeDois() {
        Suite suite = new Suite();
        suite.setValorDiaria(new BigDecimal("500.00"));
        suite.setValorHospedeAdicional(new BigDecimal("100.00"));

        assertEquals(new BigDecimal("2100.00"), suite.calcularValorHospedagem(3, 4));
    }

    @Test
    void cicloCompletoDaReservaMudaASituacaoDoQuarto() {
        Quarto quarto = quarto("Hotel Brisa do Mar", "101");
        Hospede carlos = hospede("Carlos Eduardo Lima");
        Reserva reserva = reservaService.criar(carlos.getId(), quarto.getId(), hoje, hoje.plusDays(2), 1, null);

        reservaService.confirmar(reserva.getId());
        reserva = reservaService.fazerCheckIn(reserva.getId());
        assertEquals(StatusReserva.EM_ANDAMENTO, reserva.getStatus());
        assertEquals(StatusQuarto.OCUPADO, reserva.getQuarto().getStatus());

        reserva = reservaService.fazerCheckOut(reserva.getId());
        assertEquals(StatusReserva.FINALIZADA, reserva.getStatus());
        assertEquals(StatusQuarto.LIMPEZA, reserva.getQuarto().getStatus());
    }

    @Test
    void checkInSoEmReservaConfirmada() {
        Quarto quarto = quarto("Hotel Brisa do Mar", "103");
        Hospede carlos = hospede("Carlos Eduardo Lima");
        Reserva pendente = reservaService.criar(carlos.getId(), quarto.getId(), hoje, hoje.plusDays(1), 1, null);
        assertThrows(RegraNegocioException.class, () -> reservaService.fazerCheckIn(pendente.getId()));
    }

    @Test
    void servicoConsumidoEntraNoValorFinal() {
        Quarto quarto = quarto("Hotel Brisa do Mar", "103");
        Hospede carlos = hospede("Carlos Eduardo Lima");
        Reserva reserva = reservaService.criar(carlos.getId(), quarto.getId(), hoje, hoje.plusDays(2), 1, null);
        BigDecimal antes = reserva.getValorTotal();

        Servico lavanderia = servicoRepository.findAllByOrderByNomeAsc().stream()
                .filter(s -> s.getNome().equals("Lavanderia")).findFirst().orElseThrow();
        reserva = reservaService.adicionarServico(reserva.getId(), lavanderia.getId(), 3);

        assertEquals(antes.add(new BigDecimal("54.00")), reserva.getValorTotal());
    }

    @Test
    void menorDeIdadeNaoPodeSerResponsavel() {
        Quarto quarto = quarto("Hotel Brisa do Mar", "103");
        Hospede gabriel = hospede("Gabriel Souza");
        assertThrows(RegraNegocioException.class, () ->
                reservaService.criar(gabriel.getId(), quarto.getId(), hoje.plusDays(1), hoje.plusDays(2), 1, null));
    }

    @Test
    void salarioMudaConformeOCargo() {
        Gerente gerente = new Gerente();
        gerente.setSalarioBase(new BigDecimal("5000.00"));
        assertEquals(new BigDecimal("6500.00"), gerente.calcularSalario());

        Recepcionista noturna = new Recepcionista();
        noturna.setSalarioBase(new BigDecimal("2500.00"));
        noturna.setJornada(new Jornada(LocalTime.of(22, 0), LocalTime.of(6, 0), Escala.CINCO_POR_DOIS));
        assertEquals(new BigDecimal("3000.00"), noturna.calcularSalario());
    }

    @Test
    void cpfInvalidoNaoEAceito() {
        Hospede hospede = new Hospede();
        assertThrows(RegraNegocioException.class, () -> hospede.setCpf("111.111.111-11"));
        assertThrows(RegraNegocioException.class, () -> hospede.setCpf("123.456.789-00"));
    }

    private Quarto quarto(String hotel, String numero) {
        return quartoRepository.findAllByOrderByHotelNomeAscNumeroAsc().stream()
                .filter(q -> q.getHotel().getNome().equals(hotel) && q.getNumero().equals(numero))
                .findFirst().orElseThrow();
    }

    private Hospede hospede(String nome) {
        return hospedeRepository.findAllByOrderByNomeAsc().stream()
                .filter(h -> h.getNome().equals(nome))
                .findFirst().orElseThrow();
    }
}
