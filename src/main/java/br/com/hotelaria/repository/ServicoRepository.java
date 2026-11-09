package br.com.hotelaria.repository;

import br.com.hotelaria.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findAllByOrderByNomeAsc();

    List<Servico> findByAtivoTrueOrderByNomeAsc();

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
