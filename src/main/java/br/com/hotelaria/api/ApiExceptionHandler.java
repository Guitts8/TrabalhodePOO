package br.com.hotelaria.api;

import br.com.hotelaria.api.dto.ErroResposta;
import br.com.hotelaria.exception.RecursoNaoEncontradoException;
import br.com.hotelaria.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "br.com.hotelaria.api")
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> naoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest request) {
        return montar(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> regraNegocio(RegraNegocioException ex, HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> jsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST,
                "Não foi possível ler o corpo da requisição. Confira o JSON e o formato das datas (aaaa-mm-dd).", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> parametroInvalido(MethodArgumentTypeMismatchException ex,
                                                          HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST,
                "Valor inválido para o parâmetro '" + ex.getName() + "': " + ex.getValue(), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResposta> parametroFaltando(MissingServletRequestParameterException ex,
                                                          HttpServletRequest request) {
        return montar(HttpStatus.BAD_REQUEST, "Parâmetro obrigatório não informado: " + ex.getParameterName(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> integridade(DataIntegrityViolationException ex, HttpServletRequest request) {
        return montar(HttpStatus.CONFLICT,
                "A operação não foi feita porque viola uma restrição do banco (registro duplicado ou em uso).", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> erroInesperado(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado na API", ex);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor. Tente novamente.", request);
    }

    private ResponseEntity<ErroResposta> montar(HttpStatus status, String mensagem, HttpServletRequest request) {
        ErroResposta erro = new ErroResposta(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                mensagem, request.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }
}
