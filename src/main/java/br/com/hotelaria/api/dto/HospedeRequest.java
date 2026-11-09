package br.com.hotelaria.api.dto;

import br.com.hotelaria.model.Hospede;

import java.time.LocalDate;

public record HospedeRequest(String nome, String cpf, LocalDate dataNascimento, String telefone, String email,
                             String cidade, String uf) {
    public void copiarPara(Hospede hospede) {
        hospede.setNome(nome);
        hospede.setCpf(cpf);
        hospede.setDataNascimento(dataNascimento);
        hospede.setTelefone(telefone);
        hospede.setEmail(email);
        hospede.setCidade(cidade);
        hospede.setUf(uf);
    }
}
