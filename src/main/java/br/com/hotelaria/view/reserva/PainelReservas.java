package br.com.hotelaria.view.reserva;

import br.com.hotelaria.controller.ReservaController;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.model.StatusReserva;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.Mensagens;
import br.com.hotelaria.view.PainelCadastro;
import br.com.hotelaria.view.componentes.Componentes;

import javax.swing.JComboBox;
import java.util.List;

public class PainelReservas extends PainelCadastro<Reserva> {
    private final ReservaController controller;
    private final JComboBox<Object> filtroSituacao = new JComboBox<>();

    public PainelReservas(ReservaController controller) {
        super("Reservas", "Reservas dos hóspedes, check-in, check-out e serviços consumidos",
                "Hóspede, nº, hotel ou quarto");
        this.controller = controller;

        modelo.coluna("Nº", Reserva::getId)
                .coluna("Hóspede", r -> r.getHospede().getNome())
                .coluna("Hotel", r -> r.getQuarto().getHotel().getNome())
                .coluna("Quarto", r -> r.getQuarto().getNumero() + " - " + r.getQuarto().getTipo())
                .coluna("Entrada", r -> Formatos.data(r.getDataEntrada()))
                .coluna("Saída", r -> Formatos.data(r.getDataSaida()))
                .coluna("Diárias", Reserva::getQuantidadeDiarias)
                .coluna("Pessoas", Reserva::getQuantidadeHospedes)
                .coluna("Situação", Reserva::getStatus)
                .coluna("Total", r -> Formatos.moeda(r.getValorTotal()));
        configurarColunas(45, 180, 165, 105, 85, 85, 55, 60, 120, 105);
        Componentes.centralizar(tabela, 0, 4, 5, 6, 7);
        Componentes.alinharDireita(tabela, 9);
        Componentes.coresDeSituacao(tabela, 8);

        filtroSituacao.addItem("Todas");
        for (StatusReserva situacao : StatusReserva.values()) {
            filtroSituacao.addItem(situacao);
        }
        filtroSituacao.addActionListener(e -> atualizar());
        adicionarFiltro("Situação:", filtroSituacao);

        adicionarAcao("Confirmar", () -> comSelecionado(this::confirmar));
        adicionarAcao("Check-in", () -> comSelecionado(this::fazerCheckIn));
        adicionarAcao("Check-out", () -> comSelecionado(this::fazerCheckOut));
        adicionarAcao("Cancelar reserva", () -> comSelecionado(this::cancelar));
        adicionarAcao("Serviços e conta", () -> comSelecionado(this::abrirConta));
    }

    @Override
    protected List<Reserva> buscarDados(String filtro) {
        StatusReserva situacao = filtroSituacao.getSelectedItem() instanceof StatusReserva s ? s : null;
        return controller.listar(situacao, filtro);
    }

    @Override
    protected void novo() {
        ReservaDialog dialogo = new ReservaDialog(this, null, controller);
        if (dialogo.abrir()) {
            Reserva salva = dialogo.getReservaSalva();
            atualizar();
            Mensagens.info(this, "Reserva nº " + salva.getId() + " criada como pendente. Valor previsto: "
                    + Formatos.moeda(salva.getValorTotal()) + ".");
        }
    }

    @Override
    protected void editar(Reserva reserva) {
        if (!reserva.podeSerAlterada()) {
            abrirConta(reserva);
            return;
        }
        new ReservaDialog(this, reserva, controller).abrir();
        atualizar();
    }

    @Override
    protected void excluir(Reserva reserva) {
        controller.excluir(reserva);
    }

    @Override
    protected String descrever(Reserva reserva) {
        return "a reserva nº " + reserva.getId() + " de " + reserva.getHospede().getNome();
    }

    private void confirmar(Reserva reserva) {
        controller.confirmar(reserva);
        atualizar();
        Mensagens.info(this, "Reserva nº " + reserva.getId() + " confirmada. O check-in pode ser feito a partir de "
                + Formatos.data(reserva.getDataEntrada()) + ".");
    }

    private void fazerCheckIn(Reserva reserva) {
        String pergunta = "Fazer o check-in de " + reserva.getHospede().getNome() + " no quarto "
                + reserva.getQuarto().getNumero() + " do " + reserva.getQuarto().getHotel().getNome() + "?";
        if (!Mensagens.confirmar(this, pergunta)) {
            return;
        }
        controller.fazerCheckIn(reserva);
        atualizar();
        Mensagens.info(this, "Check-in feito! A hospedagem está em andamento e o quarto "
                + reserva.getQuarto().getNumero() + " agora está ocupado.");
    }

    private void fazerCheckOut(Reserva reserva) {
        if (!Mensagens.confirmar(this, "Fazer o check-out de " + reserva.getHospede().getNome()
                + "? A conta da hospedagem será fechada.")) {
            return;
        }
        Reserva finalizada = controller.fazerCheckOut(reserva);
        atualizar();
        new ContaReservaDialog(this, finalizada, controller).setVisible(true);
    }

    private void cancelar(Reserva reserva) {
        if (Mensagens.confirmar(this, "Cancelar a reserva nº " + reserva.getId() + " de "
                + reserva.getHospede().getNome() + "? O quarto fica livre para outras reservas.")) {
            controller.cancelar(reserva);
            atualizar();
        }
    }

    private void abrirConta(Reserva reserva) {
        new ContaReservaDialog(this, controller.recarregar(reserva), controller).setVisible(true);
        atualizar();
    }
}
