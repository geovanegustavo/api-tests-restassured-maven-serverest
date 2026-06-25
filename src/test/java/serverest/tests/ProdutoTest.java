package serverest.tests;

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import serverest.base.BaseTest;
import serverest.model.LoginResponse;
import serverest.model.Produto;
import serverest.model.Usuario;
import serverest.util.ProdutoHelper;
import serverest.util.UsuarioHelper;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static serverest.util.Constants.*;

public class ProdutoTest extends BaseTest {

    private String token;
    private String produtoId;
    private Produto produtoCriado = ProdutoHelper.gerarProdutoAleatorio();

    @BeforeClass(description = "Garante que o teste de produtos tenha um token válido e independente")
    public void setupProdutoTest() {
        // 1. Cria um utilizador administrador dinâmico para este contexto de testes
        String emailAdmin = UsuarioHelper.gerarEmail();
        String senhaAdmin = "1234";
        Usuario usuarioAdmin = new Usuario("Admin QA Produtos", emailAdmin, senhaAdmin, "true");

        requestJson()
            .body(usuarioAdmin)
        .when()
            .post("/usuarios")
        .then()
            .statusCode(201);

        // 2. Realiza o login para obter o token de forma autónoma
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", emailAdmin);
        credenciais.put("password", senhaAdmin);

        LoginResponse respostaLogin = requestJson()
            .body(credenciais)
        .when()
            .post("/login")
        .then()
            .spec(responseStatusEJson(200))
            .extract()
            .as(LoginResponse.class);

        System.out.println("Mensagem da API: " + respostaLogin.getMessage());

        // Armazena o token na variável local da instância
        this.token = respostaLogin.getAuthorization().replace("Bearer ", "");
    }

    @Test(
            priority = 1,
            description = "Deve cadastrar um produto aleatório na base de dados",
            groups = {"produto", "sucesso"}
    )
    public void cadastrarProduto() {
        produtoId = requestAuth(this.token) // Passa o token local da classe
            .body(produtoCriado)
        .when()
            .post("/produtos")
        .then()
            .log().all()
            .spec(responseComSchema(201, "schemas/produto/cadastrar-produto-schema.json"))
            .body("message", equalTo(MSG_CADASTRO_SUCESSO))
            .body("_id", notNullValue())
            .extract()
            .path("_id");
    }

    @Test(
            priority = 2,
            description = "NÃO deve cadastrar um produto já existente na base de dados",
            dependsOnMethods = "cadastrarProduto",
            groups = {"produto", "exceção"}
    )
    public void cadastrarProdutoExistente() {
        requestAuth(this.token)
            .body(produtoCriado)
        .when()
            .post("/produtos")
        .then()
            .log().all()
            .spec(responseComSchema(400, "schemas/produto/cadastrar-produto-cadastrado-schema.json"))
            .body("message", equalTo(MSG_PRODUTO_EXISTENTE));
    }

    @Test(
            priority = 3,
            description = "Deve listar o produto cadastrado pelo id",
            dependsOnMethods = "cadastrarProduto",
            groups = {"produto", "sucesso"}
    )
    public void listarProdutoPorId() {
        requestAuth(this.token)
            .pathParam("id", produtoId)
        .when()
            .get("/produtos/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/produto/listar-produto-schema.json"))
            .body("_id", equalTo(produtoId))
            .body("nome", equalTo(produtoCriado.getNome()))
            .body("preco", equalTo(produtoCriado.getPreco()))
            .body("descricao", equalTo(produtoCriado.getDescricao()))
            .body("quantidade", equalTo(produtoCriado.getQuantidade()));
    }

    @Test(
            priority = 4,
            description = "Deve pesquisar produto cadastrado pelo nome",
            dependsOnMethods = "cadastrarProduto",
            groups = {"produto", "sucesso"}
    )
    public void pesquisarProdutoPorNome() {
        requestAuth(this.token)
        .when()
            .get("/produtos")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/produto/pesquisar-produto-schema.json"))
            .body("produtos._id", hasItem(produtoId))
            .body("produtos.nome", hasItem(produtoCriado.getNome()))
            .body("produtos.preco", hasItem(produtoCriado.getPreco()))
            .body("produtos.descricao", hasItem(produtoCriado.getDescricao()))
            .body("produtos.quantidade", hasItem(produtoCriado.getQuantidade()));
    }

    @Test(
            priority = 5,
            description = "Deve editar o produto já cadastrado",
            dependsOnMethods = "listarProdutoPorId",
            groups = {"produto", "sucesso"}
    )
    public void editarProduto() {
        Produto produtoEditado = new Produto(
            produtoCriado.getNome() + " - Edição",
            produtoCriado.getPreco() + 50,
            produtoCriado.getDescricao() + " (atualizado)",
            produtoCriado.getQuantidade() + 10
        );

        requestAuth(this.token)
            .pathParam("id", produtoId)
            .body(produtoEditado)
        .when()
            .put("/produtos/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/produto/editar-produto-schema.json"))
            .body("message", equalTo(MSG_REGISTRO_ALTERADO));
    }

    @Test(
            priority = 6,
            description = "Deve excluir o produto cadastrado pelo id",
            dependsOnMethods = "editarProduto",
            groups = {"produto", "sucesso"}
    )
    public void excluirProduto() {
        requestAuth(this.token)
            .pathParam("id", produtoId)
        .when()
            .delete("/produtos/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/produto/excluir-produto-schema.json"))
            .body("message", equalTo(MSG_REGISTRO_EXCLUIDO));
    }

    @Test(
            priority = 7,
            description = "NÃO deve encontrar produto já excluído",
            dependsOnMethods = "excluirProduto",
            groups = {"produto", "exceção"}
    )
    public void listarProdutoExcluido() {
        requestAuth(this.token)
            .pathParam("id", produtoId)
        .when()
            .get("/produtos/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(400, "schemas/produto/listar-produto-excluido-schema.json"))
            .body("message", equalTo(MSG_PRODUTO_NAO_ENCONTRADO));
    }

    @Test(
            priority = 8,
            description = "NÃO deve cadastrar um produto sem token de autenticação",
            groups = {"produto", "exceção"}
    )
    public void cadastrarProdutoSemToken() {
        // Removida a dependência do 'cadastrarProduto' para que este cenário negativo possa rodar sozinho
        requestJson()
            .body(produtoCriado)
        .when()
            .post("/produtos")
        .then()
            .log().all()
            .spec(responseComSchema(401, "schemas/produto/cadastrar-produto-sem-token-schema.json"))
            .body("message", equalTo(MSG_TOKEN_INVALIDO));
    }
}