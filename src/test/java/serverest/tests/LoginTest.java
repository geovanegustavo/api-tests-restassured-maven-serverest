package serverest.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import serverest.base.BaseTest;
import serverest.model.Usuario;
import serverest.util.UsuarioHelper;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static serverest.util.Constants.*;

public class LoginTest extends BaseTest {

    private String emailValido;
    private final String senhaValida = "1234";

    @BeforeClass(description = "Cria um utilizador válido prévio para os testes de login")
    public void setupLogin() {
        emailValido = UsuarioHelper.gerarEmail();
        Usuario usuarioAdmin = new Usuario("Admin QA Login", emailValido, senhaValida, "true");

        requestJson()
            .body(usuarioAdmin)
        .when()
            .post("/usuarios")
        .then()
            .statusCode(201);
    }

    @Test(
            priority = 1,
            description = "Deve logar usuário administrador com credenciais válidas",
            groups = {"login", "sucesso"}
    )
    public void realizarLoginUsuarioAdmin() {
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", emailValido);
        credenciais.put("password", senhaValida);

        requestJson()
            .body(credenciais)
        .when()
            .post("/login")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/login/realizar-login-schema.json"))
            .body("message", equalTo(MSG_LOGIN_SUCESSO));
    }

    @Test(
            priority = 2,
            description = "NÃO deve logar usuário com credenciais inválidas",
            groups = {"login", "exceção"}
    )
    public void realizarLoginInvalido() {
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", "email@invalido.com");
        credenciais.put("password", "1234");

        requestJson()
            .body(credenciais)
        .when()
            .post("/login")
        .then()
            .log().all()
            .spec(responseComSchema(401, "schemas/login/realizar-login-errado-schema.json"))
            .body("message", equalTo(MSG_LOGIN_ERRADO));
    }

    @Test(
            priority = 3,
            description = "NÃO deve logar usuário sem e-mail",
            groups = {"login", "exceção"}
    )
    public void realizarLoginSemEmail() {
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", "");
        credenciais.put("password", "1234");

        requestJson()
            .body(credenciais)
        .when()
            .post("/login")
        .then()
            .log().all()
            .spec(responseComSchema(400, "schemas/login/realizar-login-sem-email-schema.json"))
            .body("email", equalTo(MSG_LOGIN_SEM_EMAIL));
    }

    @Test(
            priority = 4,
            description = "NÃO deve logar usuário sem senha",
            groups = {"login", "exceção"}
    )
    public void realizarLoginSemSenha() {
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", emailValido);
        credenciais.put("password", "");

        requestJson()
            .body(credenciais)
        .when()
            .post("/login")
        .then()
            .log().all()
            .spec(responseComSchema(400, "schemas/login/realizar-login-sem-senha-schema.json"))
            .body("password", equalTo(MSG_LOGIN_SEM_SENHA));
    }

    @Test(
            priority = 5,
            description = "NÃO deve logar usuário sem email e senha",
            groups = {"login", "exceção"}
    )
    public void realizarLoginSemEmailSenha() {
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", "");
        credenciais.put("password", "");

        requestJson()
            .body(credenciais)
        .when()
            .post("/login")
        .then()
            .log().all().spec(responseComSchema(400,
                        "schemas/login/realizar-login-sem-email-senha-schema.json"))
            .body("email", equalTo(MSG_LOGIN_SEM_EMAIL))
            .body("password", equalTo(MSG_LOGIN_SEM_SENHA));
    }
}