package br.com.hotelaria.view.funcionario;

import br.com.hotelaria.controller.FuncionarioController;
import br.com.hotelaria.exception.RegraNegocioException;
import br.com.hotelaria.model.Administrador;
import br.com.hotelaria.model.Cargo;
import br.com.hotelaria.model.Escala;
import br.com.hotelaria.model.Funcionario;
import br.com.hotelaria.model.Hotel;
import br.com.hotelaria.model.Jornada;
import br.com.hotelaria.model.Recepcionista;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.FormularioDialog;
import br.com.hotelaria.view.Tema;
import br.com.hotelaria.view.componentes.Componentes;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.time.LocalTime;

public class FuncionarioDialog extends FormularioDialog {
    private final FuncionarioController controller;
    private final Funcionario funcionario;

    private final JTextField nome = Componentes.texto();
    private final JFormattedTextField cpf = Componentes.cpf();
    private final JFormattedTextField nascimento = Componentes.data();
    private final JTextField telefone = Componentes.texto("(00) 00000-0000");
    private final JTextField email = Componentes.texto("nome@redehoteleira.com.br");

    private final JComboBox<Cargo> cargo = new JComboBox<>(Cargo.values());
    private final JComboBox<Hotel> hotel;
    private final JTextField salarioBase = Componentes.texto("0,00");
    private final JFormattedTextField contratacao = Componentes.data();

    private final CardLayout cartoesCargo = new CardLayout();
    private final JPanel painelCargo = new JPanel(cartoesCargo);
    private final JTextField idiomas = Componentes.texto("Ex: Inglês, Espanhol");
    private final JTextField setor = Componentes.texto("Ex: Financeiro, RH, Compras");

    private final JFormattedTextField horaEntrada = Componentes.hora();
    private final JFormattedTextField horaSaida = Componentes.hora();
    private final JComboBox<Escala> escala = new JComboBox<>(Escala.values());
    private final JLabel resumoJornada = Componentes.subtitulo(" ");
    private final JLabel previaSalario = Componentes.destaque(" ", Tema.PRIMARIA);

    public FuncionarioDialog(Component pai, Funcionario funcionario, FuncionarioController controller) {
        super(pai, funcionario == null ? "Novo funcionário" : "Editar funcionário",
                "O cargo define os atributos extras e como o salário é calculado", 2);
        this.controller = controller;
        this.funcionario = funcionario;
        this.hotel = Componentes.combo(controller.listarHoteis());

        secao("Dados pessoais");
        campo("Nome completo *", nome, 2);
        campo("CPF *", cpf);
        campo("Data de nascimento", nascimento);
        campo("Telefone", telefone);
        campo("E-mail", email);

        secao("Contrato");
        campo("Cargo *", cargo);
        campo("Hotel (unidade) *", hotel);
        campo("Salário base (R$) *", salarioBase);
        campo("Data de contratação *", contratacao);
        montarCartoesDoCargo();
        componente(painelCargo, 2);

        secao("Jornada de trabalho");
        campo("Hora de entrada *", horaEntrada);
        campo("Hora de saída *", horaSaida);
        campo("Escala *", escala);
        componente(resumoJornada, 1);
        componente(previaSalario, 2);

        cargo.addActionListener(e -> {
            cartoesCargo.show(painelCargo, ((Cargo) cargo.getSelectedItem()).name());
            atualizarPrevia();
        });
        escala.addActionListener(e -> atualizarPrevia());
        ouvirMudancas(salarioBase);
        ouvirMudancas(horaEntrada);
        ouvirMudancas(horaSaida);

        if (funcionario != null) {
            preencherCampos();
            cargo.setEnabled(false);
        } else {
            hotel.setSelectedIndex(-1);
            horaEntrada.setText("08:00");
            horaSaida.setText("17:00");
        }
        cartoesCargo.show(painelCargo, ((Cargo) cargo.getSelectedItem()).name());
        atualizarPrevia();
    }

    private void montarCartoesDoCargo() {
        JPanel gerente = new JPanel(new BorderLayout());
        gerente.add(Componentes.subtitulo("Recebe 30% de gratificação de função. Só pode haver um gerente por hotel."),
                BorderLayout.CENTER);

        JPanel recepcionista = new JPanel(new BorderLayout(0, 4));
        recepcionista.add(Componentes.subtitulo("Idiomas que atende (no turno da noite recebe +20% de adicional):"),
                BorderLayout.NORTH);
        recepcionista.add(idiomas, BorderLayout.CENTER);

        JPanel administrador = new JPanel(new BorderLayout(0, 4));
        administrador.add(Componentes.subtitulo("Setor (o administrador recebe só o salário base):"), BorderLayout.NORTH);
        administrador.add(setor, BorderLayout.CENTER);

        painelCargo.add(gerente, Cargo.GERENTE.name());
        painelCargo.add(recepcionista, Cargo.RECEPCIONISTA.name());
        painelCargo.add(administrador, Cargo.ADMINISTRADOR.name());
    }

    private void ouvirMudancas(JTextComponent campo) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarPrevia();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarPrevia();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarPrevia();
            }
        });
    }

    private void atualizarPrevia() {
        Jornada jornada = null;
        try {
            jornada = new Jornada(Formatos.lerHora(horaEntrada.getText(), "Hora de entrada"),
                    Formatos.lerHora(horaSaida.getText(), "Hora de saída"), (Escala) escala.getSelectedItem());
            String texto = horas(jornada.getHorasPorDia()) + " por dia, " + horas(jornada.getHorasSemanais())
                    + " por semana, turno " + (jornada.isNoturna() ? "noturno" : "diurno");
            resumoJornada.setForeground(Tema.TEXTO_SECUNDARIO);
            jornada.validar();
            resumoJornada.setText(texto);
        } catch (RegraNegocioException e) {
            resumoJornada.setForeground(Tema.VERMELHO);
            resumoJornada.setText("<html><body style='width: 230px'>" + e.getMessage() + "</body></html>");
        }

        try {
            Funcionario exemplo = ((Cargo) cargo.getSelectedItem()).novoFuncionario();
            exemplo.setSalarioBase(Formatos.lerValor(salarioBase.getText(), "salário base"));
            if (jornada != null) {
                try {
                    exemplo.setJornada(jornada);
                } catch (RegraNegocioException e) {
                }
            }
            previaSalario.setText("Salário calculado: " + Formatos.moeda(exemplo.calcularSalario())
                    + "   (" + exemplo.getComposicaoSalario() + ")");
        } catch (RegraNegocioException e) {
            previaSalario.setText("Digite o salário base para ver o salário calculado do cargo.");
        }
    }

    private static String horas(double valor) {
        int inteiras = (int) valor;
        int minutos = (int) Math.round((valor - inteiras) * 60);
        return minutos == 0 ? inteiras + "h" : inteiras + "h" + String.format("%02d", minutos);
    }

    private void preencherCampos() {
        nome.setText(funcionario.getNome());
        cpf.setText(funcionario.getCpfFormatado());
        nascimento.setText(Formatos.data(funcionario.getDataNascimento()));
        telefone.setText(funcionario.getTelefone());
        email.setText(funcionario.getEmail());
        cargo.setSelectedItem(funcionario.getCargo());
        hotel.setSelectedItem(funcionario.getHotel());
        salarioBase.setText(Formatos.valor(funcionario.getSalarioBase()));
        contratacao.setText(Formatos.data(funcionario.getDataContratacao()));
        if (funcionario.getJornada() != null) {
            horaEntrada.setText(Formatos.hora(funcionario.getJornada().getHoraEntrada()));
            horaSaida.setText(Formatos.hora(funcionario.getJornada().getHoraSaida()));
            escala.setSelectedItem(funcionario.getJornada().getEscala());
        }
        if (funcionario instanceof Recepcionista recepcionista) {
            idiomas.setText(recepcionista.getIdiomas());
        } else if (funcionario instanceof Administrador administrador) {
            setor.setText(administrador.getSetor());
        }
    }

    @Override
    protected void salvar() {
        Funcionario dados = funcionario == null
                ? controller.novoFuncionario((Cargo) cargo.getSelectedItem())
                : funcionario;
        dados.setNome(nome.getText());
        dados.setCpf(cpf.getText());
        dados.setDataNascimento(Formatos.lerData(nascimento.getText(), "Data de nascimento"));
        dados.setTelefone(telefone.getText());
        dados.setEmail(email.getText());
        dados.setHotel((Hotel) hotel.getSelectedItem());
        dados.setSalarioBase(Formatos.lerValor(salarioBase.getText(), "salário base"));
        dados.setDataContratacao(Formatos.lerData(contratacao.getText(), "Data de contratação"));
        LocalTime entrada = Formatos.lerHora(horaEntrada.getText(), "Hora de entrada");
        LocalTime saida = Formatos.lerHora(horaSaida.getText(), "Hora de saída");
        dados.setJornada(new Jornada(entrada, saida, (Escala) escala.getSelectedItem()));

        if (dados instanceof Recepcionista recepcionista) {
            recepcionista.setIdiomas(idiomas.getText());
        } else if (dados instanceof Administrador administrador) {
            administrador.setSetor(setor.getText());
        }
        controller.salvar(dados);
    }
}
