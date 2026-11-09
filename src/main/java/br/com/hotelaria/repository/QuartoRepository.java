package br.com.hotelaria.repository;

import br.com.hotelaria.model.Quarto;
import br.com.hotelaria.model.StatusQuarto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {
    List<Quarto> findAllByOrderByHotelNomeAscNumeroAsc();

    List<Quarto> findByHotelIdOrderByNumeroAsc(Long hotelId);

    boolean existsByHotelIdAndNumeroIgnoreCaseAndIdNot(Long hotelId, String numero, Long id);

    long countByHotelId(Long hotelId);

    long countByStatus(StatusQuarto status);

    long countByHotelIdAndStatus(Long hotelId, StatusQuarto status);
}
