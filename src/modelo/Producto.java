/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author USER
 */

import java.util.Objects;

public abstract class Producto {

    private String codigo;
    private String nombre;
    private double precio;

    protected Producto(String codigo, String nombre, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
    }

   
    public abstract CategoriaProducto getCategoria();
    public abstract String getDetalleEspecifico();

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

   
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producto)) {
            return false;
        }
        return codigo.equalsIgnoreCase(((Producto) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("%s - %s ($%.2f)", codigo, nombre, precio);
    }
}