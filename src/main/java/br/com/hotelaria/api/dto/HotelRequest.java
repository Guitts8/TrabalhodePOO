package br.com.hotelaria.api.dto;

import br.com.hotelaria.model.Endereco;
import br.com.hotelaria.model.Hotel;

public record HotelRequest(String nome, String telefone, String email, Integer estrelas,
                           String logradouro, String numero, String bairro, String cidade, String uf, String cep) {
    public void copiarPara(Hotel hotel) {
        hotel.setNome(nome);
        hotel.setTelefone(telefone);
        hotel.setEmail(email);
        hotel.setEstrelas(estrelas == null ? 0 : estrelas);
        hotel.setEndereco(new Endereco(logradouro, numero, bairro, cidade, uf, cep));
    }
}
