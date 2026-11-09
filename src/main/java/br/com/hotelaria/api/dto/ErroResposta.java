package br.com.hotelaria.api.dto;

import java.time.LocalDateTime;

public record ErroResposta(LocalDateTime dataHora, int status, String erro, String mensagem, String caminho) {
}
