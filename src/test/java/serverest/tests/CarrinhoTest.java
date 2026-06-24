package serverest.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import serverest.base.BaseTest;
import serverest.model.Carrinho;
import serverest.model.CarrinhoItem;
import serverest.model.Produto;
import serverest.model.Usuario;
import serverest.util.UsuarioHelper;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static serverest.util.Constants.*;

public class CarrinhoTest extends BaseTest {

    private String tokenUsuario;
    private String produtoId;
    private String carrinhoId;

    @BeforeClass(description = "Prepara os dados necessários para a criação do carrinho")
    public void setupCarrinhoTest() {
        // 1. Criar um usuário administrador para poder cadastrar o produto
        String emailAdmin = UsuarioHelper.gerarEmail();
        Usuario usuarioAdmin = new Usuario("Admin Carrinho", emailAdmin, "1234", "true");

        requestJson()
            .body(usuarioAdmin)
        .when()
            .post("/usuarios")
        .then()
            .statusCode(201);
        System.out.println("Cadastrou usuário admin");

        // 2. Logar com o administrador para obter o token administrativo
        Map<String, String> credenciaisAdmin = new HashMap<>();
        credenciaisAdmin.put("email", emailAdmin);
        credenciaisAdmin.put("password", "1234");

        String tokenAdmin = requestJson()
            .body(credenciaisAdmin)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .extract().path("authorization").toString().replace("Bearer ", "");
        System.out.println("Logou no sistema com admin");

        // 3. Cadastrar um produto usando o token do administrador
        String nomeProduto = "Produto_" + UUID.randomUUID().toString().substring(0, 8);
        Produto produtoComEstoque = new Produto(nomeProduto, 470,
                "Produto para Teste de Carrinho", 100);

        produtoId = requestAuth(tokenAdmin)
            .body(produtoComEstoque)
        .when()
            .post("/produtos")
        .then()
            .statusCode(201)
            .extract().path("_id");
        System.out.println("Cadastrou produto");

        // 4. Criar um usuário comum que será o dono do carrinho
        String emailCliente = UsuarioHelper.gerarEmail();
        Usuario usuarioCliente = new Usuario("Cliente Carrinho", emailCliente, "1234", "false");

        requestJson()
            .body(usuarioCliente)
        .when()
            .post("/usuarios")
        .then()
            .statusCode(201);
        System.out.println("Cadastrou usuário comum");

        // 5. Logar com o usuário comum para obter o token que usaremos no teste do carrinho
        Map<String, String> credenciaisCliente = new HashMap<>();
        credenciaisCliente.put("email", emailCliente);
        credenciaisCliente.put("password", "1234");

        tokenUsuario = requestJson()
            .body(credenciaisCliente)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .extract().path("authorization").toString().replace("Bearer ", "");
        System.out.println("Logou no sistema com usuário comum");
    }

    @Test(
            priority = 1,
            description = "Deve cadastrar um carrinho com sucesso para um usuário autenticado",
            groups = {"carrinho", "sucesso"}
    )
    public void cadastrarCarrinhoComSucesso() {
        // Monta a lista de itens usando o produto cadastrado no setup
        List<CarrinhoItem> itens = new ArrayList<>();
        itens.add(new CarrinhoItem(produtoId, 2)); // Comprando 2 unidades

        Carrinho novoCarrinho = new Carrinho(itens);

        carrinhoId = requestAuth(tokenUsuario)
            .body(novoCarrinho)
        .when()
            .post("/carrinhos")
        .then()
            .log().all()
            .spec(responseComSchema(201, "schemas/carrinho/cadastrar-carrinho-schema.json"))
            .body("message", equalTo(MSG_CADASTRO_SUCESSO))
            .extract()
            .path("_id");
    }

    @Test(
            priority = 2,
            description = "NÃO deve cadastrar um carrinho duplicado",
            groups = {"carrinho", "exceção"}
    )
    public void naoDeveCadastrarCarrinhoDuplicado() {
        // Monta a lista de itens usando o produto cadastrado no setup
        List<CarrinhoItem> itens = new ArrayList<>();
        itens.add(new CarrinhoItem(produtoId, 2)); // Comprando 2 unidades

        Carrinho novoCarrinho = new Carrinho(itens);

        carrinhoId = requestAuth(tokenUsuario)
            .body(novoCarrinho)
        .when()
            .post("/carrinhos")
        .then()
            .log().all()
            .spec(responseComSchema(400, "schemas/carrinho/cadastrar-carrinho-duplicado-schema.json"))
            .body("message", equalTo(MSG_CARRINHO_DUPLICADO))
            .extract()
            .path("_id");
    }
}