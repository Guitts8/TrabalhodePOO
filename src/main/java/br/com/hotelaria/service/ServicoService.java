package br.com.hotelaria.service;

import br.com.hotelaria.exception.RecursoNaoEncontradoException;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Servico;
import br.com.hotelaria.repository.ConsumoServicoRepository;
import br.com.hotelaria.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoService {
    private final ServicoRepository servicoRepository;
    private final ConsumoServicoRepository consumoRepository;

    public ServicoService(ServicoRepository servicoRepository, ConsumoServicoRepository consumoRepository) {
        this.servicoRepository = servicoRepository;
        this.consumoRepository = consumoRepository;
    }

    public List<Servico> listar() {
        return servicoRepository.findAllByOrderByNomeAsc();
    }

    public List<Servico> listarAtivos() {
        return servicoRepository.findByAtivoTrueOrderByNomeAsc();
    }

    public Servico buscarPorId(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe o código do serviço.");
        }
        return servicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado (código " + id + ")."));
    }

    @Transactional
    public Servico salvar(Servico servico) {
        Long id = servico.getId() == null ? 0L : servico.getId();
        if (servicoRepository.existsByNomeIgnoreCaseAndIdNot(servico.getNome(), id)) {
            throw new RegraNegocioException("Já existe um serviço com o nome " + servico.getNome() + ".");
        }
        return servicoRepository.save(servico);
    }

    @Transactional
    public void excluir(Long id) {
        Servico servico = buscarPorId(id);
        if (consumoRepository.existsByServicoId(id)) {
            throw new RegraNegocioException("O serviço " + servico.getNome() + " já foi usado em reservas e não pode ser excluído. "
                    + "Você pode inativá-lo para que não seja mais oferecido.");
        }
        servicoRepository.delete(servico);
    }

    @Transactional
    public Servico alterarSituacao(Long id, boolean ativo) {
        Servico servico = buscarPorId(id);
        servico.setAtivo(ativo);
        return servicoRepository.save(servico);
    }
}
