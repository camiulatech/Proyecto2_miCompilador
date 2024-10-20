import java.util.List;

public class FaseSintactica {
    private List<Token> tokens;
    private int indiceActual;
    private int lineaActual;
    private boolean existe_error;

    public FaseSintactica(List<Token> tokens) {
        this.tokens = tokens;
        this.indiceActual = 0;
        this.lineaActual = 0; // Comienza en 1 para contar las líneas de código
        this.existe_error = false;
    }

    public void analizar() throws Exception {
        try {
            while (indiceActual < tokens.size() && !existe_error) {
                programa();
            }
        } catch (IndexOutOfBoundsException e) {
            existe_error = true;
            System.out.println("Error: Indice fuera de límites.");
        } catch (Exception e) {
            existe_error = true;
            System.out.println("Error [Fase Sintactica]: La línea " + lineaActual + e.getMessage());
        }

        if (!existe_error) {
            System.out.println("Se logró completar la fase sintáctica correctamente.");
        }
    }

    // programa -> declaración programa
    private void programa() throws Exception {
        while (indiceActual < tokens.size()) {
            lineaActual++;
            declaracion();
        }
    }

    // declaración -> identificador = numero ; | control_flujo | impresion ;
    private void declaracion() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            siguienteToken(); // Toma el identificador
            if (tokens.get(indiceActual).getTipo().equals("ASIGNACION")) {
                siguienteToken(); // Toma el signo '='
                if (tokens.get(indiceActual).getTipo().equals("NUMERO")) {
                    siguienteToken(); // Toma el número
                    if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                        siguienteToken(); // Toma ';'
                    } else {
                        throw new Exception(" se esperaba un punto y coma.");
                    }
                } else {
                    throw new Exception(" se esperaba un número después de '='.");
                }
            } else if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                siguienteToken(); // Toma ';' sin asignación
            } else {
                throw new Exception(" se esperaba un operador de asignación o punto y coma.");
            }
        } else if (tokens.get(indiceActual).getTipo().equals("IF") || tokens.get(indiceActual).getTipo().equals("WHILE") || tokens.get(indiceActual).getTipo().equals("FOR")) {
            control_flujo(); // Maneja control de flujo
        } else if (tokens.get(indiceActual).getTipo().equals("PRINT")) {
            impresion(); // Maneja impresión
        } else {
            throw new Exception(" declaración no válida.");
        }
    }

    // control_flujo -> if_stmt | while_stmt | for_stmt
    private void control_flujo() throws Exception {
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
        siguienteToken(); // Toma 'if'
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // Toma '('
            expresion(); // Analiza expresión
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // Toma ')'
                if (tokens.get(indiceActual).getTipo().equals("LLAVE_IZQ")) {
                    siguienteToken(); // Toma '{'
                    declaraciones();
                    if (tokens.get(indiceActual).getTipo().equals("LLAVE_DER")) {
                        siguienteToken(); // Toma '}'
                        if (tokens.get(indiceActual).getTipo().equals("ELSE")) {
                            siguienteToken(); // Toma 'else'
                            if (tokens.get(indiceActual).getTipo().equals("LLAVE_IZQ")) {
                                siguienteToken(); // Toma '{'
                                declaraciones();
                                if (tokens.get(indiceActual).getTipo().equals("LLAVE_DER")) {
                                    siguienteToken(); // Toma '}'
                                } else {
                                    throw new Exception(" se esperaba '}'.");
                                }
                            } else {
                                throw new Exception(" se esperaba '{' después de 'else'.");
                            }
                        }
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

    private void while_stmt() throws Exception {
        siguienteToken(); // Toma 'while'
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // Toma '('
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

    private void for_stmt() throws Exception {
        siguienteToken(); // Toma 'for'
        if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // Toma '('
            expresion(); // Analiza expresión
            if (tokens.get(indiceActual).getTipo().equals("PUNTO_Y_COMA")) {
                siguienteToken(); // Toma ';'
                expresion(); // Analiza expresión
                if (tokens.get(indiceActual).getTipo().equals("PUNTO_Y_COMA")) {
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
                    if (tokens.get(indiceActual).getTipo().equals("PUNTO_COMA")) {
                        siguienteToken(); // Toma ';'
                    } else {
                        throw new Exception(" se esperaba un punto y coma.");
                    }
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

    // expresion -> identificador = numero | termino operador_aditivo termino
    private void expresion() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            siguienteToken(); // Toma el identificador
            if (tokens.get(indiceActual).getTipo().equals("ASIGNACION")) {
                siguienteToken(); // Toma '='
                if (tokens.get(indiceActual).getTipo().equals("NUMERO")) {
                    siguienteToken(); // Toma el número
                } else {
                    throw new Exception(" se esperaba un número.");
                }
            }
        } else {
            termino(); // Analiza término
            operador_aditivo();
            termino(); // Analiza término
        }
    }

    // termino -> factor operador_multiplicativo factor
    private void termino() throws Exception {
        factor(); // Analiza factor
        operador_multiplicativo();
        factor(); // Analiza factor
    }

    // factor -> identificador | numero | ( expresion )
    private void factor() throws Exception {
        if (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            siguienteToken(); // Toma el identificador
        } else if (tokens.get(indiceActual).getTipo().equals("NUMERO")) {
            siguienteToken(); // Toma el número
        } else if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_IZQ")) {
            siguienteToken(); // Toma '('
            expresion(); // Analiza expresión
            if (tokens.get(indiceActual).getTipo().equals("PARENTESIS_DER")) {
                siguienteToken(); // Toma ')'
            } else {
                throw new Exception(" se esperaba ')'.");
            }
        } else {
            throw new Exception(" factor no válido.");
        }
    }

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

    private void siguienteToken() {
        if (indiceActual < tokens.size()) {
            indiceActual++;
        }
    }

    private void declaraciones() throws Exception {
        while (tokens.get(indiceActual).getTipo().equals("IDENTIFICADOR")) {
            declaracion();
        }
    }
}