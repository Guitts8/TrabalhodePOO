package br.com.hotelaria.view;

import br.com.hotelaria.exception.RecursoNaoEncontradoException;
import br.com.hotelaria.exception.RegraNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import java.awt.Component;

public final class TratadorErros {
    private static final Logger log = LoggerFactory.getLogger(TratadorErros.class);

    private TratadorErros() {
    }

    public static boolean executar(Component pai, Runnable acao) {
        try {
            acao.run();
            return true;
        } catch (Exception e) {
            mostrar(pai, e);
            return false;
        }
    }

    public static void mostrar(Component pai, Throwable erro) {
        if (erro instanceof RegraNegocioException || erro instanceof RecursoNaoEncontradoException) {
            Mensagens.aviso(pai, erro.getMessage());
        } else if (erro instanceof DataIntegrityViolationException) {
            Mensagens.aviso(pai, "Não foi possível salvar: já existe um registro com esses dados ou ele está sendo usado.");
        } else {
            log.error("Erro inesperado na interface", erro);
            String detalhe = erro.getMessage() == null ? erro.getClass().getSimpleName() : erro.getMessage();
            Mensagens.erro(pai, "Ocorreu um erro inesperado: " + detalhe);
        }
    }
}
