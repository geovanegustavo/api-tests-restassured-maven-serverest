package serverest.tests;

import org.testng.annotations.Test;
import serverest.base.BaseTest;
import serverest.model.CadastrarUsuarioResponse;
import serverest.model.Usuario;
import serverest.util.UsuarioHelper;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static serverest.util.Constants.*;
import static serverest.util.IdHelper.gerarIdAleatorio;

public class UsuarioTest extends BaseTest {

    @Test(description = "Deve cadastrar um usuário Administrador")
    public void cadastrarUsuarioAdmin() {
        // 1. ARRANJO: Cria os dados dinâmicos do teste
        String usuarioDinamico = serverest.util.UsuarioHelper.gerarUsuario();
        String emailDinamico = serverest.util.UsuarioHelper.gerarEmail();
        Usuario novoUsuario = new Usuario(usuarioDinamico, emailDinamico, "1234", "true");

        String idCadastrado = null;

        try {
            // 2. AÇÃO: Realiza o cadastro
            CadastrarUsuarioResponse resposta = requestJson()
                .body(novoUsuario)
            .when()
                .post("/usuarios")
            .then()
                .log().all()
                .spec(responseComSchema(201, "schemas/usuario/cadastrar-usuario-schema.json"))
                .body("message", equalTo(MSG_CADASTRO_SUCESSO))
                .body("_id", notNullValue())
                .extract()
                .as(CadastrarUsuarioResponse.class);

            idCadastrado = resposta.getId();

            // 3. ASSERÇÃO: Valida o que precisa do teste
            assertThat(idCadastrado).isNotBlank();

        } finally {
            // 4. LIMPEZA: Reutiliza o método da BaseTest
            deletarUsuarioSeExistir(idCadastrado);
        }
    }

    @Test(description = "Deve cadastrar um usuário comum")
    public void cadastrarUsuarioComum() {
        // 1. ARRANJO: Cria os dados dinâmicos do teste
        String usuarioDinamico = serverest.util.UsuarioHelper.gerarUsuario();
        String emailDinamico = serverest.util.UsuarioHelper.gerarEmail();
        Usuario novoUsuario = new Usuario(usuarioDinamico, emailDinamico, "1234", "false");

        String idCadastrado = null;

        try {
            // 2. AÇÃO: Realiza o cadastro
            CadastrarUsuarioResponse resposta = requestJson()
                .body(novoUsuario)
            .when()
                .post("/usuarios")
            .then()
                .log().all()
                .spec(responseComSchema(201, "schemas/usuario/cadastrar-usuario-schema.json"))
                .body("message", equalTo(MSG_CADASTRO_SUCESSO))
                .body("_id", notNullValue())
                .extract()
                .as(CadastrarUsuarioResponse.class);

            idCadastrado = resposta.getId();

            // 3. ASSERÇÃO: Valida o que precisa do teste
            assertThat(idCadastrado).isNotBlank();

        } finally {
            // 4. LIMPEZA: Reutiliza o método da BaseTest
            deletarUsuarioSeExistir(idCadastrado);
        }
    }

    @Test(description = "Deve editar usuário")
    public void editarUsuario() {
        // 1. ARRANJO: Cria os dados dinâmicos do teste
        Map<String, Object> dadosMassa = UsuarioHelper.cadastrarUsuario("true");
        String idCadastrado = (String) dadosMassa.get("id");
        Usuario novoUsuario = (Usuario) dadosMassa.get("usuario");

        Usuario usuarioEditado = new Usuario(
                novoUsuario.getNome() + " - Edição",
                novoUsuario.getEmail() + ".br",
                novoUsuario.getPassword() + "5",
                "true");

        try {
            // 2. AÇÃO: Realiza a edição
            requestJson()
                .pathParam("id", idCadastrado)
                .body(usuarioEditado)
            .when()
                .put("/usuarios/{id}")
            .then()
                .log().all()
                .spec(responseComSchema(200, "schemas/usuario/editar-usuario-schema.json"))
                .body("message", equalTo(MSG_REGISTRO_ALTERADO));

            // 3. ASSERÇÃO: Valida o que precisa do teste
            assertThat(idCadastrado).isNotBlank();

        } finally {
            // 4. LIMPEZA: Reutiliza o método da BaseTest
            deletarUsuarioSeExistir(idCadastrado);
        }
    }

    @Test(description = "Deve editar usuário inexistente, cadastrando novo usuário")
    public void editarUsuarioInexistente() {
        // 1. ARRANJO: Cria os dados dinâmicos do teste
        String nomeUsuarioInexistente = UsuarioHelper.gerarUsuario();
        String emailInexistente = UsuarioHelper.gerarEmail();
        Usuario usuarioInexistente = new Usuario(nomeUsuarioInexistente, emailInexistente, "1234", "false");

        String idCadastrado = null;

        try {
            // 2. AÇÃO: Realiza a edição
            idCadastrado = requestJson()
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

            // 3. ASSERÇÃO: Valida o que precisa do teste
            assertThat(idCadastrado).isNotBlank();

        } finally {
            // 4. LIMPEZA: Reutiliza o método da BaseTest
            deletarUsuarioSeExistir(idCadastrado);
        }
    }

    @Test(description = "Deve excluir usuário")
    public void excluirUsuario() {
        // 1. ARRANJO: Cria os dados dinâmicos do teste
        Map<String, Object> dadosMassa = UsuarioHelper.cadastrarUsuario("true");
        String idCadastrado = (String) dadosMassa.get("id");

        // 2. AÇÃO: Realiza a exclusão
        requestJson()
            .pathParam("id", idCadastrado)
        .when()
            .delete("/usuarios/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/usuario/excluir-usuario-schema.json"))
            .body("message", equalTo(MSG_REGISTRO_EXCLUIDO));

        // 3. ASSERÇÃO: Valida o que precisa do teste
        assertThat(idCadastrado).isNotBlank();
    }

    @Test(description = "NÃO deve cadastrar um usuário com email duplicado")
    public void cadastrarUsuarioEmailDuplicado() {
        // 1. ARRANJO: Cria os dados dinâmicos do teste
        Map<String, Object> dadosMassa = UsuarioHelper.cadastrarUsuario("true");
        String idCadastrado = (String) dadosMassa.get("id");
        Usuario novoUsuario = (Usuario) dadosMassa.get("usuario");

        try {
            // 2. AÇÃO: Realiza o cadastro
            requestJson()
                .body(novoUsuario)
            .when()
                .post("/usuarios")
            .then()
                .log().all()
                .spec(responseComSchema(400, "schemas/usuario/cadastrar-usuario-email-duplicado-schema.json"))
                .body("message", equalTo(MSG_USUARIO_EMAIL_DUPLICADO));

            // 3. ASSERÇÃO: Valida o que precisa do teste
            assertThat(idCadastrado).isNotBlank();

        } finally {
            // 4. LIMPEZA: Reutiliza o método da BaseTest
            deletarUsuarioSeExistir(idCadastrado);
        }
    }

    @Test(description = "Deve pesquisar usuário por Id")
    public void pesquisarUsuarioPorId() {
        // 1. ARRANJO: Cria os dados dinâmicos do teste
        Map<String, Object> dadosMassa = UsuarioHelper.cadastrarUsuario("true");
        String idCadastrado = (String) dadosMassa.get("id");
        Usuario novoUsuario = (Usuario) dadosMassa.get("usuario");

        try {
            // 2. AÇÃO: Realiza o cadastro
            requestJson()
                .pathParam("id", idCadastrado)
            .when()
                .get("/usuarios/{id}")
            .then()
                .log().all()
                .spec(responseComSchema(200, "schemas/usuario/listar-usuario-schema.json"))
                .body("_id", equalTo(idCadastrado))
                .body("nome", equalTo(novoUsuario.getNome()))
                .body("email", equalTo(novoUsuario.getEmail()))
                .body("password", equalTo(novoUsuario.getPassword()))
                .body("administrador", equalTo(novoUsuario.getAdministrador()));

            // 3. ASSERÇÃO: Valida o que precisa do teste
            assertThat(idCadastrado).isNotBlank();

        } finally {
            // 4. LIMPEZA: Reutiliza o método da BaseTest
            deletarUsuarioSeExistir(idCadastrado);
        }
    }

    @Test(description = "Deve pesquisar usuário por Nome")
    public void pesquisarUsuarioPorNome() {
        // 1. ARRANJO: Cria os dados dinâmicos do teste
        Map<String, Object> dadosMassa = UsuarioHelper.cadastrarUsuario("true");
        String idCadastrado = (String) dadosMassa.get("id");
        Usuario novoUsuario = (Usuario) dadosMassa.get("usuario");

        try {
            // 2. AÇÃO: Realiza o cadastro
            requestJson()
                .queryParam("nome", novoUsuario.getNome())
            .when()
                .get("/usuarios")
            .then()
                .log().all()
                .spec(responseComSchema(200, "schemas/usuario/pesquisar-usuario-schema.json"))
                .body("usuarios._id", hasItem(idCadastrado))
                .body("usuarios.nome", hasItem(novoUsuario.getNome()))
                .body("usuarios.email", hasItem(novoUsuario.getEmail()))
                .body("usuarios.password", hasItem(novoUsuario.getPassword()))
                .body("usuarios.administrador", hasItem(novoUsuario.getAdministrador()));

            // 3. ASSERÇÃO: Valida o que precisa do teste
            assertThat(idCadastrado).isNotBlank();

        } finally {
            // 4. LIMPEZA: Reutiliza o método da BaseTest
            deletarUsuarioSeExistir(idCadastrado);
        }
    }

    @Test(description = "NÃO deve encontrar usuário pelo id")
    public void usuarioNaoEncontrado() {
        requestJson()
            .pathParam("id", gerarIdAleatorio())
        .when()
            .get("/usuarios/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(400, "schemas/usuario/nao-encontrar-usuario-schema.json"))
            .body("message", equalTo(MSG_USUARIO_NAO_ENCONTRADO));
    }

    @Test(description = "NÃO deve excluir usuário não existente")
    public void usuarioNaoExcluido() {
        requestJson()
            .pathParam("id", gerarIdAleatorio())
        .when()
            .delete("/usuarios/{id}")
        .then()
            .log().all()
            .spec(responseComSchema(200, "schemas/usuario/nao-excluir-usuario-schema.json"))
            .body("message", equalTo(MSG_NENHUM_REGISTRO_EXCLUIDO));
    }

}
