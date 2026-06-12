# Finance Crypto Backend

Este projeto é o backend de uma aplicação voltada para controle de carteira de criptomoedas. A ideia principal é permitir que o usuário consiga se cadastrar, fazer login e acessar funcionalidades relacionadas a ativos digitais, como ranking de criptomoedas e futuramente controle de transações, carteira e rentabilidade.

O backend foi desenvolvido em Java com Spring Boot, utilizando autenticação com JWT, banco de dados PostgreSQL e organização em camadas, separando controllers, services, repositories, entities e DTOs.

## Tecnologias utilizadas

* Java 21
* Spring Boot
* Spring Security
* JWT
* Maven
* PostgreSQL
* Spring Data JPA
* Hibernate
* Bean Validation
* Swagger/OpenAPI
* Flyway
* OpenFeign

## Estrutura do projeto

A estrutura principal do backend está organizada da seguinte forma:

```text
src/main/java/com/finance_crypto
├── config
│   ├── AdminUserConfig.java
│   └── SecurityConfig.java
│
├── controller
│   ├── RankingController.java
│   ├── TokenController.java
│   └── UserController.java
│
├── controller/dto
│   ├── LoginRequestDTO.java
│   ├── LoginResponseDTO.java
│   ├── RegisterUserRequestDTO.java
│   └── RegisterUserResponseDTO.java
│
├── dto
│   └── RankingAtivoDTO.java
│
├── entity
│   └── User.java
│
├── repository
│   └── UserRepository.java
│
├── service
│   ├── RankingService.java
│   └── UserService.java
│
└── Startup.java
```

## Como rodar o projeto

### 1. Clonar o repositório

```bash
git clone https://github.com/Greed2003/finance-crypto-backend.git
```

Depois entre na pasta do projeto:

```bash
cd finance-crypto-backend
```

### 2. Configurar o banco de dados

O projeto utiliza PostgreSQL. Antes de rodar o backend, é necessário criar um banco com o nome:

```text
finance_crypto_db
```

As configurações do banco estão no arquivo:

```text
src/main/resources/application.yml
```

Exemplo de configuração:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finance_crypto_db
    username: postgres
    password: admin
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

Caso a senha do PostgreSQL seja diferente no computador, é necessário alterar o campo `password`.

### 3. Rodar o backend

No Windows, use:

```bash
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS, use:

```bash
./mvnw spring-boot:run
```

Se tudo estiver correto, a aplicação será iniciada na porta:

```text
http://localhost:8080
```

## Autenticação

A autenticação do projeto é feita usando JWT. Primeiro o usuário faz login enviando username e senha. Se os dados estiverem corretos, o backend retorna um token.

Esse token deve ser enviado nas próximas requisições protegidas no formato:

```text
Authorization: Bearer SEU_TOKEN_AQUI
```

## Endpoints principais

### Cadastro de usuário

```http
POST /users
```

Exemplo de body:

```json
{
  "username": "igor",
  "email": "igor@email.com",
  "password": "123456"
}
```

Exemplo de resposta:

```json
{
  "userId": "uuid-do-usuario",
  "username": "igor",
  "email": "igor@email.com"
}
```

Esse endpoint permite criar um novo usuário no sistema. A senha é salva de forma criptografada usando BCrypt.

### Login

```http
POST /login
```

Exemplo de body:

```json
{
  "username": "igor",
  "password": "123456"
}
```

Exemplo de resposta:

```json
{
  "accessToken": "token-jwt-gerado",
  "expiresIn": 800
}
```

Esse endpoint realiza a autenticação do usuário e retorna o token JWT.

### Ranking de ativos

```http
GET /ranking/lucrativos
```

Esse endpoint retorna uma lista de ativos com informações de preço de compra, preço atual e rentabilidade.

Atualmente essa funcionalidade pode usar dados simulados ou lógica inicial, dependendo da versão do projeto. A ideia é que futuramente esse ranking seja calculado com base nas transações reais do usuário e nas cotações atuais dos ativos.

## Segurança

O projeto usa Spring Security para proteger as rotas da API.

As rotas públicas são:

```text
POST /login
POST /users
Swagger/OpenAPI
```

As demais rotas precisam de autenticação via token JWT.

A configuração principal de segurança está no arquivo:

```text
SecurityConfig.java
```

## Usuário administrador

O projeto possui uma configuração inicial para criar um usuário administrador automaticamente. Essa configuração está no arquivo:

```text
AdminUserConfig.java
```

Esse usuário facilita os testes iniciais da aplicação, principalmente para validar o login e a geração de token.

## Documentação da API

O projeto possui Swagger/OpenAPI configurado. Após iniciar o backend, a documentação pode ser acessada em:

```text
http://localhost:8080/swagger-ui/index.html
```

Nessa página é possível visualizar e testar os endpoints da aplicação.

## Possíveis erros ao rodar

### Erro de conexão com o banco

Se aparecer erro relacionado ao PostgreSQL, verifique:

* se o PostgreSQL está instalado;
* se o serviço do PostgreSQL está rodando;
* se o banco `finance_crypto_db` foi criado;
* se o usuário e senha no `application.yml` estão corretos;
* se a porta do banco é realmente `5432`.

### Erro de autenticação

Se uma rota protegida retornar erro `401 Unauthorized`, significa que o token JWT não foi enviado ou está inválido.

Nesse caso, faça login novamente e envie o token no header:

```text
Authorization: Bearer SEU_TOKEN_AQUI
```

### Erro ao cadastrar usuário repetido

Se o username ou e-mail já estiver cadastrado, o sistema não deve permitir criar outro usuário igual.

## Funcionalidades já implementadas

* Estrutura inicial do backend em Spring Boot;
* Configuração de segurança com Spring Security;
* Autenticação com JWT;
* Login de usuário;
* Cadastro de usuário;
* Criptografia de senha com BCrypt;
* Repository para busca de usuário por username e e-mail;
* Endpoint de ranking de ativos;
* Configuração com PostgreSQL;
* Documentação com Swagger/OpenAPI.

## Melhorias futuras

Algumas melhorias que ainda podem ser feitas no backend:

* criar CRUD de transações de compra e venda de criptomoedas;
* criar entidade de carteira do usuário;
* calcular preço médio dos ativos;
* calcular lucro e prejuízo de cada ativo;
* buscar cotação atual em uma API externa;
* substituir dados simulados por dados reais;
* criar testes automatizados para controllers e services;
* melhorar o tratamento de erros da API;
* usar variáveis de ambiente para senhas e chaves;
* organizar migrations do banco com Flyway.

## Observação final

Este backend ainda está em desenvolvimento, mas já possui uma base importante para autenticação, cadastro de usuário e organização da API. A partir dessa estrutura, é possível continuar evoluindo o projeto e adicionar as funcionalidades principais de uma carteira de criptomoedas.