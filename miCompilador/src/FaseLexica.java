import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FaseLexica {
    private List<Token> tokens = new ArrayList<>();
    private TablaSimbolos tablaSimbolos = new TablaSimbolos();
    private int lineaActual = 1;
    private boolean esPrimeroEnLinea = true; // Variable para controlar si es el primer token en la línea

    public void analizarArchivo(String archivo) throws IOException {
        BufferedReader leer = new BufferedReader(new FileReader(archivo));
        String linea;

        while ((linea = leer.readLine()) != null) {
            esPrimeroEnLinea = true; // Al inicio de cada línea, consideramos que el primer token es el primero en la línea
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
                    System.out.println("Error [Fase Lexica]: La línea " + lineaActual + " contiene un identificador no válido, mayor a 12 letras: " + identificador.toString());
                }
                if (contieneNumero) {
                    System.out.println("Error [Fase Lexica]: La línea " + lineaActual + " contiene un identificador no válido, contiene un dígito: " + identificador.toString());
                }
                if (contieneMayuscula) {
                    System.out.println("Error [Fase Lexica]: La línea " + lineaActual + " contiene un identificador no válido, contiene una mayúscula: " + identificador.toString());
                }

                String id = identificador.toString();

                // Verificar si es una palabra clave reservada
                if (id.equals("if")) {
                    tokens.add(new Token("if", "IF"));
                } else if (id.equals("else")) {
                    tokens.add(new Token("else", "ELSE"));
                } else if (id.equals("while")) {
                    tokens.add(new Token("while", "WHILE"));
                } else if (id.equals("for")) {
                    tokens.add(new Token("for", "FOR"));
                } else if (id.equals("print")) {
                    tokens.add(new Token("print", "PRINT"));
                } else {
                    // Si no es palabra clave, validar como identificador
                    tokens.add(new Token(id, "IDENTIFICADOR"));
                    // Solo agregamos a la tabla de símbolos si es el primer token o está precedido por un ';'
                    if (esPrimeroEnLinea || (tokens.size() > 1 && tokens.get(tokens.size() - 2).getTipo().equals("PUNTO_COMA"))) {
                        if (!tablaSimbolos.existeSimbolo(id)) {
                            String valor = obtenerValorDeLaLinea(linea);
                            InformacionSimbolo info = new InformacionSimbolo(lineaActual, valor);
                            tablaSimbolos.agregarSimbolo(id, info);
                        }
                    }
                }
                esPrimeroEnLinea = false; // Después del primer token, ya no es el primero en la línea
                continue;
            }

            // El resto del código para números y operadores se mantiene igual
            if (Character.isDigit(actual)) {
                StringBuilder numero = new StringBuilder();
                while (i < caracteres.length && Character.isDigit(caracteres[i])) {
                    numero.append(caracteres[i]);
                    i++;
                }
                tokens.add(new Token(numero.toString(), "NUMERO"));
                esPrimeroEnLinea = false;
                continue;
            }

            // Detección de operadores relacionales
        if (actual == '<') {
            if (i + 1 < caracteres.length && caracteres[i + 1] == '=') {
                tokens.add(new Token("<=", "MENOR_IGUAL"));
                i += 2; // Saltar el siguiente carácter '='
            } else if (i + 1 < caracteres.length && caracteres[i + 1] == '>') {
                tokens.add(new Token("<>", "DIFERENTE"));
                i += 2; // Saltar el siguiente carácter '>'
            } else {
                tokens.add(new Token("<", "MENOR"));
                i++;
            }
            esPrimeroEnLinea = false;
            continue;
        } else if (actual == '>') {
            if (i + 1 < caracteres.length && caracteres[i + 1] == '=') {
                tokens.add(new Token(">=", "MAYOR_IGUAL"));
                i += 2; // Saltar el siguiente carácter '='
            } else {
                tokens.add(new Token(">", "MAYOR"));
                i++;
            }
            esPrimeroEnLinea = false;
            continue;
        } else if (actual == '=') {
            if (i + 1 < caracteres.length && caracteres[i + 1] == '=') {
                tokens.add(new Token("==", "IGUALDAD"));
                i += 2; // Saltar el siguiente carácter '='
            } else {
                tokens.add(new Token("=", "ASIGNACION"));
                i++;
            }
            esPrimeroEnLinea = false;
            continue;
        }

            if (actual == '=') {
                tokens.add(new Token("=", "ASIGNACION"));
                i++;
                esPrimeroEnLinea = false;
                continue;
            } else if (actual == '+') {
                tokens.add(new Token("+", "SUMA"));
                i++;
                esPrimeroEnLinea = false;
                continue;
            } else if (actual == '-') {
                tokens.add(new Token("-", "RESTA"));
                i++;
                esPrimeroEnLinea = false;
                continue;
            } else if (actual == '*') {
                tokens.add(new Token("*", "MULTIPLICACION"));
                i++;
                esPrimeroEnLinea = false;
                continue;
            } else if (actual == '/') {
                tokens.add(new Token("/", "DIVISION"));
                i++;
                esPrimeroEnLinea = false;
                continue;
            } else if (actual == '(') {
                tokens.add(new Token("(", "PARENTESIS_IZQ"));
                i++;
                esPrimeroEnLinea = false;
                continue;
            } else if (actual == ')') {
                tokens.add(new Token(")", "PARENTESIS_DER"));
                i++;
                esPrimeroEnLinea = false;
                continue;
            } else if (actual == ';') {
                tokens.add(new Token(";", "PUNTO_COMA"));
                i++;
                esPrimeroEnLinea = true; // Después de un punto y coma, el siguiente token es considerado como el primero
                continue;
            } else if (actual == '}') {
                tokens.add(new Token("}", "LLAVE_DER"));
                i++;
                esPrimeroEnLinea = false;
                continue;
            } else if (actual == '{') {
                tokens.add(new Token("{", "LLAVE_IZQ"));
                i++;
                esPrimeroEnLinea = false;
                continue;
            }

            System.out.println("Error [Fase Lexica]: La línea " + lineaActual + " contiene un lexema no reconocido: " + actual);
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
