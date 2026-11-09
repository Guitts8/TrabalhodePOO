package br.com.hotelaria.service;

import br.com.hotelaria.exception.RecursoNaoEncontradoException;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Hospede;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.repository.HospedeRepository;
import br.com.hotelaria.repository.ReservaRepository;
import br.com.hotelaria.util.Validador;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HospedeService {
    private final HospedeRepository hospedeRepository;
    private final ReservaRepository reservaRepository;

    public HospedeService(HospedeRepository hospedeRepository, ReservaRepository reservaRepository) {
        this.hospedeRepository = hospedeRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Hospede> listar() {
        return hospedeRepository.findAllByOrderByNomeAsc();
    }

    public List<Hospede> pesquisar(String texto) {
        if (Validador.isVazio(texto)) {
            return listar();
        }
        String termo = texto.trim();
        return hospedeRepository.pesquisar(termo, Validador.somenteNumeros(termo));
    }

    public Hospede buscarPorId(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe o código do hóspede.");
        }
        return hospedeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hóspede não encontrado (código " + id + ")."));
    }

    @Transactional
    public Hospede salvar(Hospede hospede) {
        if (hospede.getCpf() == null) {
            throw new RegraNegocioException("Informe o CPF do hóspede.");
        }
        if (hospede.getDataNascimento() == null) {
            throw new RegraNegocioException("Informe a data de nascimento do hóspede.");
        }
        Long id = hospede.getId() == null ? 0L : hospede.getId();
        if (hospedeRepository.existsByCpfAndIdNot(hospede.getCpf(), id)) {
            throw new RegraNegocioException("Já existe um hóspede cadastrado com o CPF " + hospede.getCpfFormatado() + ".");
        }
        return hospedeRepository.save(hospede);
    }

    @Transactional
    public void excluir(Long id) {
        Hospede hospede = buscarPorId(id);
        if (reservaRepository.existsByHospedeId(id)) {
            throw new RegraNegocioException(hospede.getNome() + " tem reservas no histórico e não pode ser excluído(a).");
        }
        hospedeRepository.delete(hospede);
    }

    public List<Reserva> historico(Long hospedeId) {
        buscarPorId(hospedeId);
        return reservaRepository.findByHospedeIdOrderByDataEntradaDesc(hospedeId);
    }
}
