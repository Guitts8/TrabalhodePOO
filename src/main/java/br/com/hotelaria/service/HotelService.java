package br.com.hotelaria.service;

import br.com.hotelaria.exception.RecursoNaoEncontradoException;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.repository.FuncionarioRepository;
import br.com.hotelaria.repository.HotelRepository;
import br.com.hotelaria.repository.QuartoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final QuartoRepository quartoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public HotelService(HotelRepository hotelRepository, QuartoRepository quartoRepository,
                        FuncionarioRepository funcionarioRepository) {
        this.hotelRepository = hotelRepository;
        this.quartoRepository = quartoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Hotel> listar() {
        return hotelRepository.findAllByOrderByNomeAsc();
    }

    public Hotel buscarPorId(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe o código do hotel.");
        }
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hotel não encontrado (código " + id + ")."));
    }

    @Transactional
    public Hotel salvar(Hotel hotel) {
        Long id = hotel.getId() == null ? 0L : hotel.getId();
        if (hotelRepository.existsByNomeIgnoreCaseAndIdNot(hotel.getNome(), id)) {
            throw new RegraNegocioException("Já existe um hotel cadastrado com o nome " + hotel.getNome() + ".");
        }
        return hotelRepository.save(hotel);
    }

    @Transactional
    public void excluir(Long id) {
        Hotel hotel = buscarPorId(id);
        if (quartoRepository.countByHotelId(id) > 0) {
            throw new RegraNegocioException(hotel.getNome() + " tem quartos cadastrados e não pode ser excluído. "
                    + "Exclua os quartos primeiro.");
        }
        if (funcionarioRepository.countByHotelId(id) > 0) {
            throw new RegraNegocioException(hotel.getNome() + " tem funcionários vinculados e não pode ser excluído.");
        }
        hotelRepository.delete(hotel);
    }

    public List<Quarto> listarQuartos(Long hotelId) {
        buscarPorId(hotelId);
        return quartoRepository.findByHotelIdOrderByNumeroAsc(hotelId);
    }

    public List<Funcionario> listarFuncionarios(Long hotelId) {
        buscarPorId(hotelId);
        return funcionarioRepository.findByHotelIdOrderByNomeAsc(hotelId);
    }

    public long contarQuartos(Long hotelId) {
        return quartoRepository.countByHotelId(hotelId);
    }

    public long contarFuncionarios(Long hotelId) {
        return funcionarioRepository.countByHotelId(hotelId);
    }
}
