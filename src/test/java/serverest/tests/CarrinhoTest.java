package serverest.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import serverest.base.BaseTest;
import serverest.model.*;
import serverest.util.CarrinhoHelper; // <-- Import do Helper adicionado
import serverest.util.LoginHelper;
import serverest.util.ProdutoHelper;
import serverest.util.UsuarioHelper;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static serverest.util.Constants.*;
import static serverest.util.IdHelper.gerarIdAleatorio;

public class CarrinhoTest extends BaseTest {

    private String tokenAdmin;
    private String adminId;
    private String produtoId;
    private String usuarioComumId;
    private String tokenUsuarioComum;

    @BeforeClass(description = "Prepara os dados necessários para a criação do carrinho")
    public void setupCarrinhoTest() {
        // 1. Cria e autentica o usuário administrador
        Map<String, Object> dadosMassaAdmin = UsuarioHelper.cadastrarUsuario("true");
        Usuario usuarioAdmin = (Usuario) dadosMassaAdmin.get("usuario");
        this.adminId = (String) dadosMassaAdmin.get("id");

        // CENTRALIZADO
        this.tokenAdmin = LoginHelper.logarObterToken(usuarioAdmin.getEmail(), usuarioAdmin.getPassword());

        // 2. Cadastra o produto necessário para os testes
        Map<String, Object> dadosProduto = ProdutoHelper.cadastrarProduto(this.tokenAdmin, 100, 200);
        this.produtoId = (String) dadosProduto.get("id");

        // 3. Cria e autentica o usuário comum (dono do carrinho)
        Map<String, Object> dadosUsuarioComum = UsuarioHelper.cadastrarUsuario("false");
        Usuario usuarioComum = (Usuario) dadosUsuarioComum.get("usuario");
        this.usuarioComumId = (String) dadosUsuarioComum.get("id");

        // CENTRALIZADO
        this.tokenUsuarioComum = LoginHelper.logarObterToken(usuarioComum.getEmail(), usuarioComum.getPassword());
    }

    @AfterClass(description = "Limpa a massa de dados gerada para o bloco de testes")
    public void tearDownCarrinhoTest() {
        if (this.produtoId != null) {
            System.out.println("Deletando produto...");
            deletarProdutoSeExistir(this.produtoId, this.tokenAdmin);
        }
        if (this.usuarioComumId != null) {
            System.out.println("Deletando usuário comum...");
            deletarUsuarioSeExistir(this.usuarioComumId);
        }
        if (this.adminId != null) {
            System.out.println("Deletando usuário admin...");
            deletarUsuarioSeExistir(this.adminId);
        }
        System.out.println("Massa de dados do carrinho limpa com sucesso!");
    }

    /**
     * CENÁRIOS DE SUCESSO
     */

    @Test(description = "Deve cadastrar um carrinho com sucesso")
    public void cadastrarCarrinhoComSucesso() {
        // Usando o helper para criar o objeto na memória
        Carrinho payloadCarrinho = CarrinhoHelper.criarCarrinhoNaMemoria(produtoId, 2);

        try {
            CadastrarCarrinhoResponse resposta = requestAuth(tokenUsuarioComum)
                .body(payloadCarrinho)
            .when()
                .post("/carrinhos")
            .then()
                .log().all()
                .spec(responseComSchema(201, "schemas/carrinho/cadastrar-carrinho-schema.json"))
                .body("message", equalTo(MSG_CADASTRO_SUCESSO))
                .extract()
                .as(CadastrarCarrinhoResponse.class);

            assertThat(resposta.getId()).isNotBlank();

        } finally {
            deletarCarrinhoSeExistir(this.tokenUsuarioComum);
        }
    }

    /**
     * CENÁRIOS DE EXCEÇÃO
     */

    @Test(description = "NÃO deve cadastrar um carrinho duplicado")
    public void naoDeveCadastrarCarrinhoDuplicado() {
        // Cadastra o primeiro carrinho usando a chamada real do Helper da API
        CarrinhoHelper.cadastrarCarrinhoNaApi(tokenUsuarioComum, produtoId, 2);

        // Cria o payload duplicado usando o Helper na memória
        Carrinho payloadCarrinho = CarrinhoHelper.criarCarrinhoNaMemoria(produtoId, 2);

        try {
            requestAuth(tokenUsuarioComum)
                .body(payloadCarrinho)
            .when()
                .post("/carrinhos")
            .then()
                .log().all()
                .spec(responseComSchema(400, "schemas/carrinho/cadastrar-carrinho-duplicado-schema.json"))
                .body("message", equalTo(MSG_CARRINHO_DUPLICADO));
        } finally {
            deletarCarrinhoSeExistir(this.tokenUsuarioComum);
        }
    }

    @Test(description = "Deve pesquisar um carrinho por Id")
    public void pesquisarCarrinhoPorId() {
        // Cadastra o carrinho através da API usando o Helper
        CadastrarCarrinhoResponse novoCarrinho = CarrinhoHelper.cadastrarCarrinhoNaApi(this.tokenUsuarioComum, this.produtoId, 2);
        String idCadastrado = novoCarrinho.getId();

        try {
            requestAuth(this.tokenUsuarioComum)
                .pathParam("id", idCadastrado)
            .when()
                .get("/carrinhos/{id}")
            .then()
                .log().all()
                .spec(responseComSchema(200, "schemas/carrinho/listar-carrinho-schema.json"))
                .body("_id", equalTo(idCadastrado))
                .body("idUsuario", equalTo(this.usuarioComumId))
                .body("quantidadeTotal", equalTo(2))
                .body("produtos.idProduto", hasItem(this.produtoId))
                .body("produtos.quantidade", hasItem(2));
        } finally {
            deletarCarrinhoSeExistir(this.tokenUsuarioComum);
        }
    }

    @Test(description = "Não deve pesquisar um carrinho por Id inexistente")
    public void pesquisarCarrinhoPorIdInexistente() {

        String idInexistente = gerarIdAleatorio();

        requestAuth(this.tokenUsuarioComum)
            .pathParam("id", idInexistente)
        .when()
            .get("/carrinhos/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(400, "schemas/carrinho/pesquisar-carrinho-inexistente-schema.json"))
            .body("message", equalTo(MSG_CARRINHO_NAO_ENCONTRADO));
    }

}