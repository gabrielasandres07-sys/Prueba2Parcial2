/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author USER
 */

public class Plato extends Producto {

    private int tiempoPreparacionMin;
    private boolean vegetariano;

    public Plato(String codigo, String nombre, double precio,
                 int tiempoPreparacionMin, boolean vegetariano) {
        super(codigo, nombre, precio);
        this.tiempoPreparacionMin = tiempoPreparacionMin;
        this.vegetariano = vegetariano;
    }

    @Override
    public CategoriaProducto getCategoria() {
        return CategoriaProducto.PLATO;
    }

    @Override
    public String getDetalleEspecifico() {
        return "Preparación: " + tiempoPreparacionMin + " min"+ (vegetariano ? " | Vegetariano" : "");
    }

    public int getTiempoPreparacionMin() {
        return tiempoPreparacionMin;
    }

    public void setTiempoPreparacionMin(int tiempoPreparacionMin) {
        this.tiempoPreparacionMin = tiempoPreparacionMin;
    }

    public boolean isVegetariano() {
        return vegetariano;
    }

    public void setVegetariano(boolean vegetariano) {
        this.vegetariano = vegetariano;
    }
}
