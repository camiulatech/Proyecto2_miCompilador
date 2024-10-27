import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class FaseSintactica {
    private List<Token> tokens;
    private int indiceActual;
    private int lineaActual;
    private boolean existe_error;
    private List<Integer> erroresTablaSimbolos = new ArrayList<>();
    private boolean eliminar;
    private boolean llave_der;

    public FaseSintactica(List<Token> tokens) {
        this.tokens = tokens;
        this.indiceActual = 0;
        this.lineaActual = 0; // Comienza en 1 para contar las líneas de código
        this.existe_error = false;
        this.eliminar = true;
    }

    public void analizar() throws Exception {
        try {
            while (indiceActual < tokens.size() && !existe_error) {
                programa();
            }
        } catch (IndexOutOfBoundsException e) {
            existe_error = true;
            System.out.println("Error [Fase Sintactica]: La linea " + lineaActual + " contiene un error en su gramatica, falta token ;");
            if(eliminar){
                erroresTablaSimbolos.add(lineaActual+1);
                eliminarErroresTablaSimbolos("tablaDeSimbolos.txt");
            }

        } catch (Exception e) {
            existe_error = true;
            if(eliminar){
                erroresTablaSimbolos.add(lineaActual+1);
                eliminarErroresTablaSimbolos("tablaDeSimbolos.txt");
            }
            System.out.println("Error [Fase Sintactica]: La línea " + lineaActual + e.getMessage());
        }

        if (!existe_error) {
            System.out.println("Se logró completar la fase sintáctica correctamente.");
        }
    }

    // programa -> declaración programa
    private void programa() throws Exception {
        declaraciones();
    }

    // declaración -> expresion | control_flujo | impresion
    private void declaracion() throws Exception {
        // Identificamos el tipo de declaración basado en el primer token
        String tipoToken = tokens.get(indiceActual).getTipo();
        System.out.println(tipoToken);

        if (tipoToken.equals("IDENTIFICADOR") || tipoToken.equals("NUMERO") || tipoToken.equals("PARENTESIS_IZQ") || tipoToken.equals("RESTA")) {
            // Si es un identificador, número, paréntesis o un operador menos, tratamos la declaración como una expresión
            expresion();
        } 
        else if (tipoToken.equals("IF") || tipoToken.equals("WHILE") || tipoToken.equals("FOR")) {
            // Control de flujo: if, while, o for
            control_flujo();
        } 
        else if (tipoToken.equals("PRINT")) {
            // Impresión: print
            impresion();
        } 
        else if(llave_der == true && tipoToken.equals("LLAVE_DER")){
            llave_der = false;
            siguienteToken();
        }
        else {
            throw new Exception("PRUEBA Declaración no válida en la línea " + lineaActual);
        }
    }

    // control_flujo -> if_stmt | while_stmt | for_stmt
    private void control_flujo() throws Exception {
        eliminar = false;
        if (tokens.get(indiceActual).getTipo().equals("IF")) {
            if_stmt();
        } else if (tokens.get(indiceActual).getTipo().equals("WHILE")) {
            while_stmt();
        } else if (tokens.get(indiceActual).getTipo().equals("FOR")) {
            for_stmt();
        } else {
            throw new Exception(" control de flujo no válido.");
        }
    }

    private void if_stmt() throws Exception {
        boolean validacion_else = false;
        siguienteToken(); // Toma 'if'
    
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // Toma '('
            expresion(); // Analiza la expresión
    
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // Toma ')'
    
                if (tokens.get(indiceActual).getTipo().equals("LLAVE_IZQ")) {
                    siguienteToken(); // Toma '{'
                    declaraciones(); // Analiza las declaraciones dentro del if
    
                    if (tokens.get(indiceActual).getTipo().equals("LLAVE_DER")) {
                        siguienteToken(); // Toma '}'
    
                        // Verificar si hay un bloque else
                        if (indiceActual < tokens.size() && tokens.get(indiceActual).getTipo().equals("ELSE")) {
                            validacion_else = true;
                            manejarElse(); // Maneja el bloque else si existe
                        }
    
                        // Solo verificamos el punto y coma después de manejar 'else' (si existe)
                        if (indiceActual < tokens.size() && tokens.get(indiceActual).getTipo().equals("PUNTO_COMA") && !validacion_else) {
                            siguienteToken(); // Toma ';' después del bloque if
                        }
                    } else {
                        throw new Exception("Se esperaba '}' en el bloque if.");
                    }
                } else {
                    throw new Exception("Se esperaba '{' después de '(' en el if.");
                }
            } else {
                throw new Exception("Se esperaba ')' en la condición del if.");
            }
        } else {
            throw new Exception("Se esperaba '(' después de 'if'.");
        }
    }
    
    private void manejarElse() throws Exception {
        siguienteToken(); // Toma 'else'
    
        if (tokens.get(indiceActual).getTipo().equals("LLAVE_IZQ")) {
            siguienteToken(); // Toma '{'
            declaraciones(); // Analiza las declaraciones dentro del else
    
            if (tokens.get(indiceActual).getTipo().equals("LLAVE_DER")) {
                siguienteToken(); // Toma '}'
    
                if (indiceActual < tokens.size() && tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                    siguienteToken(); // Toma ';' después del bloque else
                } else {
                    throw new Exception("Se esperaba ';' después del bloque else.");
                }
            } else {
                throw new Exception("Se esperaba '}' en el bloque else.");
            }
        } else {
            throw new Exception("Se esperaba '{' después de 'else'.");
        }
    }
    

    private void while_stmt() throws Exception {
        siguienteToken(); // Toma 'while'
    
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // Toma '('
            expresion(); // Analiza expresión
    
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // Toma ')'
    
                if (tokens.get(indiceActual).getTipo().equals("LLAVE_IZQ")) {
                    llave_der = true;
                    siguienteToken(); // Toma '{'
                    declaraciones(); // Analiza las declaraciones dentro del while
                    

                    /*  ------------------------------------------------
    
                    if (tokens.get(indiceActual).getTipo().equals("LLAVE_DER")) {
                        siguienteToken(); // Toma '}'
    
                        // Verificación del punto y coma después del bloque while
                        if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                            siguienteToken(); // Toma ';'
                        } else {
                            throw new Exception(" se esperaba ';' después del bloque while.");
                        }
                    } else {
                        throw new Exception(" se esperaba '}' después del bloque while.");
                    }

                    */
                } else {
                    throw new Exception(" se esperaba '{' después de la condición del while.");
                }
            } else {
                throw new Exception(" se esperaba ')' después de la condición del while.");
            }
        } else {
            throw new Exception(" se esperaba '(' después de 'while'.");
        }
    }
    

    private void for_stmt() throws Exception {
        siguienteToken(); // Toma 'for'
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // Toma '('
            expresion(); // Analiza expresión
            if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                siguienteToken(); // Toma ';'
                expresion(); // Analiza expresión
                if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                    siguienteToken(); // Toma ';'
                    expresion(); // Analiza expresión
                    if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                        siguienteToken(); // Toma ')'
                        if (tokens.get(indiceActual).getTipo().equals("LLAVE_IZQ")) {
                            siguienteToken(); // Toma '{'
                            declaraciones();
                            if (tokens.get(indiceActual).getTipo().equals("LLAVE_DER")) {
                                siguienteToken(); // Toma '}'
                            } else {
                                throw new Exception(" se esperaba '}'.");
                            }
                        } else {
                            throw new Exception(" se esperaba '{' después de '('.");
                        }
                    } else {
                        throw new Exception(" se esperaba ')'.");
                    }
                }
            }
        }
    }

    private void impresion() throws Exception {
        siguienteToken(); // Toma 'print'
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // Toma '('
            if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
                siguienteToken(); // Toma el identificador
                if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                    siguienteToken(); // Toma ')'
                } else {
                    throw new Exception(" se esperaba ')'.");
                }
            } else {
                throw new Exception(" se esperaba un identificador.");
            }
        } else {
            throw new Exception(" se esperaba '('.");
        }
    }

    // Expresion -> identificador = expresion | identificador | numero | (expresion)
    //              | expresion operador_aditivo expresion
    //              | expresion operador_multiplicativo expresion
    //              | expresion operador_relacional expresion
    //              | - expresion
    
    private void expresion() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            siguienteToken(); // Toma el identificador
            if (indiceActual < tokens.size() && tokens.get(indiceActual).getTipo().equals("ASIGNACION")) {
                siguienteToken(); // Toma el operador '='
                expresion(); // Llama a expresion recursivamente para el lado derecho
            }
        } 
        else if (tokens.get(indiceActual).getTipo().equals("NUMERO")) {
            siguienteToken(); // Toma el número
        } 
        else if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_ABIERTO")) {
            siguienteToken(); // Toma '('
            expresion(); // Analiza la expresión dentro de los paréntesis
            if (indiceActual < tokens.size() && tokens.get(indiceActual).getTipo().equals("PARENTESIS_CERRADO")) {
                siguienteToken(); // Toma ')'
            } else {
                throw new Exception("Se esperaba un paréntesis cerrado en la línea " + lineaActual);
            }
        } 
        else if (tokens.get(indiceActual).getTipo().equals("RESTA")) { // Para -expresion
            siguienteToken(); // Toma el operador '-'
            expresion(); // Analiza la expresión después del operador unario
        } 
        else {
            throw new Exception("Expresión no válida en la línea " + lineaActual);
        }
        
        // Verifica si hay una operación binaria después de la primera parte de la expresión
        if (indiceActual < tokens.size() && esOperadorBinario(tokens.get(indiceActual).getTipo())) {
            siguienteToken(); // Toma el operador binario
            expresion(); // Analiza la expresión del lado derecho
        }
    }

    // Verifica si el token actual es un operador binario válido
    private boolean esOperadorBinario(String tipoToken) {
        return tipoToken.equals("SUMA") || tipoToken.equals("RESTA") || 
            tipoToken.equals("DIVISION") || tipoToken.equals("MULTIPLICACION") ||
            tipoToken.equals("MENOR_IGUAL") || tipoToken.equals("DIFERENTE") || 
            tipoToken.equals("MENOR") || tipoToken.equals("MAYOR_IGUAL") || 
            tipoToken.equals("MAYOR") || tipoToken.equals("IGUALDAD");
    }

    /* 
    // operador_aditivo -> + | -
    private void operador_aditivo() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("SUMA") || tokens.get(indiceActual).getTipo().equals("RESTA")) {
            siguienteToken(); // Toma el operador
        }
    }

    // operador_multiplicativo -> * | /
    private void operador_multiplicativo() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("MULTIPLICACION") || tokens.get(indiceActual).getTipo().equals("DIVISION")) {
            siguienteToken(); // Toma el operador
        }
    }
        */

    private void siguienteToken() {
        if (indiceActual < tokens.size()) {
            indiceActual++;
        }
    }

    // declaraciones -> declaración ; declaraciones | ε
    private void declaraciones() throws Exception {
        // Mientras haya tokens, intenta analizar declaraciones
        while (indiceActual < tokens.size()) {
            declaracion(); // Analiza la declaración actual
            
            // Después de cada declaración, se espera un punto y coma
            if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                siguienteToken(); // Toma el punto y coma
                lineaActual++;
            } else {
                throw new Exception(" DECLRACIONES se esperaba un punto y coma después de la declaración.");
            }
            
            // Llamada recursiva para seguir analizando más declaraciones
            // Si no hay más declaraciones, se manejará la producción ε y terminará
        }
    }


    private void eliminarErroresTablaSimbolos(String archivoTablaSimbolos) throws IOException {
        List<String> lineasValidas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivoTablaSimbolos))) {
            String linea;
            int numeroLinea = 1;

            while ((linea = br.readLine()) != null) {
                if (!erroresTablaSimbolos.contains(numeroLinea)) {
                    lineasValidas.add(linea);
                }
                numeroLinea++;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoTablaSimbolos))) {
            for (String linea : lineasValidas) {
                bw.write(linea);
                bw.newLine();
            }
        }
    }
    
}