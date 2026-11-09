# Rede Hoteleira

Trabalho da disciplina de Programação Orientada a Objetos.

Sistema para gerenciar uma rede de hotéis: cadastro de hotéis, quartos, hóspedes, funcionários e serviços, e controle das reservas (check-in, check-out, serviços consumidos e conta final).

Feito por Raul Guilherme Rodrigues da Silva.

## Tecnologias

Java 21, Spring Boot (Web e Data JPA), Swing com FlatLaf para as telas, banco H2 salvo em arquivo e JUnit nos testes. A API tem documentação pelo Swagger.

## Como rodar

Precisa do JDK 21 instalado.

Pela IDE é só rodar a classe `RedeHoteleiraApplication`. Pelo terminal, na pasta do projeto:

```
mvnw.cmd spring-boot:run
```

Quando abre, aparece a tela do sistema e a API sobe junto em `http://localhost:8080/api`.

- Swagger: http://localhost:8080/swagger-ui.html
- Console do H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./dados/hotelaria`, usuário `sa`, sem senha)

Na primeira vez o sistema cria alguns dados de exemplo. Se quiser começar do zero, é só apagar a pasta `dados`.

Também dá para usar MySQL ativando o perfil `mysql` (configuração em `application-mysql.properties`).

Para rodar os testes: `mvnw.cmd test`

## Organização

- `model` - classes do domínio (Hotel, Quarto, Reserva, Hospede, Funcionario...)
- `repository` - acesso ao banco
- `service` - regras de negócio
- `controller` e `view` - telas em Swing
- `api` - endpoints REST
- `exception`, `config` e `util` - exceções, dados iniciais e classes de apoio

## Regras principais

- Não deixa reservar um quarto em datas que batem com outra reserva
- O valor da diária muda conforme o tipo do quarto (Standard, Luxo ou Suíte)
- Desconto de 10% para estadias de 7 diárias ou mais e de 5% para hóspede com 3 ou mais hospedagens
- A reserva passa por pendente, confirmada, em andamento e finalizada (ou cancelada)
- No check-in o quarto fica ocupado e no check-out vai para limpeza
- O salário do funcionário depende do cargo (gerente tem gratificação, recepcionista noturno tem adicional)
- O responsável pela reserva precisa ser maior de idade

## POO no projeto

- **Herança:** `Pessoa` é a base de `Hospede` e `Funcionario`. `Funcionario` tem as subclasses `Gerente`, `Recepcionista` e `Administrador`, e `Quarto` tem `QuartoStandard`, `QuartoLuxo` e `Suite`.
- **Classes abstratas:** `Pessoa`, `Quarto` e `Funcionario`.
- **Polimorfismo:** cada tipo de quarto calcula a diária do seu jeito e cada cargo calcula o salário do seu jeito.
- **Interface:** `RegraDesconto`, com uma classe para cada tipo de desconto.
- **Encapsulamento:** os setters validam os dados e o status da reserva só muda pelos métodos `confirmar`, `cancelar`, `fazerCheckIn` e `fazerCheckOut`.
