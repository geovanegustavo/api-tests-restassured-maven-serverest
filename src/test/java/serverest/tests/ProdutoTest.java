package serverest.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import serverest.base.BaseTest;
import serverest.models.*;
import serverest.util.LoginHelper;
import serverest.util.ProdutoHelper;
import serverest.util.UsuarioHelper;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static serverest.util.Constants.*;

public class ProdutoTest extends BaseTest {

    private String token;
    private String usuarioId; // <-- Guardar o ID do admin

    @BeforeClass(description = "Garante que o teste de produtos tenha um token válido e independente")
    public void setupProdutoTest() {
        // 1. Cria um usuário administrador dinâmico para este contexto de testes
        Map<String, Object> dadosMassa = UsuarioHelper.cadastrarUsuario("true");
        Usuario usuarioAdmin = (Usuario) dadosMassa.get("usuario");
        this.usuarioId = (String) dadosMassa.get("id");

        // 2. Efetua o login e pega o token
        this.token = LoginHelper.logarObterToken(usuarioAdmin.getEmail(), usuarioAdmin.getPassword());
    }

    @AfterClass(description = "Limpa o usuário administrador criado para o contexto do teste")
    public void tearDownProdutoTest() {
        deletarUsuarioSeExistir(this.usuarioId);
    }

    /**
     * CENÁRIOS DE SUCESSO
     */

    @Test(description = "Deve cadastrar um produto na base de dados")
    public void cadastrarProduto() {
        Produto novoProduto = ProdutoHelper.gerarProdutoAleatorio();
        String idCadastrado = null;

        try {
            CadastrarProdutoResponse resposta = requestAuth(this.token) // Passa o token local da classe
                .body(novoProduto)
            .when()
                .post("/produtos")
            .then()
                .log().all()
                .spec(responseComSchema(201, "schemas/produto/cadastrar-produto-schema.json"))
                .body("message", equalTo(MSG_CADASTRO_SUCESSO))
                .body("_id", notNullValue())
                .extract()
                .as(CadastrarProdutoResponse.class);

            idCadastrado = resposta.getId();
            assertThat(idCadastrado).isNotBlank();

        } finally {
            deletarProdutoSeExistir(idCadastrado, this.token);
        }

    }

    @Test(description = "Deve editar um produto na base de dados")
    public void editarProduto() {
        Map<String, Object> dadosMassa = ProdutoHelper.cadastrarProduto(this.token, 100, 200);
        String idCadastrado = (String) dadosMassa.get("id");
        Produto novoProduto = (Produto) dadosMassa.get("produto");

        novoProduto.setNome("Nome Editado " + java.util.UUID.randomUUID().toString().substring(0, 5));
        novoProduto.setPreco(130);
        novoProduto.setDescricao("Descrição Editada");
        novoProduto.setQuantidade(100);

        try {
            requestAuth(this.token)
                .pathParam("id", idCadastrado)
                .body(novoProduto)
            .when()
                .put("/produtos/{id}")
            .then()
                .log().all()
                .spec(responseComSchema(200, "schemas/produto/editar-produto-schema.json"))
                .body("message", equalTo(MSG_REGISTRO_ALTERADO));

            assertThat(idCadastrado).isNotBlank();

        } finally {
            deletarProdutoSeExistir(idCadastrado, this.token);
        }
    }

    @Test(description = "Deve excluir um produto na base de dados")
    public void excluirProduto() {
        Map<String, Object> dadosMassa = ProdutoHelper.cadastrarProduto(this.token, 100, 200);
        String idCadastrado = (String) dadosMassa.get("id");

        requestAuth(this.token)
            .pathParam("id", idCadastrado)
        .when()
            .delete("/produtos/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/produto/excluir-produto-schema.json"))
            .body("message", equalTo(MSG_REGISTRO_EXCLUIDO));

        assertThat(idCadastrado).isNotBlank();
    }

    @Test(description = "Deve pesquisar um produto na base de dados por Id")
    public void pesquisarProdutoPorId() {
        Map<String, Object> dadosMassa = ProdutoHelper.cadastrarProduto(this.token, 100, 200);
        String idCadastrado = (String) dadosMassa.get("id");
        Produto novoProduto = (Produto) dadosMassa.get("produto");

        try {
            requestAuth(this.token)
                .pathParam("id", idCadastrado)
            .when()
                .get("/produtos/{id}")
            .then()
                .log().all()
                .spec(responseComSchema(200, "schemas/produto/listar-produto-schema.json"))
                .body("_id", equalTo(idCadastrado))
                .body("nome", equalTo(novoProduto.getNome()))
                .body("preco", equalTo(novoProduto.getPreco()))
                .body("descricao", equalTo(novoProduto.getDescricao()))
                .body("quantidade", equalTo(novoProduto.getQuantidade()));

            assertThat(idCadastrado).isNotBlank();

        } finally {
            deletarProdutoSeExistir(idCadastrado, this.token);
        }
    }

    @Test(description = "Deve pesquisar um produto na base de dados por Nome")
    public void pesquisarProdutoPorNome() {
        Map<String, Object> dadosMassa = ProdutoHelper.cadastrarProduto(this.token, 100, 200);
        String idCadastrado = (String) dadosMassa.get("id");
        Produto novoProduto = (Produto) dadosMassa.get("produto");

        try {
            requestAuth(this.token)
            .when()
                .get("/produtos")
            .then()
                .log().all()
                .spec(responseComSchema(200, "schemas/produto/pesquisar-produto-schema.json"))
                .body("produtos._id", hasItem(idCadastrado))
                .body("produtos.nome", hasItem(novoProduto.getNome()))
                .body("produtos.preco", hasItem(novoProduto.getPreco()))
                .body("produtos.descricao", hasItem(novoProduto.getDescricao()))
                .body("produtos.quantidade", hasItem(novoProduto.getQuantidade()));

            assertThat(idCadastrado).isNotBlank();

        } finally {
            deletarProdutoSeExistir(idCadastrado, this.token);
        }

    }

    /**
     * CENÁRIOS DE EXCEÇÃO
     */

    @Test(description = "NÃO deve cadastrar um produto já existente na base de dados")
    public void cadastrarProdutoExistente() {
        Map<String, Object> dadosMassa = ProdutoHelper.cadastrarProduto(this.token, 100, 200);
        String idCadastrado = (String) dadosMassa.get("id");
        Produto novoProduto = (Produto) dadosMassa.get("produto");

        try {
            requestAuth(this.token)
                .body(novoProduto)
            .when()
                .post("/produtos")
            .then()
                .log().all()
                .spec(responseComSchema(400, "schemas/produto/cadastrar-produto-cadastrado-schema.json"))
                .body("message", equalTo(MSG_PRODUTO_EXISTENTE));

            assertThat(idCadastrado).isNotBlank();

        } finally {
            deletarProdutoSeExistir(idCadastrado, this.token);
        }

    }

    @Test(description = "NÃO deve pesquisar um produto já excluído da base de dados")
    public void pesquisarProdutoExcluido() {
        Map<String, Object> dadosMassa = ProdutoHelper.cadastrarProduto(this.token, 100, 200);
        String idCadastrado = (String) dadosMassa.get("id");

        // Excluir o produto
        deletarProdutoSeExistir(idCadastrado, this.token);

        /*requestAuth(this.token)
            .pathParam("id", idCadastrado)
        .when()
            .delete("/produtos/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/produto/excluir-produto-schema.json"))
            .body("message", equalTo(MSG_REGISTRO_EXCLUIDO));*/

        // (Tentar...) pesquisar o produto excluído
        requestAuth(this.token)
            .pathParam("id", idCadastrado)
        .when()
            .get("/produtos/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(400, "schemas/produto/listar-produto-excluido-schema.json"))
            .body("message", equalTo(MSG_PRODUTO_NAO_ENCONTRADO));

        assertThat(idCadastrado).isNotBlank();
    }

    @Test(description = "NÃO deve cadastrar um produto na base de dados sem token")
    public void cadastrarProdutoSemToken() {
        Produto novoProduto = ProdutoHelper.gerarProdutoAleatorio();

        requestJson()
            .body(novoProduto)
        .when()
            .post("/produtos")
        .then()
            .log().all()
            .spec(responseComSchema(401, "schemas/produto/cadastrar-produto-sem-token-schema.json"))
            .body("message", equalTo(MSG_TOKEN_INVALIDO));
    }

}
