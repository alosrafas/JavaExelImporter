import java.util.HashMap;
import java.util.Map;

public class Localidad {
    private String nombreLocalidad;
    private String nombreEstado;
    private String nombrePais;

    Map<String, Integer> estadosMap = new HashMap<>();

    Map<String, Integer> localidadesMap = new HashMap<>();

    public String getNombreLocalidad() {
        return nombreLocalidad;
    }

    public void setNombreLocalidad(String nombreLocalidad) {
        this.nombreLocalidad = nombreLocalidad;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public Map<String, Integer> getEstadosMap() {
        return estadosMap;
    }

    public void setEstadosMap(Map<String, Integer> estadosMap) {
        this.estadosMap = estadosMap;
    }

    public Map<String, Integer> getLocalidadesMap() {
        return localidadesMap;
    }

    public void setLocalidadesMap(Map<String, Integer> localidadesMap) {
        this.localidadesMap = localidadesMap;
    }
}
