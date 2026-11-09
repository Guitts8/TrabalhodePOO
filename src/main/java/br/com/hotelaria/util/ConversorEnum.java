package br.com.hotelaria.util;

import br.com.hotelaria.exception.RegraNegocioException;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class ConversorEnum {
    private ConversorEnum() {
    }

    public static <E extends Enum<E>> E converter(Class<E> tipo, String valor, String campo) {
        if (Validador.isVazio(valor)) {
            throw new RegraNegocioException("Informe o campo " + campo + ".");
        }
        try {
            return Enum.valueOf(tipo, valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            String aceitos = Arrays.stream(tipo.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", "));
            throw new RegraNegocioException("Valor inválido para " + campo + ": " + valor + ". Use um destes: " + aceitos + ".");
        }
    }
}
