package br.com.hotelaria.repository;

import br.com.hotelaria.model.ConsumoServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumoServicoRepository extends JpaRepository<ConsumoServico, Long> {
    boolean existsByServicoId(Long servicoId);
}
