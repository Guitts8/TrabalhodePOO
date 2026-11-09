package br.com.hotelaria.repository;

import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findAllByOrderByDataEntradaDesc();

    List<Reserva> findByStatusOrderByDataEntradaDesc(StatusReserva status);

    List<Reserva> findByHospedeIdOrderByDataEntradaDesc(Long hospedeId);

    List<Reserva> findByQuartoIdAndStatusIn(Long quartoId, List<StatusReserva> status);

    List<Reserva> findByDataEntradaAndStatusInOrderById(LocalDate data, List<StatusReserva> status);

    List<Reserva> findByDataSaidaAndStatusOrderById(LocalDate data, StatusReserva status);

    long countByHospedeIdAndStatus(Long hospedeId, StatusReserva status);

    long countByStatus(StatusReserva status);

    boolean existsByQuartoId(Long quartoId);

    boolean existsByHospedeId(Long hospedeId);

    @Query("select coalesce(sum(r.quantidadeHospedes), 0) from Reserva r where r.status = :status")
    long somarHospedes(@Param("status") StatusReserva status);
}
