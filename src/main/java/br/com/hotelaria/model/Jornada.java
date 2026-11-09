package br.com.hotelaria.model;

import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.util.Formatos;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Duration;
import java.time.LocalTime;

@Embeddable
public class Jornada {
    private static final LocalTime INICIO_NOITE = LocalTime.of(22, 0);
    private static final LocalTime FIM_NOITE = LocalTime.of(5, 0);

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_saida")
    private LocalTime horaSaida;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Escala escala;

    public Jornada() {
    }

    public Jornada(LocalTime horaEntrada, LocalTime horaSaida, Escala escala) {
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
        this.escala = escala;
    }

    public double getHorasPorDia() {
        if (horaEntrada == null || horaSaida == null) {
            return 0;
        }
        long minutos = Duration.between(horaEntrada, horaSaida).toMinutes();
        if (minutos <= 0) {
            minutos += 24 * 60;
        }
        return minutos / 60.0;
    }

    public double getHorasSemanais() {
        if (escala == null) {
            return 0;
        }
        return getHorasPorDia() * escala.getDiasPorSemana();
    }

    public boolean isNoturna() {
        if (horaEntrada == null || horaSaida == null) {
            return false;
        }
        if (!horaSaida.isAfter(horaEntrada)) {
            return true;
        }
        return horaEntrada.isBefore(FIM_NOITE) || horaSaida.isAfter(INICIO_NOITE);
    }

    public void validar() {
        if (horaEntrada == null || horaSaida == null) {
            throw new RegraNegocioException("Informe o horário de entrada e de saída da jornada.");
        }
        if (escala == null) {
            throw new RegraNegocioException("Informe a escala de trabalho.");
        }
        double horas = getHorasPorDia();
        if (escala == Escala.DOZE_POR_TRINTA_E_SEIS) {
            if (horas != 12) {
                throw new RegraNegocioException("Na escala 12x36 a jornada precisa ter 12 horas (ex: 07:00 às 19:00).");
            }
        } else if (horas < 4 || horas > 10) {
            throw new RegraNegocioException("A jornada diária deve ter entre 4 e 10 horas, com o intervalo incluso.");
        }
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalTime getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(LocalTime horaSaida) {
        this.horaSaida = horaSaida;
    }

    public Escala getEscala() {
        return escala;
    }

    public void setEscala(Escala escala) {
        this.escala = escala;
    }

    @Override
    public String toString() {
        if (horaEntrada == null || horaSaida == null) {
            return "";
        }
        return Formatos.hora(horaEntrada) + " às " + Formatos.hora(horaSaida);
    }
}
