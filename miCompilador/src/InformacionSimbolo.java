// Copiado Proyecto 1

public class InformacionSimbolo {
    private int lineaDeclaracion;  
    private String valor;          

    public InformacionSimbolo(int lineaDeclaracion, String valor) {
        this.lineaDeclaracion = lineaDeclaracion;
        this.valor = valor;
    }

    public int getLineaDeclaracion() {
        return lineaDeclaracion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Linea: " + lineaDeclaracion + ", Valor: " + valor;
    }
}
