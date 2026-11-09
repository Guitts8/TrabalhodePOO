package br.com.hotelaria.repository;

import br.com.hotelaria.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    List<Funcionario> findAllByOrderByNomeAsc();

    List<Funcionario> findByHotelIdOrderByNomeAsc(Long hotelId);

    long countByHotelId(Long hotelId);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    @Query("select count(g) from Gerente g where g.hotel.id = :hotelId and g.id <> :ignorarId")
    long contarGerentes(@Param("hotelId") Long hotelId, @Param("ignorarId") Long ignorarId);
}
