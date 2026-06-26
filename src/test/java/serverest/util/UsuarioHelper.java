package serverest.util;

import io.restassured.RestAssured;
import serverest.models.CadastrarUsuarioResponse;
import serverest.models.Usuario;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UsuarioHelper {
    public static String gerarUsuario() {
        return "user_" + UUID.randomUUID();
    }

    public static String gerarEmail() {
        return "user_" + UUID.randomUUID() + "@qa.com";
    }

    public static Map<String, Object> cadastrarUsuario(String isAdmin) {
        String nome = gerarUsuario();
        String email = gerarEmail();
        Usuario novoUsuario = new Usuario(nome, email, "1234", isAdmin);

        CadastrarUsuarioResponse resposta = RestAssured.given()
            .contentType("application/json")
            .body(novoUsuario)
        .when()
            .post("/usuarios")
        .then()
            .statusCode(201)
            .extract()
            .as(CadastrarUsuarioResponse.class);

        Map<String, Object> dados = new HashMap<>();
        dados.put("usuario", novoUsuario);
        dados.put("id", resposta.getId());
        return dados;
    }

}
