package br.com.hotelaria.view.inicio;

import br.com.hotelaria.controller.InicioController;
import br.com.hotelaria.model.Reserva;
import br.com.hotelaria.service.ResumoService;
import br.com.hotelaria.util.Formatos;
import br.com.hotelaria.view.Atualizavel;
import br.com.hotelaria.view.Tema;
import br.com.hotelaria.view.TratadorErros;
import br.com.hotelaria.view.componentes.CartaoResumo;
import br.com.hotelaria.view.componentes.Componentes;
import br.com.hotelaria.view.componentes.ModeloTabela;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PainelInicio extends JPanel implements Atualizavel {
    private record Movimento(String tipo, Reserva reserva) {
    }

    private final InicioController controller;

    private final CartaoResumo cartaoHoteis = new CartaoResumo("Hotéis na rede", Tema.ROXO);
    private final CartaoResumo cartaoDisponiveis = new CartaoResumo("Quartos disponíveis", Tema.VERDE);
    private final CartaoResumo cartaoOcupados = new CartaoResumo("Quartos ocupados", Tema.VERMELHO);
    private final CartaoResumo cartaoHospedes = new CartaoResumo("Hóspedes no momento", Tema.AZUL);
    private final CartaoResumo cartaoChegadas = new CartaoResumo("Check-ins previstos hoje", Tema.LARANJA);
    private final CartaoResumo cartaoSaidas = new CartaoResumo("Check-outs previstos hoje", Tema.AMARELO);

    private final ModeloTabela<ResumoService.OcupacaoHotel> modeloOcupacao = new ModeloTabela<>();
    private final ModeloTabela<Movimento> modeloMovimento = new ModeloTabela<>();
    private final JLabel rotuloData = Componentes.subtitulo(" ");

    public PainelInicio(InicioController controller) {
        super(new BorderLayout(0, 18));
        this.controller = controller;
        setBackground(Tema.FUNDO);
        setBorder(new EmptyBorder(22, 26, 22, 26));

        JButton botaoAtualizar = Tema.botao("Atualizar");
        botaoAtualizar.addActionListener(e -> atualizar());
        JPanel areaBotao = new JPanel(new GridBagLayout());
        areaBotao.setOpaque(false);
        areaBotao.add(botaoAtualizar);
        JPanel linhaTitulo = new JPanel(new BorderLayout());
        linhaTitulo.setOpaque(false);
        linhaTitulo.add(Componentes.titulo("Painel da rede"), BorderLayout.WEST);
        linhaTitulo.add(areaBotao, BorderLayout.EAST);
        rotuloData.setText(Formatos.dataPorExtenso(LocalDate.now()));

        JPanel cabecalho = new JPanel(new BorderLayout(0, 2));
        cabecalho.setOpaque(false);
        cabecalho.add(linhaTitulo, BorderLayout.NORTH);
        cabecalho.add(rotuloData, BorderLayout.SOUTH);
        add(cabecalho, BorderLayout.NORTH);

        JPanel cartoes = new JPanel(new GridLayout(2, 3, 14, 14));
        cartoes.setOpaque(false);
        cartoes.add(cartaoHoteis);
        cartoes.add(cartaoDisponiveis);
        cartoes.add(cartaoOcupados);
        cartoes.add(cartaoHospedes);
        cartoes.add(cartaoChegadas);
        cartoes.add(cartaoSaidas);

        modeloOcupacao.coluna("Hotel", ResumoService.OcupacaoHotel::hotel)
                .coluna("Quartos", ResumoService.OcupacaoHotel::quartos)
                .coluna("Ocupados", ResumoService.OcupacaoHotel::ocupados)
                .coluna("Livres", ResumoService.OcupacaoHotel::disponiveis)
                .coluna("Ocupação", o -> Math.round(o.taxaOcupacao() * 100) + "%");
        JTable tabelaOcupacao = Componentes.tabela(modeloOcupacao);
        Componentes.larguras(tabelaOcupacao, 170, 60, 70, 55, 75);
        Componentes.centralizar(tabelaOcupacao, 1, 2, 3, 4);

        modeloMovimento.coluna("Movimento", Movimento::tipo)
                .coluna("Reserva", m -> "nº " + m.reserva().getId())
                .coluna("Hóspede", m -> m.reserva().getHospede().getNome())
                .coluna("Hotel", m -> m.reserva().getQuarto().getHotel().getNome())
                .coluna("Quarto", m -> m.reserva().getQuarto().getNumero())
                .coluna("Situação", m -> m.reserva().getStatus());
        JTable tabelaMovimento = Componentes.tabela(modeloMovimento);
        Componentes.larguras(tabelaMovimento, 95, 60, 145, 160, 55, 120);
        Componentes.coresDeSituacao(tabelaMovimento, 5);

        JPanel tabelas = new JPanel(new BorderLayout(16, 0));
        tabelas.setOpaque(false);
        JPanel blocoOcupacao = bloco("Ocupação por hotel", tabelaOcupacao);
        blocoOcupacao.setPreferredSize(new Dimension(430, 0));
        tabelas.add(blocoOcupacao, BorderLayout.WEST);
        tabelas.add(bloco("Movimento de hoje (check-in e check-out são feitos na tela Reservas)", tabelaMovimento),
                BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(0, 18));
        centro.setOpaque(false);
        centro.add(cartoes, BorderLayout.NORTH);
        centro.add(tabelas, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    @Override
    public void atualizar() {
        TratadorErros.executar(this, () -> {
            ResumoService.Resumo resumo = controller.resumo();
            long percentual = resumo.quartos() == 0 ? 0 : Math.round(resumo.quartosOcupados() * 100.0 / resumo.quartos());

            cartaoHoteis.setValor(String.valueOf(resumo.hoteis()), resumo.quartos() + " quartos cadastrados");
            cartaoDisponiveis.setValor(String.valueOf(resumo.quartosDisponiveis()), "prontos para receber hóspedes");
            cartaoOcupados.setValor(String.valueOf(resumo.quartosOcupados()), percentual + "% de ocupação na rede");
            cartaoHospedes.setValor(String.valueOf(resumo.hospedesNoMomento()), "pessoas em hospedagens em andamento");
            cartaoChegadas.setValor(String.valueOf(resumo.checkInsHoje()),
                    resumo.reservasPendentes() + " reserva(s) aguardando confirmação");
            cartaoSaidas.setValor(String.valueOf(resumo.checkOutsHoje()), "hospedagens que terminam hoje");

            modeloOcupacao.setLinhas(controller.ocupacaoPorHotel());

            List<Movimento> movimentos = new ArrayList<>();
            for (Reserva reserva : controller.chegadasDeHoje()) {
                movimentos.add(new Movimento("Check-in", reserva));
            }
            for (Reserva reserva : controller.saidasDeHoje()) {
                movimentos.add(new Movimento("Check-out", reserva));
            }
            modeloMovimento.setLinhas(movimentos);
            rotuloData.setText(Formatos.dataPorExtenso(LocalDate.now()));
        });
    }

    private JPanel bloco(String titulo, JTable tabela) {
        JPanel bloco = new JPanel(new BorderLayout(0, 8));
        bloco.setOpaque(false);
        JLabel rotulo = new JLabel(titulo);
        rotulo.setFont(Tema.fonte(Font.BOLD, 14f));
        rotulo.setForeground(Tema.TEXTO);
        bloco.add(rotulo, BorderLayout.NORTH);
        bloco.add(Componentes.rolagem(tabela), BorderLayout.CENTER);
        return bloco;
    }
}
