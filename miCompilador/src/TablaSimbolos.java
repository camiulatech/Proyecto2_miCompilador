import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

// Copiado Proyecto 1

public class TablaSimbolos {
    // Cambiamos HashMap por LinkedHashMap para conservar el orden de inserción
    private Map<String, InformacionSimbolo> tabla;

    public TablaSimbolos() {
        // Inicializamos el LinkedHashMap
        tabla = new LinkedHashMap<>();
    }
    
    public void agregarSimbolo(String identificador, InformacionSimbolo info) {
        tabla.put(identificador, info);
    }

    public InformacionSimbolo obtenerSimbolo(String identificador) {
        return tabla.get(identificador);
    }

    public boolean existeSimbolo(String identificador) {
        return tabla.containsKey(identificador);
    }

    // Método para guardar la tabla de símbolos en un archivo en el orden en que fueron agregados
    public void guardarTablaSimbolos(String nombreArchivo) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo));
        writer.write("Tabla de Simbolos:\n");
        for (Map.Entry<String, InformacionSimbolo> entry : tabla.entrySet()) {
            String id = entry.getKey();
            InformacionSimbolo info = entry.getValue();
            writer.write("Identificador: " + id + ", Informacion: " + info + "\n");
        }
        writer.close();
    }
}
