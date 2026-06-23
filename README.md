# 🧪 API Tests — REST Assured + Maven + ServeRest

![Java](https://img.shields.io/badge/Java-17%2B-007396?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?logo=apachemaven&logoColor=white)
![REST Assured](https://img.shields.io/badge/REST%20Assured-5.x-4CAF50)
![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white)
![ServeRest](https://img.shields.io/badge/ServeRest-API%20Alvo-blueviolet)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)

> Projeto de automação de testes de API REST desenvolvido com **Java**, **REST Assured** e **Maven**, utilizando o [ServeRest](https://serverest.dev) como API alvo. Criado como laboratório prático de aprendizado em automação de testes de API.

---

## 📋 Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias e Dependências](#tecnologias-e-dependências)
- [Pré-requisitos](#pré-requisitos)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Como Executar](#como-executar)
- [Endpoints Testados](#endpoints-testados)
- [Cenários de Teste](#cenários-de-teste)
- [Validações Implementadas](#validações-implementadas)
- [Próximos Passos](#próximos-passos)
- [Autor](#autor)

---

## 📖 Sobre o Projeto

Este repositório representa um projeto de estudo prático em automação de testes de API, com foco no uso do **REST Assured** — uma das bibliotecas Java mais utilizadas pelo mercado para testes de serviços REST.

A API alvo utilizada é o **ServeRest**, um servidor REST open source que simula uma loja virtual, amplamente adotado pela comunidade QA brasileira como ambiente de treino.

**Objetivos de aprendizado:**

- Escrever testes de API automatizados com REST Assured e JUnit 5
- Validar status codes HTTP em diferentes cenários (sucesso e falha)
- Realizar validação de contratos via **JSON Schema Validation**
- Organizar um projeto de testes com Maven, seguindo boas práticas
- Compreender o fluxo de autenticação via token (JWT/Bearer)

---

## 🛠 Tecnologias e Dependências

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 17+ | Linguagem principal |
| Maven | 3.8+ | Gerenciamento de dependências e build |
| REST Assured | 5.x | DSL para testes de API REST |
| JUnit 5 | 5.x | Framework de testes |
| JSON Schema Validator | 5.x | Validação de contrato de resposta |
| Hamcrest | (incluso) | Asserções expressivas |

### `pom.xml` — Dependências principais

```xml
<!-- REST Assured -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.3.2</version>
    <scope>test</scope>
</dependency>

<!-- JSON Schema Validator -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>5.3.2</version>
    <scope>test</scope>
</dependency>

<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

---

## ✅ Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- **Java JDK 17+** — [Download](https://adoptium.net/)
- **Apache Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- **Git** — [Download](https://git-scm.com/)
- **IDE** recomendada: [IntelliJ IDEA](https://www.jetbrains.com/idea/) ou [Eclipse](https://www.eclipse.org/)

Verificar instalação:

```bash
java -version
mvn -version
```

---

## 📁 Estrutura do Projeto

```
api-tests-restassured-maven-serverest/
│
├── src/
│   └── test/
│       ├── java/
│       │   └── tests/
│       │       └── LoginTest.java          # Testes do endpoint POST /login
│       │
│       └── resources/
│           └── schemas/
│               └── login-schema.json       # JSON Schema para validação de contrato
│
├── pom.xml                                 # Configuração Maven e dependências
└── README.md
```

---

## ▶️ Como Executar

### 1. Clone o repositório

```bash
git clone https://github.com/geovanegustavo/api-tests-restassured-maven-serverest.git
cd api-tests-restassured-maven-serverest
```

### 2. Execute os testes

```bash
mvn test
```

Os testes são executados contra a URL pública do ServeRest: `https://serverest.dev`

> **Nota:** Nenhuma configuração adicional é necessária. A API ServeRest está disponível publicamente e não requer instalação local.

### 3. Executar um teste específico

```bash
mvn test -Dtest=LoginTest
```

---

## 🌐 Endpoints Testados

### `POST /login`

Endpoint de autenticação da API ServeRest.

| Informação | Valor |
|---|---|
| **Método** | POST |
| **URL** | `https://serverest.dev/login` |
| **Content-Type** | `application/json` |
| **Autenticação** | Não requerida |

**Request Body:**

```json
{
  "email": "fulano@qa.com",
  "password": "teste"
}
```

**Response de sucesso (200):**

```json
{
  "message": "Login realizado com sucesso",
  "authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 🔬 Cenários de Teste

### `LoginTest.java`

| # | Cenário | Input | Status Code Esperado |
|---|---|---|---|
| 1 | Login com credenciais válidas | E-mail e senha corretos | `200 OK` |
| 2 | Login com senha incorreta | E-mail válido + senha errada | `401 Unauthorized` |
| 3 | Login sem e-mail no body | Campo email ausente | `400 Bad Request` |

---

## 🧩 Validações Implementadas

### Validação de Status Code

```java
given()
    .contentType(ContentType.JSON)
    .body(loginPayload)
.when()
    .post("/login")
.then()
    .statusCode(200);
```

### Validação de Corpo da Resposta (mensagem)

```java
.then()
    .statusCode(200)
    .body("message", equalTo("Login realizado com sucesso"));
```

### Validação de Contrato — JSON Schema

```java
.then()
    .statusCode(200)
    .body(matchesJsonSchemaInClasspath("schemas/login-schema.json"));
```

**Arquivo `login-schema.json`:**

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "message": {
      "type": "string"
    },
    "authorization": {
      "type": "string"
    }
  },
  "required": ["message", "authorization"]
}
```

### Validação de Cenário de Erro (401)

```java
given()
    .contentType(ContentType.JSON)
    .body("{\"email\": \"fulano@qa.com\", \"password\": \"senhaerrada\"}")
.when()
    .post("/login")
.then()
    .statusCode(401)
    .body("message", equalTo("Email e/ou senha inválidos"));
```

---

## 🚀 Próximos Passos

Melhorias planejadas para evolução do projeto:

- [ ] Adicionar testes para outros endpoints do ServeRest (`/usuarios`, `/produtos`, `/carrinhos`)
- [ ] Implementar classe `BaseTest` com configurações globais (base URI, logs)
- [ ] Extrair payloads para arquivos JSON externos em `resources/`
- [ ] Adicionar geração de relatórios com **Allure Report**
- [ ] Implementar pipeline de CI/CD com **GitHub Actions**
- [ ] Adicionar testes de contrato para outros endpoints
- [ ] Criar testes de fluxo E2E (login → criar produto → adicionar ao carrinho)

---

## 👤 Autor

**Geovane Gustavo Torres**

Profissional de QA com mais de 20 anos de experiência em testes manuais, em transição para automação de testes com foco em APIs REST.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Geovane%20Gustavo-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/geovanegustavo)
[![GitHub](https://img.shields.io/badge/GitHub-geovanegustavo-181717?logo=github&logoColor=white)](https://github.com/geovanegustavo)

---

## 📄 Licença

Este projeto é de uso livre para fins de estudo e aprendizado.

---

> 💡 **Dica:** Para explorar mais a API ServeRest, acesse a documentação oficial em [serverest.dev](https://serverest.dev) — ela possui interface Swagger completa com todos os endpoints disponíveis.
