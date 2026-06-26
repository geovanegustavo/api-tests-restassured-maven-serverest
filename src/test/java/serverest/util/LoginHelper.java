package serverest.util;

import io.restassured.RestAssured;
import serverest.models.LoginResponse;

import java.util.HashMap;
import java.util.Map;

public class LoginHelper {

    /**
     * Realiza a autenticação na API do ServeRest e retorna apenas a String limpa do Token.
     */
    public static String logarObterToken(String email, String password) {
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", email);
        credenciais.put("password", password);

        LoginResponse resposta = RestAssured.given()
            .contentType("application/json")
            .body(credenciais)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .extract()
            .as(LoginResponse.class);

        // Remove o prefixo se a API o retornar junto, garantindo a string pura do token
        return resposta.getAuthorization().replace("Bearer ", "");
    }
}