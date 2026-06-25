package serverest.base;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static serverest.util.Constants.MSG_REGISTRO_EXCLUIDO;

public abstract class BaseTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://serverest.dev";
    }

    // --- REUTILIZAÇÃO DE REQUESTS ---

    /**
     * Valida o tipo de conteúdo
     */
    protected RequestSpecification requestJson() {
        return given()
                .contentType("application/json")
                .log().all();
    }

    /**
     * Valida o tipo de conteúdo e passa o bearer token
     */
    protected RequestSpecification requestAuth(String token) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .log().all();
    }

    // --- REUTILIZAÇÃO DE RESPONSES ---

    /**
     * Valida o status HTTP esperado e garante que o Content-Type seja JSON.
     */
    protected ResponseSpecification responseStatusEJson(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectContentType("application/json")
                .build();
    }

    /**
     * Valida o status HTTP e também valida o contrato (JSON Schema).
     */
    protected ResponseSpecification responseComSchema(int statusCode, String schemaPath) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectContentType("application/json")
                .expectBody(matchesJsonSchemaInClasspath(schemaPath))
                .build();
    }

    // --- REUTILIZAÇÃO DE CÓDIGO ---

    protected void deletarUsuarioSeExistir(String id) {
        if (id != null) {
            System.out.println("🧹 Limpando base de dados... Excluindo usuário ID: " + id);
            requestJson()
                    .pathParam("id", id)
                    .when()
                    .delete("/usuarios/{id}")
                    .then()
                    .spec(responseComSchema(200, "schemas/usuario/excluir-usuario-schema.json"));
        }
    }

    protected void deletarProdutoSeExistir(String id, String token) {
        if (id != null) {
            System.out.println("🧹 Limpando base de dados... Excluindo produto ID: " + id);
            requestAuth(token)
                    .pathParam("id", id)
                    .when()
                    .delete("/produtos/{id}")
                    .then()
                    .log().all()
                    .spec(responseComSchema(200, "schemas/produto/excluir-produto-schema.json"))
                    .body("message", equalTo(MSG_REGISTRO_EXCLUIDO));
        }
    }
}