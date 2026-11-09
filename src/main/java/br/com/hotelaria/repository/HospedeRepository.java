package br.com.hotelaria.repository;

import br.com.hotelaria.model.Hospede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HospedeRepository extends JpaRepository<Hospede, Long> {
    List<Hospede> findAllByOrderByNomeAsc();

    boolean existsByCpfAndIdNot(String cpf, Long id);

    @Query("""
            select h from Hospede h
            where lower(h.nome) like lower(concat('%', :texto, '%'))
               or lower(h.email) like lower(concat('%', :texto, '%'))
               or (:cpf <> '' and h.cpf like concat('%', :cpf, '%'))
            order by h.nome
            """)
    List<Hospede> pesquisar(@Param("texto") String texto, @Param("cpf") String cpf);
}
