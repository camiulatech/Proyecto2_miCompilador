import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FaseSintactica {
    private List<Token> tokens;
    private int indiceActual;
    private int lineaActual;
    private boolean existe_error;
    List<Integer> errores_tablaSimbolos = new ArrayList<>();

    public FaseSintactica(List<Token> tokens) {
        this.tokens = tokens;
        this.indiceActual = 0; // indice en la lista de tokens
        this.lineaActual = 0; // Linea actual para mensajes de error
        this.existe_error = false; // Indica si hay errores
    }

    // Método principal que inicia el análisis sintáctico
    public void analizar() throws Exception {
        try {
            programa(); // Comienza con la regla principal de la gramática
        } catch (Exception e) {
            existe_error = true;
            errores_tablaSimbolos.add(lineaActual + 1);
            eliminarErroresTablaSimbolos("tablaDeSimbolos.txt");
            System.out.println("Error [Fase Sintactica]: La línea " + (lineaActual) + e.getMessage());
        }

        if (!existe_error) {
            System.out.println("Se completó la fase sintáctica correctamente.");
        }
    }

    // programa -> declaraciones
    private void programa() throws Exception {
        declaraciones();
    }

    // declaraciones -> declaración declaraciones | ε
    private void declaraciones() throws Exception {
        while (indiceActual < tokens.size() && !tokens.get(indiceActual).getTipo().equals("LLAVE_DER")) {
            declaracion();
        }
    }

    // declaración -> asignación | estructura_control | impresión
    private void declaracion() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            asignacion();
        } else if (tokens.get(indiceActual).getTipo().equals("IF") || tokens.get(indiceActual).getTipo().equals("WHILE") || tokens.get(indiceActual).getTipo().equals("FOR")) {
            estructuraControl();
        } else if (tokens.get(indiceActual).getTipo().equals("PRINT")) {
            impresion();
        } else {
            throw new Exception(" se esperaba una declaración.");
        }
    }

    // asignación -> identificador '=' expresión ';'
    private void asignacion() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            siguienteToken(); // toma el identificador
            if (tokens.get(indiceActual).getTipo().equals("ASIGNACION")) {
                siguienteToken(); // toma '='
                expresion();
                if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                    siguienteToken(); // toma ';'
                } else {
                    throw new Exception(" se esperaba ';' después de la asignación.");
                }
            } else {
                throw new Exception(" se esperaba '=' para la asignación.");
            }
        }
    }

    // estructura_control -> if | while | for
    private void estructuraControl() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("IF")) {
            condicionalIf();
        } else if (tokens.get(indiceActual).getTipo().equals("WHILE")) {
            bucleWhile();
        } else if (tokens.get(indiceActual).getTipo().equals("FOR")) {
            bucleFor();
        }
    }

    // if -> 'if' '(' expresión ')' '{' declaraciones '}'
    private void condicionalIf() throws Exception {
        siguienteToken(); // toma 'if'
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // toma '('
            expresion();
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // toma ')'
                if (tokens.get(indiceActual).getTipo().equals("LLAVE_IZQ")) {
                    siguienteToken(); // toma '{'
                    declaraciones();
                    if (tokens.get(indiceActual).getTipo().equals("LLAVE_DER")) {
                        siguienteToken(); // toma '}'
                    } else {
                        throw new Exception(" se esperaba '}' después del bloque de if.");
                    }
                } else {
                    throw new Exception(" se esperaba '{' para el bloque de if.");
                }
            } else {
                throw new Exception(" se esperaba ')' después de la condición.");
            }
        } else {
            throw new Exception(" se esperaba '(' después de 'if'.");
        }
    }

    // while -> 'while' '(' expresión ')' '{' declaraciones '}'
    private void bucleWhile() throws Exception {
        siguienteToken(); // toma 'while'
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // toma '('
            expresion();
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // toma ')'
                if (tokens.get(indiceActual).getTipo().equals("LLAVE_IZQ")) {
                    siguienteToken(); // toma '{'
                    declaraciones();
                    if (tokens.get(indiceActual).getTipo().equals("LLAVE_DER")) {
                        siguienteToken(); // toma '}'
                    } else {
                        throw new Exception(" se esperaba '}' después del bloque de while.");
                    }
                } else {
                    throw new Exception(" se esperaba '{' para el bloque de while.");
                }
            } else {
                throw new Exception(" se esperaba ')' después de la condición.");
            }
        } else {
            throw new Exception(" se esperaba '(' después de 'while'.");
        }
    }

    // for -> 'for' '(' asignación ';' expresión ';' asignación ')' '{' declaraciones '}'
    private void bucleFor() throws Exception {
        siguienteToken(); // toma 'for'
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // toma '('
            asignacion();
            if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                siguienteToken(); // toma ';'
                expresion();
                if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                    siguienteToken(); // toma ';'
                    asignacion();
                    if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                        siguienteToken(); // toma ')'
                        if (tokens.get(indiceActual).getTipo().equals("LLAVE_IZQ")) {
                            siguienteToken(); // toma '{'
                            declaraciones();
                            if (tokens.get(indiceActual).getTipo().equals("LLAVE_DER")) {
                                siguienteToken(); // toma '}'
                            } else {
                                throw new Exception(" se esperaba '}' después del bloque de for.");
                            }
                        } else {
                            throw new Exception(" se esperaba '{' para el bloque de for.");
                        }
                    } else {
                        throw new Exception(" se esperaba ')' después de la expresión.");
                    }
                } else {
                    throw new Exception(" se esperaba ';' después de la expresión.");
                }
            } else {
                throw new Exception(" se esperaba ';' después de la asignación.");
            }
        } else {
            throw new Exception(" se esperaba '(' después de 'for'.");
        }
    }

    // impresión -> 'print' '(' expresión ')' ';'
    private void impresion() throws Exception {
        siguienteToken(); // toma 'print'
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // toma '('
            expresion();
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // toma ')'
                if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                    siguienteToken(); // toma ';'
                } else {
                    throw new Exception(" se esperaba ';' después de la impresión.");
                }
            } else {
                throw new Exception(" se esperaba ')' después de la expresión.");
            }
        } else {
            throw new Exception(" se esperaba '(' después de 'print'.");
        }
    }

    // expresión -> término { ('+' | '-') término }
    private void expresion() throws Exception {
        termino();
        while (tokens.get(indiceActual).getTipo().equals("MAS") || tokens.get(indiceActual).getTipo().equals("MENOS")) {
            siguienteToken(); // toma '+' o '-'
            termino();
        }
    }

    // término -> factor { ('*' | '/') factor }
    private void termino() throws Exception {
        factor();
        while (tokens.get(indiceActual).getTipo().equals("MULTIPLICACION") || tokens.get(indiceActual).getTipo().equals("DIVISION")) {
            siguienteToken(); // toma '*' o '/'
            factor();
        }
    }

    // factor -> número | identificador | '(' expresión ')'
    private void factor() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("NUMERO")) {
            siguienteToken(); // toma un número
        } else if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            siguienteToken(); // toma un identificador
        } else if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // toma '('
            expresion();
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // toma ')'
            } else {
                throw new Exception(" se esperaba ')' después de la expresión.");
            }
        } else {
            throw new Exception(" se esperaba un número, identificador o '('.");
        }
    }

    // Método para avanzar al siguiente token
    private void siguienteToken() {
        if (indiceActual < tokens.size() - 1) {
            indiceActual++;
            //lineaActual = tokens.get(indiceActual).getLinea();
        }
    }

    // Método para eliminar errores en la tabla de símbolos
    private void eliminarErroresTablaSimbolos(String rutaArchivo) {
        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo));
             BufferedWriter escritor = new BufferedWriter(new FileWriter("tablaDeSimbolosA.txt"))) {

            String linea;
            int contadorLineas = 1;

            while ((linea = lector.readLine()) != null) {
                if (!errores_tablaSimbolos.contains(contadorLineas)) {
                    escritor.write(linea);
                    escritor.newLine();
                }
                contadorLineas++;
            }
        } catch (IOException e) {
            System.out.println("Error al depurar la tabla de símbolos: " + e.getMessage());
        }
    }
}
