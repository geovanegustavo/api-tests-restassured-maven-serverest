package serverest.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import serverest.models.CadastrarProdutoResponse;
import serverest.models.Produto;

public class ProdutoHelper {

    public static Produto gerarProdutoAleatorio() {
        String nome = "Produto_" + UUID.randomUUID().toString().substring(0, 8);
        int preco = (int) (Math.random() * 500) + 50; // preço entre 50 e 550
        String descricao = "Descrição aleatória para " + nome;
        int quantidade = (int) (Math.random() * 100) + 1; // quantidade entre 1 e 100

        return new Produto(nome, preco, descricao, quantidade);
    }

    public static Map<String, Object> cadastrarProduto(String token, int preco, int quantidade) {
        String nome = "Produto_" + UUID.randomUUID().toString().substring(0, 8);
        Produto novoProduto = new Produto(nome, preco, "Descrição de teste", quantidade);

        CadastrarProdutoResponse resposta = RestAssured.given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(novoProduto)
        .when()
            .post("/produtos")
        .then()
            .statusCode(201)
            .extract()
            .as(CadastrarProdutoResponse.class);

        Map<String, Object> dados = new HashMap<>();
        dados.put("produto", novoProduto);
        dados.put("id", resposta.getId());
        return dados;
    }
}
