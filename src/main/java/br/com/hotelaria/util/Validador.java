package br.com.hotelaria.util;

import java.util.regex.Pattern;

public final class Validador {
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validador() {
    }

    public static boolean isVazio(String texto) {
        return texto == null || texto.isBlank();
    }

    public static String somenteNumeros(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replaceAll("\\D", "");
    }

    public static boolean isEmailValido(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    public static boolean isCpfValido(String cpf) {
        String numeros = somenteNumeros(cpf);
        if (numeros.length() != 11) {
            return false;
        }

        if (numeros.chars().distinct().count() == 1) {
            return false;
        }
        int primeiro = calcularDigito(numeros, 9);
        int segundo = calcularDigito(numeros, 10);
        return primeiro == numeros.charAt(9) - '0' && segundo == numeros.charAt(10) - '0';
    }

    private static int calcularDigito(String numeros, int quantidade) {
        int soma = 0;
        int peso = quantidade + 1;
        for (int i = 0; i < quantidade; i++) {
            soma += (numeros.charAt(i) - '0') * peso;
            peso--;
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    public static String formatarCpf(String cpf) {
        String n = somenteNumeros(cpf);
        if (n.length() != 11) {
            return cpf;
        }
        return n.substring(0, 3) + "." + n.substring(3, 6) + "." + n.substring(6, 9) + "-" + n.substring(9);
    }
}
