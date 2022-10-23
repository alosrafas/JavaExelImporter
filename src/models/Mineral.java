package models;

public class Mineral {
    private String numero;
    private int idClasificacion;
    private String variedad;
    private String formulaQuimica;
    private int idSistemaCristalizacion;
    private String mineralAsociados;
    private String matriz;
    private int idLocalidad;
    private int idEstado;
    private int idPais;
    private int idClaseQuimica;
    private int idGrupo;
    private Double largo;
    private Double alto;
    private Double ancho;
    private String notasCampo;
    private int idUbicacion;
    private String observaciones;
    private int idColector;
    private String estadoEjemplar;
    private String fechaIngreso;

    public Mineral(String numero, int idClasificacion, String variedad, String formulaQuimica,
                   int idSistemaCristalizacion, String mineralAsociados, String matriz, int idLocalidad,
                   int idEstado, int idPais, int idClaseQuimica, int idGrupo, Double largo, Double alto,
                   Double ancho, String notasCampo, int idUbicacion, String observaciones, int idColector,
                   String estadoEjemplar, String fechaIngreso) {
        this.numero = numero;
        this.idClasificacion = idClasificacion;
        this.variedad = variedad;
        this.formulaQuimica = formulaQuimica;
        this.idSistemaCristalizacion = idSistemaCristalizacion;
        this.mineralAsociados = mineralAsociados;
        this.matriz = matriz;
        this.idLocalidad = idLocalidad;
        this.idEstado = idEstado;
        this.idPais = idPais;
        this.idClaseQuimica = idClaseQuimica;
        this.idGrupo = idGrupo;
        this.largo = largo;
        this.alto = alto;
        this.ancho = ancho;
        this.notasCampo = notasCampo;
        this.idUbicacion = idUbicacion;
        this.observaciones = observaciones;
        this.idColector = idColector;
        this.estadoEjemplar = estadoEjemplar;
        this.fechaIngreso = fechaIngreso;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getIdClasificacion() {
        return idClasificacion;
    }

    public void setIdClasificacion(int idClasificacion) {
        this.idClasificacion = idClasificacion;
    }

    public String getVariedad() {
        return variedad;
    }

    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }

    public String getFormulaQuimica() {
        return formulaQuimica;
    }

    public void setFormulaQuimica(String formulaQuimica) {
        this.formulaQuimica = formulaQuimica;
    }

    public int getIdSistemaCristalizacion() {
        return idSistemaCristalizacion;
    }

    public void setIdSistemaCristalizacion(int idSistemaCristalizacion) {
        this.idSistemaCristalizacion = idSistemaCristalizacion;
    }

    public String getMineralAsociados() {
        return mineralAsociados;
    }

    public void setMineralAsociados(String mineralAsociados) {
        this.mineralAsociados = mineralAsociados;
    }

    public String getMatriz() {
        return matriz;
    }

    public void setMatriz(String matriz) {
        this.matriz = matriz;
    }

    public int getIdLocalidad() {
        return idLocalidad;
    }

    public void setIdLocalidad(int idLocalidad) {
        this.idLocalidad = idLocalidad;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public int getIdClaseQuimica() {
        return idClaseQuimica;
    }

    public void setIdClaseQuimica(int idClaseQuimica) {
        this.idClaseQuimica = idClaseQuimica;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Double getLargo() {
        return largo;
    }

    public void setLargo(double largo) {
        this.largo = largo;
    }

    public Double getAlto() {
        return alto;
    }

    public void setAlto(double alto) {
        this.alto = alto;
    }

    public Double getAncho() {
        return ancho;
    }

    public void setAncho(double ancho) {
        this.ancho = ancho;
    }

    public String getNotasCampo() {
        return notasCampo;
    }

    public void setNotasCampo(String notasCampo) {
        this.notasCampo = notasCampo;
    }

    public int getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getIdColector() {
        return idColector;
    }

    public void setIdColector(int idColector) {
        this.idColector = idColector;
    }

    public String getEstadoEjemplar() {
        return estadoEjemplar;
    }

    public void setEstadoEjemplar(String estadoEjemplar) {
        this.estadoEjemplar = estadoEjemplar;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
}
