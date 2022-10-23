package models;

public class Localidad {
    private Integer localidadId;
    private Integer estadoId;
    private Integer paisId;

    private String nombreLocalidad;

    public Localidad(Integer localidadId, Integer estadoId, Integer paisId, String nombreLocalidad) {
        this.localidadId = localidadId;
        this.estadoId = estadoId;
        this.paisId = paisId;
        this.nombreLocalidad = nombreLocalidad;
    }

    public Integer getLocalidadId() {
        return localidadId;
    }

    public void setLocalidadId(Integer localidadId) {
        this.localidadId = localidadId;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public Integer getPaisId() {
        return paisId;
    }

    public void setPaisId(Integer paisId) {
        this.paisId = paisId;
    }

    public String getNombreLocalidad() {
        return nombreLocalidad;
    }

    public void setNombreLocalidad(String nombreLocalidad) {
        this.nombreLocalidad = nombreLocalidad;
    }
}
