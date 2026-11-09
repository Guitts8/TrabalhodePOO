package br.com.hotelaria.repository;

import br.com.hotelaria.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findAllByOrderByNomeAsc();

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
