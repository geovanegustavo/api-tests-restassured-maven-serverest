package serverest.util;

import io.restassured.RestAssured;
import serverest.models.CadastrarCarrinhoResponse;
import serverest.models.Carrinho;
import serverest.models.CarrinhoItem;

import java.util.ArrayList;
import java.util.List;

public class CarrinhoHelper {

    public static Carrinho criarCarrinhoNaMemoria(String produtoId, int quantidade) {
        List<CarrinhoItem> itens = new ArrayList<>();
        itens.add(new CarrinhoItem(produtoId, quantidade));
        return new Carrinho(itens);
    }

    public static CadastrarCarrinhoResponse cadastrarCarrinhoNaApi(String token, String produtoId, int quantidade) {
        Carrinho payloadCarrinho = criarCarrinhoNaMemoria(produtoId, quantidade);

        return RestAssured.given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
            .body(payloadCarrinho)
        .when()
            .post("/carrinhos")
        .then()
            .statusCode(201) // Validação rápida de infraestrutura
            .extract()
            .as(CadastrarCarrinhoResponse.class);
    }
}