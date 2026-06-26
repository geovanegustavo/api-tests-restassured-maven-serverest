package serverest.tests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import serverest.base.BaseTest;
import serverest.model.*;
import serverest.util.ProdutoHelper;
import serverest.util.UsuarioHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static serverest.util.Constants.MSG_CADASTRO_SUCESSO;
import static serverest.util.Constants.MSG_CARRINHO_DUPLICADO;

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
        this.tokenAdmin = realizarLogin(usuarioAdmin.getEmail(), usuarioAdmin.getPassword());

        // 2. Cadastra o produto necessário para os testes
        Map<String, Object> dadosProduto = ProdutoHelper.cadastrarProduto(this.tokenAdmin, 100, 200);
        this.produtoId = (String) dadosProduto.get("id");

        // 3. Cria e autentica o usuário comum (dono do carrinho)
        Map<String, Object> dadosUsuarioComum = UsuarioHelper.cadastrarUsuario("false");
        Usuario usuarioComum = (Usuario) dadosUsuarioComum.get("usuario");
        this.usuarioComumId = (String) dadosUsuarioComum.get("id");
        this.tokenUsuarioComum = realizarLogin(usuarioComum.getEmail(), usuarioComum.getPassword());
    }

    @AfterClass(description = "Limpa a massa de dados gerada para o bloco de testes")
    public void tearDownCarrinhoTest() {
        if (this.produtoId != null) {
            deletarProdutoSeExistir(this.produtoId, this.tokenAdmin);
        }
        if (this.usuarioComumId != null) {
            deletarUsuarioSeExistir(this.usuarioComumId);
        }
        if (this.adminId != null) {
            deletarUsuarioSeExistir(this.adminId);
        }
        System.out.println("Massa de dados do carrinho limpa com sucesso!");
    }

    @Test(description = "Deve cadastrar um carrinho com sucesso")
    public void cadastrarCarrinhoComSucesso() {
        Carrinho novoCarrinho = criarCarrinho();

        try {
            CadastrarCarrinhoResponse resposta = requestAuth(tokenUsuarioComum)
                .body(novoCarrinho)
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

    @Test(description = "NÃO deve cadastrar um carrinho duplicado")
    public void naoDeveCadastrarCarrinhoDuplicado() {
        Carrinho carrinhoInicial = criarCarrinho();

        // Cadastra o primeiro carrinho de forma direta para preparar o cenário de erro
        requestAuth(tokenUsuarioComum)
            .body(carrinhoInicial)
        .when()
            .post("/carrinhos")
        .then()
            .statusCode(201);

        Carrinho novoCarrinho = criarCarrinho();

        try {
            requestAuth(tokenUsuarioComum)
                .body(novoCarrinho)
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

    /**
     * MÉTODOS AUXILIARES
     */

    // Isola o fluxo repetitivo de login
    private String realizarLogin(String email, String password) {
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", email);
        credenciais.put("password", password);

        LoginResponse resposta = requestJson()
            .body(credenciais)
        .when()
            .post("/login")
        .then()
            .spec(responseStatusEJson(200))
            .extract()
            .as(LoginResponse.class);

        return resposta.getAuthorization().replace("Bearer ", "");
    }

    // Isola a montagem da lista de itens do carrinho
    private Carrinho criarCarrinho() {
        List<CarrinhoItem> itens = new ArrayList<>();
        itens.add(new CarrinhoItem(produtoId, 2));
        return new Carrinho(itens);
    }
}