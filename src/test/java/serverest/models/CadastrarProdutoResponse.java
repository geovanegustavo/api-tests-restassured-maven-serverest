package serverest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CadastrarProdutoResponse {

    private String message;

    @JsonProperty("_id") // Diz ao Jackson para mapear o campo "_id" nesta variável
    private String id;

    // Construtor vazio padrão (obrigatório para bibliotecas de desserialização)
    public CadastrarProdutoResponse() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

}
