# 🧪 API Tests — REST Assured + Maven + ServeRest

![Java](https://img.shields.io/badge/Java-17%2B-007396?logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?logo=apachemaven&logoColor=white)
![REST Assured](https://img.shields.io/badge/REST%20Assured-5.x-4CAF50)
![TestNG](https://img.shields.io/badge/TestNG-7.x-FF6600)
![ServeRest](https://img.shields.io/badge/API%20Alvo-ServeRest-blueviolet)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)

> Projeto de automação de testes de API REST desenvolvido com **Java**, **REST Assured**, **TestNG** e **Maven**, utilizando o [ServeRest](https://serverest.dev) como API alvo. Criado como laboratório prático de aprendizado em automação de testes de API.

---

## 📋 Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias e Dependências](#tecnologias-e-dependências)
- [Pré-requisitos](#pré-requisitos)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Como Executar](#como-executar)
- [Endpoints e Cenários de Teste](#endpoints-e-cenários-de-teste)
  - [POST /login](#post-login)
  - [/usuarios](#usuarios)
  - [/produtos](#produtos)
  - [/carrinhos](#carrinhos)
- [Validações Implementadas](#validações-implementadas)
- [Próximos Passos](#próximos-passos)
- [Autor](#autor)

---

## 📖 Sobre o Projeto

Este repositório é um projeto de estudo prático em automação de testes de API, com foco no uso do **REST Assured** — uma das bibliotecas Java mais utilizadas pelo mercado para testes de serviços REST.

A API alvo utilizada é o **ServeRest**, um servidor REST open source que simula uma loja virtual, amplamente adotado pela comunidade QA brasileira como ambiente de treino.

**Objetivos de aprendizado:**

- Escrever testes de API automatizados com REST Assured e TestNG
- Validar status codes HTTP em diferentes cenários (sucesso e falha)
- Realizar validação de contratos via **JSON Schema Validation**
- Testar fluxos que exigem **autenticação Bearer token**
- Organizar um projeto de testes por domínio/endpoint com Maven

---

## 🛠 Tecnologias e Dependências

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 17+ | Linguagem principal |
| Maven | 3.8+ | Gerenciamento de dependências e build |
| REST Assured | 5.x | DSL para testes de API REST |
| TestNG | 7.x | Framework de testes |
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

<!-- TestNG -->
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.8.0</version>
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
│       │   ├── base/
│       │   │   └── BaseTest.java              # Configurações globais (base URI, logs, auth)
│       │   │
│       │   └── tests/
│       │       ├── login/
│       │       │   └── LoginTest.java         # Testes do endpoint POST /login
│       │       │
│       │       ├── usuarios/
│       │       │   ├── PostUsuariosTest.java  # Criar usuário
│       │       │   ├── GetUsuariosTest.java   # Listar / buscar usuário
│       │       │   ├── PutUsuariosTest.java   # Atualizar usuário
│       │       │   └── DeleteUsuariosTest.java# Deletar usuário
│       │       │
│       │       ├── produtos/
│       │       │   ├── PostProdutosTest.java  # Criar produto (requer auth)
│       │       │   ├── GetProdutosTest.java   # Listar / buscar produto
│       │       │   ├── PutProdutosTest.java   # Atualizar produto (requer auth)
│       │       │   └── DeleteProdutosTest.java# Deletar produto (requer auth)
│       │       │
│       │       └── carrinhos/
│       │           ├── PostCarrinhosTest.java # Criar carrinho (requer auth)
│       │           ├── GetCarrinhosTest.java  # Listar / buscar carrinho
│       │           └── DeleteCarrinhosTest.java # Cancelar/concluir compra (requer auth)
│       │
│       └── resources/
│           ├── schemas/
│           │   ├── login-schema.json          # Contrato da resposta de login
│           │   ├── usuarios-schema.json       # Contrato da resposta de usuários
│           │   ├── produtos-schema.json       # Contrato da resposta de produtos
│           │   └── carrinhos-schema.json      # Contrato da resposta de carrinhos
│           │
│           └── testng.xml                     # Suite de execução TestNG
│
├── pom.xml
└── README.md
```

---

## ▶️ Como Executar

### 1. Clone o repositório

```bash
git clone https://github.com/geovanegustavo/api-tests-restassured-maven-serverest.git
cd api-tests-restassured-maven-serverest
```

### 2. Execute toda a suite de testes

```bash
mvn test
```

### 3. Execute um grupo de testes específico

```bash
# Apenas testes de login
mvn test -Dtest=LoginTest

# Apenas testes de usuários
mvn test -Dtest="PostUsuariosTest,GetUsuariosTest"

# Via suite TestNG
mvn test -DsuiteXmlFile=src/test/resources/testng.xml
```

> A API ServeRest está disponível publicamente em `https://serverest.dev`. Nenhuma configuração adicional é necessária.

---

## 🌐 Endpoints e Cenários de Teste

A URL base utilizada em todos os testes é: **`https://serverest.dev`**

---

### POST /login

Autentica um usuário e retorna o **Bearer token** utilizado nos endpoints protegidos.

| Cenário | Payload | Status Esperado |
|---|---|---|
| Login com credenciais válidas | email e senha corretos | `200 OK` |
| Login com senha incorreta | email válido + senha errada | `401 Unauthorized` |
| Login sem campo email | body sem o campo `email` | `400 Bad Request` |
| Login sem campo password | body sem o campo `password` | `400 Bad Request` |

**Response de sucesso (200):**
```json
{
  "message": "Login realizado com sucesso",
  "authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### /usuarios

Gerenciamento de usuários. Operações de leitura são abertas; escrita e deleção são abertas mas com regras de negócio específicas.

| Método | Cenário | Auth | Status Esperado |
|---|---|---|---|
| `GET` | Listar todos os usuários | ❌ | `200 OK` |
| `GET` | Buscar usuário por ID válido | ❌ | `200 OK` |
| `GET` | Buscar usuário com ID inexistente | ❌ | `400 Bad Request` |
| `POST` | Criar usuário com dados válidos | ❌ | `201 Created` |
| `POST` | Criar usuário com e-mail já cadastrado | ❌ | `400 Bad Request` |
| `POST` | Criar usuário sem campo obrigatório | ❌ | `400 Bad Request` |
| `PUT` | Atualizar usuário existente | ❌ | `200 OK` |
| `PUT` | Criar usuário via PUT (ID inexistente) | ❌ | `201 Created` |
| `DELETE` | Deletar usuário existente sem carrinho | ❌ | `200 OK` |
| `DELETE` | Deletar usuário com carrinho ativo | ❌ | `400 Bad Request` |
| `DELETE` | Deletar usuário com ID inexistente | ❌ | `200 OK` |

---

### /produtos

Gerenciamento de produtos. Operações de escrita exigem **autenticação de administrador**.

| Método | Cenário | Auth | Status Esperado |
|---|---|---|---|
| `GET` | Listar todos os produtos | ❌ | `200 OK` |
| `GET` | Buscar produto por ID válido | ❌ | `200 OK` |
| `GET` | Buscar produto com ID inexistente | ❌ | `400 Bad Request` |
| `POST` | Criar produto com token válido (admin) | ✅ Bearer | `201 Created` |
| `POST` | Criar produto sem token | ❌ | `401 Unauthorized` |
| `POST` | Criar produto com nome duplicado | ✅ Bearer | `400 Bad Request` |
| `PUT` | Atualizar produto existente | ✅ Bearer | `200 OK` |
| `PUT` | Criar produto via PUT (ID inexistente) | ✅ Bearer | `201 Created` |
| `DELETE` | Deletar produto sem carrinho ativo | ✅ Bearer | `200 OK` |
| `DELETE` | Deletar produto com carrinho ativo | ✅ Bearer | `400 Bad Request` |

---

### /carrinhos

Gerenciamento de carrinhos de compra. Todos os endpoints exigem **autenticação**.

| Método | Cenário | Auth | Status Esperado |
|---|---|---|---|
| `GET` | Listar todos os carrinhos | ❌ | `200 OK` |
| `GET` | Buscar carrinho por ID válido | ❌ | `200 OK` |
| `POST` | Criar carrinho com produto válido | ✅ Bearer | `201 Created` |
| `POST` | Criar carrinho sem token | ❌ | `401 Unauthorized` |
| `POST` | Criar carrinho já existente (usuário já tem um) | ✅ Bearer | `400 Bad Request` |
| `POST` | Criar carrinho com produto inexistente | ✅ Bearer | `400 Bad Request` |
| `POST` | Criar carrinho com quantidade insuficiente em estoque | ✅ Bearer | `400 Bad Request` |
| `DELETE /concluir-compra` | Concluir compra (baixa no estoque) | ✅ Bearer | `200 OK` |
| `DELETE /cancelar-compra` | Cancelar compra (devolve ao estoque) | ✅ Bearer | `200 OK` |
| `DELETE /concluir-compra` | Concluir sem carrinho ativo | ✅ Bearer | `200 OK` |

---

## 🧩 Validações Implementadas

### Configuração global em `BaseTest.java`

```java
@BeforeClass
public void setup() {
    RestAssured.baseURI = "https://serverest.dev";
    RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
}
```

---

### Validação de Status Code

```java
given()
    .contentType(ContentType.JSON)
    .body(payload)
.when()
    .post("/login")
.then()
    .statusCode(200);
```

---

### Validação de Body da Resposta

```java
.then()
    .statusCode(200)
    .body("message", equalTo("Login realizado com sucesso"))
    .body("authorization", notNullValue());
```

---

### Validação de Contrato — JSON Schema

```java
.then()
    .statusCode(200)
    .body(matchesJsonSchemaInClasspath("schemas/login-schema.json"));
```

**Exemplo — `login-schema.json`:**

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["message", "authorization"],
  "properties": {
    "message": { "type": "string" },
    "authorization": { "type": "string" }
  },
  "additionalProperties": false
}
```

---

### Autenticação Bearer Token

O token é obtido via `POST /login` e reutilizado nos endpoints protegidos usando `@BeforeClass` do TestNG:

```java
public class BaseTest {

    protected static String bearerToken;

    @BeforeClass
    public void autenticar() {
        bearerToken =
            given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"fulano@qa.com\", \"password\": \"teste\"}")
            .when()
                .post("/login")
            .then()
                .statusCode(200)
                .extract().path("authorization");
    }
}
```

Uso nos testes que exigem autenticação:

```java
given()
    .contentType(ContentType.JSON)
    .header("Authorization", bearerToken)
    .body(produtoPayload)
.when()
    .post("/produtos")
.then()
    .statusCode(201);
```

---

## 🚀 Próximos Passos

- [ ] Implementar classes de teste para todos os endpoints listados
- [ ] Criar JSON Schemas para todos os contratos (`/usuarios`, `/produtos`, `/carrinhos`)
- [ ] Adicionar `testng.xml` com agrupamento por suíte e por endpoint
- [ ] Implementar testes de fluxo E2E (login → criar produto → montar carrinho → concluir compra)
- [ ] Adicionar geração de relatórios com **Allure Report**
- [ ] Configurar pipeline de CI/CD com **GitHub Actions**
- [ ] Externalizar massa de dados para arquivos JSON em `resources/data/`

---

## 👤 Autor

**Geovane Gustavo Torres**

Profissional de QA.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Geovane%20Gustavo-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/geovanegustavo)
[![GitHub](https://img.shields.io/badge/GitHub-geovanegustavo-181717?logo=github&logoColor=white)](https://github.com/geovanegustavo)

---

> 💡 Documentação completa da API ServeRest disponível em [serverest.dev](https://serverest.dev) — interface Swagger com todos os endpoints, payloads e exemplos de resposta.
