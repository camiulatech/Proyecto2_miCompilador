public class Main {
    public static void main(String[] args) {
        String archivo = args[0]; 
        
        FaseLexica analizadorLexico = new FaseLexica();

        try {
            System.out.println("FASE LEXICA:");

            // Inicio de Fase Lexica
            analizadorLexico.analizarArchivo(archivo);

            analizadorLexico.imprimirTokens();

            // Guardar la tabla de símbolos en un archivo
            String archivoSalida = "tablaDeSimbolos.txt";
            analizadorLexico.getTablaSimbolos().guardarTablaSimbolos(archivoSalida);
            System.out.println("\n" + "Tabla de simbolos guardada en: " + archivoSalida);
            
        }
        catch (Exception e) {
            System.out.println("Error en el main: " + e.getMessage());
        }
    }
}
