package serverest.tests;

import org.testng.annotations.Test;
import serverest.base.BaseTest;
import serverest.model.Usuario;
import serverest.util.UsuarioHelper;

import static org.hamcrest.Matchers.*;
import static serverest.util.IdHelper.gerarIdAleatorio;
import static serverest.util.Constants.*;

public class UsuarioTest extends BaseTest {

    String usuarioId;
    String usuarioComumId;
    String usuarioInexistenteId;

    String email = UsuarioHelper.gerarEmail();
    String emailComum = UsuarioHelper.gerarEmail();

    String senha = "1234";

    Usuario usuarioCriado = new Usuario("Admin QA", email, senha, "true");
    Usuario usuarioComumCriado = new Usuario("Usuario QA",  emailComum, senha, "false");

    @Test(
            priority = 1,
            description = "Deve cadastrar um usuário Administrador com credenciais válidas",
            groups = {"usuario", "sucesso"}
    )
    public void cadastrarUsuarioAdmin() {
        usuarioId = requestJson()
            .body(usuarioCriado)
        .when()
            .post("/usuarios")
        .then()
            .log().all()
            .spec(responseComSchema(201, "schemas/usuario/cadastrar-usuario-schema.json"))
            .body("message", equalTo(MSG_CADASTRO_SUCESSO))
            .body("_id", notNullValue())
            .extract()
            .path("_id");
    }

    @Test(
            priority = 2,
            description = "Deve cadastrar um usuário comum com credenciais válidas",
            groups = {"usuario", "sucesso"}
    )
    public void cadastrarUsuarioComum() {
        usuarioComumId = requestJson()
            .body(usuarioComumCriado)
        .when()
            .post("/usuarios")
        .then()
            .log().all()
            .spec(responseComSchema(201, "schemas/usuario/cadastrar-usuario-schema.json"))
            .body("message", equalTo(MSG_CADASTRO_SUCESSO))
            .body("_id", notNullValue())
            .extract()
            .path("_id");
    }

    @Test(
            priority = 3,
            description = "NÃO deve cadastrar um usuário com email duplicado",
            groups = {"usuario", "exceção"}
    )
    public void cadastrarUsuarioEmailDuplicado() {
        requestJson()
            .body(usuarioComumCriado)
        .when()
            .post("/usuarios")
        .then()
            .log().all()
            .spec(responseComSchema(400, "schemas/usuario/cadastrar-usuario-email-duplicado-schema.json"))
            .body("message", equalTo(MSG_USUARIO_EMAIL_DUPLICADO));
    }

    @Test(
            priority = 4,
            description = "Deve listar o usuário cadastrado pelo id",
            dependsOnMethods = "cadastrarUsuarioAdmin",
            groups = {"usuario", "sucesso"}
    )
    public void listarUsuarioPorId() {
        requestJson()
            .pathParam("id", usuarioId)
        .when()
            .get("/usuarios/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/usuario/listar-usuario-schema.json"))
            .body("_id", equalTo(usuarioId))
            .body("nome", equalTo(usuarioCriado.getNome()))
            .body("email", equalTo(usuarioCriado.getEmail()))
            .body("password", equalTo(usuarioCriado.getPassword()))
            .body("administrador", equalTo(usuarioCriado.getAdministrador()));
    }

    @Test(
            priority = 5,
            description = "Deve pesquisar usuario cadastrado pelo nome",
            dependsOnMethods = "cadastrarUsuarioAdmin",
            groups = {"usuario", "sucesso"}
    )
    public void pesquisarUsuarioPorNome() {
        requestJson()
            .queryParam("nome", usuarioCriado.getNome())
        .when()
            .get("/usuarios")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/usuario/pesquisar-usuario-schema.json"))
            .body("usuarios._id", hasItem(usuarioId))
            .body("usuarios.nome", hasItem(usuarioCriado.getNome()))
            .body("usuarios.email", hasItem(usuarioCriado.getEmail()))
            .body("usuarios.password", hasItem(usuarioCriado.getPassword()))
            .body("usuarios.administrador", hasItem(usuarioCriado.getAdministrador()));
    }

    @Test(
            priority = 6,
            description = "Deve editar o usuário já cadastrado",
            dependsOnMethods = "cadastrarUsuarioComum",
            groups = {"usuario", "sucesso"}
    )
    public void editarUsuario() {
        // cria um novo objeto com dados atualizados
        Usuario usuarioEditado = new Usuario(
            usuarioComumCriado.getNome() + " - Edição",
            usuarioComumCriado.getEmail() + ".br",
            usuarioComumCriado.getPassword() + "5",
            "true"
        );

        requestJson()
            .pathParam("id", usuarioComumId)
            .body(usuarioEditado)
        .when()
            .put("/usuarios/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/usuario/editar-usuario-schema.json"))
            .body("message", equalTo(MSG_REGISTRO_ALTERADO));
    }

    @Test(
            priority = 7,
            description = "Deve cadastrar o usuário inexistente",
            groups = {"usuario", "sucesso"}
    )
    public void editarUsuarioInexistente() {
        // cria um novo objeto com dados novos
        String nomeUsuarioInexistente = UsuarioHelper.gerarUsuario();
        String emailInexistente = UsuarioHelper.gerarEmail();
        Usuario usuarioInexistente = new Usuario(nomeUsuarioInexistente, emailInexistente, senha, "false");

        usuarioInexistenteId = requestJson()
            .pathParam("id", gerarIdAleatorio())
            .body(usuarioInexistente)
        .when()
            .put("/usuarios/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(201, "schemas/usuario/cadastrar-usuario-schema.json"))
            .body("message", equalTo(MSG_CADASTRO_SUCESSO))
            .extract()
            .path("_id");
    }

    @Test(
            priority = 8,
            description = "Deve excluir o usuário cadastrado pelo id",
            dependsOnMethods = "editarUsuarioInexistente",
            groups = {"usuario", "sucesso"}
    )
    public void excluirUsuario() {
        requestJson()
            .pathParam("id", usuarioInexistenteId)
        .when()
            .delete("/usuarios/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/usuario/excluir-usuario-schema.json"))
            .body("message", equalTo(MSG_REGISTRO_EXCLUIDO));
    }

}
