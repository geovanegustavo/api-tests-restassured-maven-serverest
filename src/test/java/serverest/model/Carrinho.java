package serverest.model;

import java.util.List;

public class Carrinho {
    private List<CarrinhoItem> produtos;

    public Carrinho(List<CarrinhoItem> produtos) {
        this.produtos = produtos;
    }

    // Getters e Setters
    public List<CarrinhoItem> getProdutos() { return produtos; }
    public void setProdutos(List<CarrinhoItem> produtos) { this.produtos = produtos; }
}