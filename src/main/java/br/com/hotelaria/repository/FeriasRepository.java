package br.com.hotelaria.repository;

import br.com.hotelaria.model.Ferias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FeriasRepository extends JpaRepository<Ferias, Long> {
    List<Ferias> findByFuncionarioIdOrderByDataInicioAsc(Long funcionarioId);

    @Query("select f.funcionario.id from Ferias f where f.dataInicio <= :data and f.dataFim >= :data")
    List<Long> buscarFuncionariosDeFerias(@Param("data") LocalDate data);

    void deleteByFuncionarioId(Long funcionarioId);
}
