// Copiado Proyecto 1

public class Token {
    private String valor;
    private String atributo;

    public Token(String valor, String atributo) {
        this.valor = valor;
        this.atributo = atributo;
    }

    public String getValor() {
        return valor;
    }

    public String getTipo() {
        return atributo;
    }

    @Override
    public String toString() {
        return "Valor: " + valor + ", Atributo: " + atributo;
    }
}
