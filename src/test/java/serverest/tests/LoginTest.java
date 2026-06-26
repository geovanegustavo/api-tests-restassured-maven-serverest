package serverest.tests;

import org.testng.annotations.Test;
import serverest.base.BaseTest;
import serverest.model.Usuario;
import serverest.util.UsuarioHelper;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static serverest.util.Constants.*;
import static serverest.util.Constants.MSG_LOGIN_SEM_SENHA;

public class LoginTest extends BaseTest {

    /**
     * CENÁRIOS DE SUCESSO
     */

    @Test(description = "Deve logar usuário administrador com credenciais válidas")
    public void realizarLoginUsuarioAdmin() {
        // 1. Cria um usuário administrador dinâmico para este contexto de testes
        Map<String, Object> dadosMassa = UsuarioHelper.cadastrarUsuario("true");
        Usuario usuarioAdmin = (Usuario) dadosMassa.get("usuario");

        // Guarda o ID do usuário para o desmonte posterior
        String usuarioId = (String) dadosMassa.get("id");

        try {
            Map<String, String> credenciais = new HashMap<>();
            credenciais.put("email", usuarioAdmin.getEmail());
            credenciais.put("password", usuarioAdmin.getPassword());

            requestJson()
                .body(credenciais)
            .when()
                .post("/login")
            .then()
                .log().all()
                .spec(responseComSchema(200, "schemas/login/realizar-login-schema.json"))
                .body("message", equalTo(MSG_LOGIN_SUCESSO));
        } finally {
            deletarUsuarioSeExistir(usuarioId);
        }

    }

    /**
     * CENÁRIOS DE EXCEÇÃO
     */

    @Test(description = "NÃO deve logar usuário com credenciais inválidas")
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

    @Test(description = "NÃO deve logar usuário sem e-mail")
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

    @Test(description = "NÃO deve logar usuário sem senha")
    public void realizarLoginSemSenha() {
        Map<String, String> credenciais = new HashMap<>();
        credenciais.put("email", "email-valido@qa.com");
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

    @Test(description = "NÃO deve logar usuário sem email e senha")
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
