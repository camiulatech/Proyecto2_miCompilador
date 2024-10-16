import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FaseLexica {
    private List<Token> tokens = new ArrayList<>();
    private TablaSimbolos tablaSimbolos = new TablaSimbolos();
    private int lineaActual = 1;

    public void analizarArchivo(String archivo) throws IOException {
        BufferedReader leer = new BufferedReader(new FileReader(archivo));
        String linea;

        while ((linea = leer.readLine()) != null) {
            analizarLinea(linea);
            lineaActual++;
        }
        leer.close();
    }

    private String obtenerValorDeLaLinea(String linea) {
        String[] partes = linea.split("=");
        if (partes.length == 2) {
            return partes[1].trim().replace(";", ""); // Se elimina el ;
        }
        return "";
    }

    private void analizarLinea(String linea) {
        char[] caracteres = linea.toCharArray();
        int i = 0;

        while (i < caracteres.length) {
            char actual = caracteres[i];

            if (Character.isWhitespace(actual)) {
                i++;
                continue;
            }

            int inicial = i;

            if (Character.isLetter(actual)) {
                StringBuilder identificador = new StringBuilder();
                boolean contieneNumero = false; 
                boolean contieneMayuscula = false; 
                int contieneMasCaracteres = 0; 

                while (i < caracteres.length && (Character.isLetterOrDigit(caracteres[i]))) {
                    if (Character.isUpperCase(caracteres[i])) {
                        contieneMayuscula = true;
                    }
                    if (Character.isDigit(caracteres[i])) {
                        contieneNumero = true;
                    }
                    identificador.append(caracteres[i]);
                    i++;
                }

                contieneMasCaracteres = i - inicial;

                if (contieneMasCaracteres > 12) {
                    System.out.println("Error [Fase Lexica]: La linea " + lineaActual + " contiene un identificador no valido, mayor a 12 letras: " + identificador.toString());
                }
                if (contieneNumero) {
                    System.out.println("Error [Fase Lexica]: La linea " + lineaActual + " contiene un identificador no valido, contiene un digito: " + identificador.toString());
                }
                if (contieneMayuscula) {
                    System.out.println("Error [Fase Lexica]: La linea " + lineaActual + " contiene un identificador no valido, contiene una mayuscula: " + identificador.toString());
                }

                if (contieneMasCaracteres <= 12 && !contieneNumero && !contieneMayuscula) {
                    String id = identificador.toString();

                    if (!tablaSimbolos.existeSimbolo(id)) {
                        String valor = obtenerValorDeLaLinea(linea);
                        InformacionSimbolo info = new InformacionSimbolo(lineaActual, valor);
                        tablaSimbolos.agregarSimbolo(id, info);
                    }

                    tokens.add(new Token(id, "IDENTIFICADOR"));
                }
                continue;
            }

            if (Character.isDigit(actual)) {
                StringBuilder numero = new StringBuilder();
                while (i < caracteres.length && Character.isDigit(caracteres[i])) {
                    numero.append(caracteres[i]);
                    i++;
                }
                tokens.add(new Token(numero.toString(), "NUMERO"));
                continue;
            }

            if (actual == '=') {
                tokens.add(new Token("=", "ASIGNACION"));
                i++;
                continue;
            } else if (actual == '+') {
                tokens.add(new Token("+", "SUMA"));
                i++;
                continue;
            } else if (actual == '-') {
                tokens.add(new Token("-", "RESTA"));
                i++;
                continue;
            } else if (actual == '*') {
                tokens.add(new Token("*", "MULTIPLICACION"));
                i++;
                continue;
            } else if (actual == '/') {
                tokens.add(new Token("/", "DIVISION"));
                i++;
                continue;
            } else if (actual == '(') {
                tokens.add(new Token("(", "PARENTESIS_IZQ"));
                i++;
                continue;
            } else if (actual == ')') {
                tokens.add(new Token(")", "PARENTESIS_DER"));
                i++;
                continue;
            } else if (actual == ';') {
                tokens.add(new Token(";", "PUNTO_COMA"));
                i++;
                continue;
            }

            System.out.println("Error [Fase Lexica]: La linea " + lineaActual + " contiene un lexema no reconocido: " + actual);
            i++;
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void imprimirTokens() {
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    public TablaSimbolos getTablaSimbolos() {
        return tablaSimbolos;
    }


}
