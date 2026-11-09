package br.com.hotelaria.util;

import br.com.hotelaria.exception.RegraNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public final class Formatos {
    public static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATA_EXTENSO =
            DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' uuuu", BRASIL);

    private Formatos() {
    }

    public static String moeda(BigDecimal valor) {
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }
        return NumberFormat.getCurrencyInstance(BRASIL).format(valor);
    }

    public static String valor(BigDecimal valor) {
        if (valor == null) {
            return "";
        }
        NumberFormat nf = NumberFormat.getNumberInstance(BRASIL);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(valor);
    }

    public static String data(LocalDate data) {
        return data == null ? "" : DATA.format(data);
    }

    public static String dataHora(LocalDateTime dataHora) {
        return dataHora == null ? "" : DATA_HORA.format(dataHora);
    }

    public static String hora(LocalTime hora) {
        return hora == null ? "" : HORA.format(hora);
    }

    public static String dataPorExtenso(LocalDate data) {
        String texto = DATA_EXTENSO.format(data);
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    public static LocalDate lerData(String texto, String campo) {
        String limpo = limpar(texto);
        if (limpo.replace("/", "").isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(limpo, DATA);
        } catch (DateTimeParseException e) {
            throw new RegraNegocioException(campo + " inválida. Use o formato dd/mm/aaaa.");
        }
    }

    public static LocalTime lerHora(String texto, String campo) {
        String limpo = limpar(texto);
        if (limpo.replace(":", "").isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(limpo, HORA);
        } catch (DateTimeParseException e) {
            throw new RegraNegocioException(campo + " inválida. Use o formato hh:mm.");
        }
    }

    public static BigDecimal lerValor(String texto, String campo) {
        String limpo = limpar(texto).replace("R$", "").replace(" ", "");
        if (limpo.isEmpty()) {
            throw new RegraNegocioException("Informe o campo " + campo + ".");
        }

        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        }
        try {
            return new BigDecimal(limpo).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("Valor inválido no campo " + campo + ": " + texto.trim());
        }
    }

    private static String limpar(String texto) {
        return texto == null ? "" : texto.replace("_", "").replace(' ', ' ').trim();
    }
}
