package br.com.hotelaria.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record FuncionarioRequest(String cargo, Long hotelId, String nome, String cpf, LocalDate dataNascimento,
                                 String telefone, String email, BigDecimal salarioBase, LocalDate dataContratacao,
                                 LocalTime horaEntrada, LocalTime horaSaida, String escala,
                                 String idiomas, String setor) {
}
