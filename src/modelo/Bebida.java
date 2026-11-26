/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author USER
 */

public class Bebida extends Producto {

    private int volumenMl;
    private boolean alcoholica;

    public Bebida(String codigo, String nombre, double precio,
                  int volumenMl, boolean alcoholica) {
        super(codigo, nombre, precio);
        this.volumenMl = volumenMl;
        this.alcoholica = alcoholica;
    }

    @Override
    public CategoriaProducto getCategoria() {
        return CategoriaProducto.BEBIDA;
    }

    @Override
    public String getDetalleEspecifico() {
        return "Volumen: " + volumenMl + " ml" + (alcoholica ? " | Con alcohol" : " | Sin alcohol");
    }

    public int getVolumenMl() {
        return volumenMl;
    }

    public void setVolumenMl(int volumenMl) {
        this.volumenMl = volumenMl;
    }

    public boolean isAlcoholica() {
        return alcoholica;
    }

    public void setAlcoholica(boolean alcoholica) {
        this.alcoholica = alcoholica;
    }
}