package br.com.hotelaria.config;

import br.com.hotelaria.model.Administrador;
import br.com.hotelaria.model.CategoriaServico;
import br.com.hotelaria.model.Endereco;
import br.com.hotelaria.model.Escala;
import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.model.Gerente;
import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Jornada;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.QuartoLuxo;
import br.com.hotelaria.model.QuartoStandard;
import br.com.hotelaria.model.Recepcionista;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.Servico;
import br.com.hotelaria.model.Suite;
import br.com.hotelaria.repository.HospedeRepository;
import br.com.hotelaria.repository.HotelRepository;
import br.com.hotelaria.repository.QuartoRepository;
import br.com.hotelaria.repository.ReservaRepository;
import br.com.hotelaria.repository.ServicoRepository;
import br.com.hotelaria.service.FuncionarioService;
import br.com.hotelaria.service.ReservaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DadosIniciais implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final HotelRepository hotelRepository;
    private final QuartoRepository quartoRepository;
    private final HospedeRepository hospedeRepository;
    private final ServicoRepository servicoRepository;
    private final ReservaRepository reservaRepository;
    private final FuncionarioService funcionarioService;
    private final ReservaService reservaService;

    private final LocalDate hoje = LocalDate.now();

    public DadosIniciais(HotelRepository hotelRepository, QuartoRepository quartoRepository,
                         HospedeRepository hospedeRepository, ServicoRepository servicoRepository,
                         ReservaRepository reservaRepository, FuncionarioService funcionarioService,
                         ReservaService reservaService) {
        this.hotelRepository = hotelRepository;
        this.quartoRepository = quartoRepository;
        this.hospedeRepository = hospedeRepository;
        this.servicoRepository = servicoRepository;
        this.reservaRepository = reservaRepository;
        this.funcionarioService = funcionarioService;
        this.reservaService = reservaService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (hotelRepository.count() > 0) {
            return;
        }
        log.info("Banco vazio: cadastrando dados de exemplo...");

        Hotel brisa = hotelRepository.save(new Hotel("Hotel Brisa do Mar", "(48) 3222-1000",
                "brisadomar@redehoteleira.com.br", 4,
                new Endereco("Av. Beira-Mar Norte", "3200", "Centro", "Florianópolis", "SC", "88015-700")));
        Hotel serra = hotelRepository.save(new Hotel("Hotel Serra Verde", "(54) 3286-2000",
                "serraverde@redehoteleira.com.br", 5,
                new Endereco("Rua das Hortênsias", "450", "Planalto", "Gramado", "RS", "95670-000")));
        Hotel centro = hotelRepository.save(new Hotel("Hotel Centro Executivo", "(11) 3251-3000",
                "centroexecutivo@redehoteleira.com.br", 3,
                new Endereco("Rua Augusta", "1800", "Consolação", "São Paulo", "SP", "01412-000")));

        Quarto brisa101 = standard(brisa, "101", 1, 2, "260.00", true);
        Quarto brisa102 = standard(brisa, "102", 1, 3, "280.00", false);
        Quarto brisa103 = standard(brisa, "103", 1, 2, "260.00", true);
        Quarto brisa201 = luxo(brisa, "201", 2, 2, "420.00", true, true);
        Quarto brisa202 = luxo(brisa, "202", 2, 3, "400.00", true, false);
        Quarto brisa301 = suite(brisa, "301", 3, 4, "720.00", "90.00");

        standard(serra, "101", 1, 2, "350.00", true);
        Quarto serra102 = standard(serra, "102", 1, 2, "350.00", false);
        Quarto serra201 = luxo(serra, "201", 2, 2, "560.00", false, true);
        Quarto serra202 = luxo(serra, "202", 2, 2, "560.00", false, true);
        Quarto serra301 = suite(serra, "301", 3, 4, "980.00", "120.00");

        Quarto centro101 = standard(centro, "101", 1, 1, "210.00", false);
        Quarto centro102 = standard(centro, "102", 1, 2, "230.00", true);
        standard(centro, "103", 1, 2, "230.00", false);
        Quarto centro104 = standard(centro, "104", 1, 2, "230.00", true);
        Quarto centro201 = luxo(centro, "201", 2, 2, "330.00", false, false);
        suite(centro, "301", 3, 3, "520.00", "70.00");

        centro104.colocarEmManutencao();
        centro104.setDescricao("Troca do ar-condicionado");

        Servico cafe = servico("Café da manhã", "Buffet de café da manhã (por pessoa)", "45.00", CategoriaServico.CAFE_DA_MANHA);
        Servico servicoQuarto = servico("Serviço de quarto", "Refeição servida no quarto", "68.00", CategoriaServico.SERVICO_DE_QUARTO);
        Servico frigobar = servico("Frigobar", "Item consumido do frigobar", "12.00", CategoriaServico.SERVICO_DE_QUARTO);
        Servico lavanderia = servico("Lavanderia", "Lavagem e passagem (por peça)", "18.00", CategoriaServico.LAVANDERIA);
        Servico estacionamento = servico("Estacionamento", "Diária de estacionamento coberto", "40.00", CategoriaServico.ESTACIONAMENTO);
        Servico massagem = servico("Massagem relaxante", "Sessão de 50 minutos no spa", "160.00", CategoriaServico.LAZER);
        servico("Transfer aeroporto", "Transporte entre o aeroporto e o hotel", "120.00", CategoriaServico.OUTROS);
        Servico barco = servico("Passeio de barco", "Passeio pela baía (só na temporada de verão)", "210.00", CategoriaServico.LAZER);
        barco.setAtivo(false);

        Hospede ana = hospede("Ana Beatriz Souza", "158.813.998-03", "1990-03-12", "(48) 99812-3344", "ana.souza@email.com", "Florianópolis", "SC");
        Hospede carlos = hospede("Carlos Eduardo Lima", "698.797.309-17", "1985-07-22", "(11) 98877-1200", "carlos.lima@email.com", "São Paulo", "SP");
        Hospede fernanda = hospede("Fernanda Oliveira", "114.170.753-50", "1993-11-05", "(51) 99654-7788", "fernanda.oliveira@email.com", "Porto Alegre", "RS");
        Hospede joao = hospede("João Pedro Santos", "645.568.151-77", "1988-01-30", "(21) 99123-4567", "joao.santos@email.com", "Rio de Janeiro", "RJ");
        Hospede mariana = hospede("Mariana Costa", "884.472.940-72", "1996-09-18", "(31) 98456-1122", "mariana.costa@email.com", "Belo Horizonte", "MG");
        Hospede rafael = hospede("Rafael Almeida", "557.616.987-57", "1979-04-02", "(41) 99777-8899", "rafael.almeida@email.com", "Curitiba", "PR");
        Hospede juliana = hospede("Juliana Ribeiro", "168.976.846-00", "1991-12-25", "(85) 98123-9090", "juliana.ribeiro@email.com", "Fortaleza", "CE");
        Hospede lucas = hospede("Lucas Martins", "978.809.320-54", "1983-06-14", "(61) 99234-5566", "lucas.martins@email.com", "Brasília", "DF");
        Hospede patricia = hospede("Patrícia Gomes", "819.672.731-38", "1975-08-09", "(71) 98765-4321", "patricia.gomes@email.com", "Salvador", "BA");
        Hospede bruno = hospede("Bruno Carvalho", "778.486.488-42", "2000-02-28", "(47) 99111-2233", "bruno.carvalho@email.com", "Joinville", "SC");

        hospede("Gabriel Souza", "766.344.002-10", "2012-05-10", "(48) 99812-3345", null, "Florianópolis", "SC");

        funcionarioService.salvar(funcionario(new Gerente(), brisa, "Roberto Nunes", "978.753.477-18", "1975-05-20",
                "7800.00", LocalDate.of(2018, 3, 1), "08:00", "17:00", Escala.CINCO_POR_DOIS));
        Recepcionista camila = funcionario(new Recepcionista(), brisa, "Camila Rocha", "876.804.571-90", "1994-02-11",
                "2600.00", LocalDate.of(2021, 6, 15), "07:00", "19:00", Escala.DOZE_POR_TRINTA_E_SEIS);
        camila.setIdiomas("Inglês, Espanhol");
        funcionarioService.salvar(camila);
        Recepcionista diego = funcionario(new Recepcionista(), brisa, "Diego Ferreira", "784.726.714-45", "1990-10-03",
                "2600.00", LocalDate.of(2022, 1, 10), "19:00", "07:00", Escala.DOZE_POR_TRINTA_E_SEIS);
        diego.setIdiomas("Inglês");
        funcionarioService.salvar(diego);
        Administrador sonia = funcionario(new Administrador(), brisa, "Sônia Mendes", "751.362.701-01", "1982-07-07",
                "4200.00", LocalDate.of(2019, 9, 2), "08:00", "17:00", Escala.CINCO_POR_DOIS);
        sonia.setSetor("Financeiro");
        funcionarioService.salvar(sonia);

        Funcionario helena = funcionarioService.salvar(funcionario(new Gerente(), serra, "Helena Castro", "079.819.331-02",
                "1980-12-01", "8500.00", LocalDate.of(2017, 4, 10), "09:00", "18:00", Escala.CINCO_POR_DOIS));
        Recepcionista thiago = funcionario(new Recepcionista(), serra, "Thiago Pereira", "908.452.059-94", "1997-03-19",
                "2700.00", LocalDate.of(2023, 2, 1), "14:00", "22:00", Escala.SEIS_POR_UM);
        thiago.setIdiomas("Inglês, Alemão");
        funcionarioService.salvar(thiago);
        Recepcionista larissa = funcionario(new Recepcionista(), serra, "Larissa Duarte", "972.837.929-37", "1995-08-27",
                "2700.00", LocalDate.of(2022, 8, 15), "22:00", "06:00", Escala.CINCO_POR_DOIS);
        larissa.setIdiomas("Espanhol");
        funcionarioService.salvar(larissa);
        Administrador marcos = funcionario(new Administrador(), serra, "Marcos Vieira", "841.622.948-10", "1987-11-11",
                "4000.00", LocalDate.of(2020, 5, 4), "08:00", "17:00", Escala.CINCO_POR_DOIS);
        marcos.setSetor("Recursos Humanos");
        funcionarioService.salvar(marcos);

        funcionarioService.salvar(funcionario(new Gerente(), centro, "Paulo Henrique Dias", "662.836.016-42", "1978-09-15",
                "6900.00", LocalDate.of(2019, 1, 7), "08:00", "17:00", Escala.CINCO_POR_DOIS));
        Recepcionista aline = funcionario(new Recepcionista(), centro, "Aline Barbosa", "105.500.875-67", "1999-04-04",
                "2500.00", hoje.minusMonths(4), "06:00", "14:00", Escala.SEIS_POR_UM);
        aline.setIdiomas("Inglês");
        funcionarioService.salvar(aline);
        Administrador fabio = funcionario(new Administrador(), centro, "Fábio Lopes", "475.177.992-31", "1985-01-21",
                "3900.00", LocalDate.of(2021, 3, 15), "08:00", "17:00", Escala.CINCO_POR_DOIS);
        fabio.setSetor("Compras");
        funcionarioService.salvar(fabio);

        funcionarioService.agendarFerias(camila.getId(), hoje.minusDays(5), hoje.plusDays(9));
        funcionarioService.agendarFerias(helena.getId(), hoje.plusDays(25), hoje.plusDays(39));

        Reserva r1 = hospedagemAnterior(ana, brisa101, hoje.minusDays(200), 4, 2);
        r1.adicionarConsumo(cafe, 8, momento(r1, 1, 8, 15));
        r1.adicionarConsumo(estacionamento, 4, momento(r1, 0, 15, 0));
        finalizar(r1);

        Reserva r2 = hospedagemAnterior(ana, serra201, hoje.minusDays(120), 3, 2);
        r2.adicionarConsumo(massagem, 1, momento(r2, 1, 16, 30));
        r2.adicionarConsumo(servicoQuarto, 2, momento(r2, 2, 20, 10));
        finalizar(r2);

        Reserva r3 = hospedagemAnterior(ana, brisa301, hoje.minusDays(60), 5, 3);
        r3.adicionarConsumo(cafe, 15, momento(r3, 1, 8, 0));
        r3.adicionarConsumo(lavanderia, 6, momento(r3, 3, 10, 0));
        finalizar(r3);

        Reserva r4 = hospedagemAnterior(carlos, centro102, hoje.minusDays(30), 2, 1);
        r4.adicionarConsumo(lavanderia, 3, momento(r4, 1, 9, 30));
        finalizar(r4);

        Reserva r5 = hospedagemAnterior(fernanda, serra301, hoje.minusDays(15), 7, 2);
        r5.adicionarConsumo(cafe, 14, momento(r5, 1, 8, 30));
        r5.adicionarConsumo(massagem, 2, momento(r5, 4, 17, 0));
        finalizar(r5);

        Reserva r6 = hospedagemAnterior(bruno, serra102, hoje.minusDays(2), 2, 1);
        r6.adicionarConsumo(frigobar, 2, momento(r6, 1, 22, 0));
        r6.fazerCheckOut(LocalDateTime.now().minusMinutes(30));
        reservaRepository.save(r6);

        Reserva r7 = hospedagemAnterior(joao, brisa201, hoje.minusDays(2), 5, 2);
        r7.adicionarConsumo(servicoQuarto, 1, momento(r7, 0, 21, 15));
        r7.adicionarConsumo(frigobar, 3, momento(r7, 1, 23, 0));
        reservaRepository.save(r7);

        Reserva r8 = hospedagemAnterior(mariana, centro101, hoje.minusDays(1), 1, 1);
        r8.adicionarConsumo(estacionamento, 1, momento(r8, 0, 18, 0));
        reservaRepository.save(r8);

        Reserva r9 = hospedagemAnterior(rafael, serra202, hoje.minusDays(3), 5, 2);
        r9.adicionarConsumo(cafe, 6, momento(r9, 1, 8, 10));
        r9.adicionarConsumo(lavanderia, 4, momento(r9, 2, 11, 0));
        reservaRepository.save(r9);

        Reserva hojeJuliana = reservaService.criar(juliana.getId(), brisa102.getId(), hoje, hoje.plusDays(4), 3,
                "Chegada prevista para as 15h");
        reservaService.confirmar(hojeJuliana.getId());

        reservaService.criar(ana.getId(), brisa202.getId(), hoje.plusDays(10), hoje.plusDays(14), 2, null);

        reservaService.criar(lucas.getId(), serra301.getId(), hoje.plusDays(20), hoje.plusDays(27), 4,
                "Família com duas crianças");

        Reserva patriciaReserva = reservaService.criar(patricia.getId(), centro201.getId(), hoje.plusDays(5),
                hoje.plusDays(7), 2, "Viagem a trabalho");
        reservaService.confirmar(patriciaReserva.getId());

        Reserva cancelada = reservaService.criar(bruno.getId(), brisa103.getId(), hoje.plusDays(3), hoje.plusDays(5), 1, null);
        reservaService.cancelar(cancelada.getId());

        log.info("Dados de exemplo cadastrados.");
    }

    private Quarto standard(Hotel hotel, String numero, int andar, int capacidade, String diaria, boolean camaCasal) {
        QuartoStandard quarto = new QuartoStandard();
        preencherQuarto(quarto, hotel, numero, andar, capacidade, diaria);
        quarto.setCamaCasal(camaCasal);
        return quartoRepository.save(quarto);
    }

    private Quarto luxo(Hotel hotel, String numero, int andar, int capacidade, String diaria,
                        boolean vistaMar, boolean hidromassagem) {
        QuartoLuxo quarto = new QuartoLuxo();
        preencherQuarto(quarto, hotel, numero, andar, capacidade, diaria);
        quarto.setVistaMar(vistaMar);
        quarto.setHidromassagem(hidromassagem);
        return quartoRepository.save(quarto);
    }

    private Quarto suite(Hotel hotel, String numero, int andar, int capacidade, String diaria, String adicional) {
        Suite quarto = new Suite();
        preencherQuarto(quarto, hotel, numero, andar, capacidade, diaria);
        quarto.setValorHospedeAdicional(new BigDecimal(adicional));
        return quartoRepository.save(quarto);
    }

    private void preencherQuarto(Quarto quarto, Hotel hotel, String numero, int andar, int capacidade, String diaria) {
        quarto.setHotel(hotel);
        quarto.setNumero(numero);
        quarto.setAndar(andar);
        quarto.setCapacidade(capacidade);
        quarto.setValorDiaria(new BigDecimal(diaria));
    }

    private Servico servico(String nome, String descricao, String preco, CategoriaServico categoria) {
        return servicoRepository.save(new Servico(nome, descricao, new BigDecimal(preco), categoria));
    }

    private Hospede hospede(String nome, String cpf, String nascimento, String telefone, String email,
                            String cidade, String uf) {
        return hospedeRepository.save(new Hospede(nome, cpf, LocalDate.parse(nascimento), telefone, email, cidade, uf));
    }

    private <T extends Funcionario> T funcionario(T funcionario, Hotel hotel, String nome, String cpf, String nascimento,
                                                  String salario, LocalDate contratacao, String entrada, String saida,
                                                  Escala escala) {
        funcionario.setNome(nome);
        funcionario.setCpf(cpf);
        funcionario.setDataNascimento(LocalDate.parse(nascimento));
        funcionario.setHotel(hotel);
        funcionario.setSalarioBase(new BigDecimal(salario));
        funcionario.setDataContratacao(contratacao);
        funcionario.setJornada(new Jornada(LocalTime.parse(entrada), LocalTime.parse(saida), escala));
        return funcionario;
    }

    private Reserva hospedagemAnterior(Hospede hospede, Quarto quarto, LocalDate entrada, int diarias, int pessoas) {
        Reserva reserva = new Reserva(hospede, quarto, entrada, entrada.plusDays(diarias), pessoas);
        reservaService.calcularValores(reserva);
        reserva.confirmar();
        reserva.fazerCheckIn(entrada.atTime(14, 20));
        return reserva;
    }

    private void finalizar(Reserva reserva) {
        reserva.fazerCheckOut(reserva.getDataSaida().atTime(11, 0));
        reserva.getQuarto().liberar();
        reservaRepository.save(reserva);
    }

    private LocalDateTime momento(Reserva reserva, int diaDaEstadia, int hora, int minuto) {
        return reserva.getDataEntrada().plusDays(diaDaEstadia).atTime(hora, minuto);
    }
}
