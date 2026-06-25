package serverest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Esta anotação evita erros caso a API mude e envie campos novos que não mapeou
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    // Os nomes das variáveis devem ser IGUAIS às chaves do JSON
    private String message;
    private String authorization;

    // Construtor vazio padrão (obrigatório para bibliotecas de desserialização)
    public LoginResponse() {}

    // Getters e Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAuthorization() { return authorization; }
    public void setAuthorization(String authorization) { this.authorization = authorization; }
}