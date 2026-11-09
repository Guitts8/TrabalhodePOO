package br.com.hotelaria.service;

import br.com.hotelaria.exception.RecursoNaoEncontradoException;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Cargo;
import br.com.hotelaria.model.Ferias;
import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.repository.FeriasRepository;
import br.com.hotelaria.repository.FuncionarioRepository;
import br.com.hotelaria.util.Formatos;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FuncionarioService {
    private static final int DIAS_FERIAS_POR_ANO = 30;
    private static final int MINIMO_DIAS_PERIODO = 5;

    private final FuncionarioRepository funcionarioRepository;
    private final FeriasRepository feriasRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, FeriasRepository feriasRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.feriasRepository = feriasRepository;
    }

    public List<Funcionario> listar() {
        return funcionarioRepository.findAllByOrderByNomeAsc();
    }

    public List<Funcionario> listarPorHotel(Long hotelId) {
        return funcionarioRepository.findByHotelIdOrderByNomeAsc(hotelId);
    }

    public Funcionario buscarPorId(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe o código do funcionário.");
        }
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário não encontrado (código " + id + ")."));
    }

    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        if (funcionario.getCpf() == null) {
            throw new RegraNegocioException("Informe o CPF do funcionário.");
        }
        if (funcionario.getHotel() == null) {
            throw new RegraNegocioException("Selecione o hotel em que o funcionário trabalha.");
        }
        if (funcionario.getSalarioBase() == null || funcionario.getDataContratacao() == null) {
            throw new RegraNegocioException("Informe o salário base e a data de contratação.");
        }
        if (funcionario.getJornada() == null) {
            throw new RegraNegocioException("Informe a jornada de trabalho.");
        }
        funcionario.getJornada().validar();

        Long id = funcionario.getId() == null ? 0L : funcionario.getId();
        if (funcionarioRepository.existsByCpfAndIdNot(funcionario.getCpf(), id)) {
            throw new RegraNegocioException("Já existe um funcionário cadastrado com o CPF " + funcionario.getCpfFormatado() + ".");
        }

        if (funcionario.getCargo() == Cargo.GERENTE
                && funcionarioRepository.contarGerentes(funcionario.getHotel().getId(), id) > 0) {
            throw new RegraNegocioException(funcionario.getHotel().getNome() + " já tem um gerente cadastrado.");
        }
        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public void excluir(Long id) {
        Funcionario funcionario = buscarPorId(id);
        feriasRepository.deleteByFuncionarioId(id);
        funcionarioRepository.delete(funcionario);
    }

    public BigDecimal calcularFolha(List<Funcionario> funcionarios) {
        BigDecimal total = BigDecimal.ZERO;
        for (Funcionario funcionario : funcionarios) {
            total = total.add(funcionario.calcularSalario());
        }
        return total;
    }

    public List<Ferias> listarFerias(Long funcionarioId) {
        buscarPorId(funcionarioId);
        return feriasRepository.findByFuncionarioIdOrderByDataInicioAsc(funcionarioId);
    }

    public Set<Long> buscarIdsEmFerias() {
        return new HashSet<>(feriasRepository.buscarFuncionariosDeFerias(LocalDate.now()));
    }

    public long diasDeFeriasNoAno(Long funcionarioId, int ano) {
        long dias = 0;
        for (Ferias ferias : feriasRepository.findByFuncionarioIdOrderByDataInicioAsc(funcionarioId)) {
            if (ferias.getDataInicio().getYear() == ano) {
                dias += ferias.getDias();
            }
        }
        return dias;
    }

    @Transactional
    public Ferias agendarFerias(Long funcionarioId, LocalDate inicio, LocalDate fim) {
        Funcionario funcionario = buscarPorId(funcionarioId);
        if (inicio == null || fim == null) {
            throw new RegraNegocioException("Informe a data de início e a data de fim das férias.");
        }
        if (fim.isBefore(inicio)) {
            throw new RegraNegocioException("A data de fim das férias não pode ser antes da data de início.");
        }
        Ferias nova = new Ferias(funcionario, inicio, fim);
        long dias = nova.getDias();
        if (dias < MINIMO_DIAS_PERIODO || dias > DIAS_FERIAS_POR_ANO) {
            throw new RegraNegocioException("Cada período de férias deve ter entre " + MINIMO_DIAS_PERIODO + " e "
                    + DIAS_FERIAS_POR_ANO + " dias. O período informado tem " + dias + " dias.");
        }
        if (inicio.isBefore(funcionario.getDataDireitoFerias())) {
            throw new RegraNegocioException(funcionario.getNome() + " só tem direito a férias a partir de "
                    + Formatos.data(funcionario.getDataDireitoFerias()) + " (12 meses depois da contratação).");
        }

        long diasNoAno = 0;
        for (Ferias outra : feriasRepository.findByFuncionarioIdOrderByDataInicioAsc(funcionarioId)) {
            if (outra.conflitaCom(inicio, fim)) {
                throw new RegraNegocioException("Já existem férias marcadas de " + Formatos.data(outra.getDataInicio())
                        + " a " + Formatos.data(outra.getDataFim()) + ".");
            }
            if (outra.getDataInicio().getYear() == inicio.getYear()) {
                diasNoAno += outra.getDias();
            }
        }
        if (diasNoAno + dias > DIAS_FERIAS_POR_ANO) {
            throw new RegraNegocioException(funcionario.getNome() + " já tem " + diasNoAno + " dias de férias em "
                    + inicio.getYear() + ". O limite é de " + DIAS_FERIAS_POR_ANO + " dias por ano.");
        }
        return feriasRepository.save(nova);
    }

    @Transactional
    public void cancelarFerias(Long funcionarioId, Long feriasId) {
        Ferias ferias = feriasRepository.findById(feriasId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Período de férias não encontrado (código " + feriasId + ")."));
        if (!ferias.getFuncionario().getId().equals(funcionarioId)) {
            throw new RegraNegocioException("Este período de férias não pertence ao funcionário informado.");
        }
        if (ferias.getDataFim().isBefore(LocalDate.now())) {
            throw new RegraNegocioException("Férias já concluídas ficam no histórico e não podem ser removidas.");
        }
        feriasRepository.delete(ferias);
    }
}
