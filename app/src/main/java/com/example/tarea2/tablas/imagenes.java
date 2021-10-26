package com.example.tarea2.tablas;

public class imagenes {

    private Integer id;
    private byte[] imagen;
    private String descripcion;

    public imagenes(Integer id, byte[] imagen, String descripcion) {
        this.id = id;
        this.imagen = imagen;
        this.descripcion = descripcion;
    }

    public imagenes(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }






}
