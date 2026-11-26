/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author USER
 */

public class Postre extends Producto {

    private int calorias;
    private boolean sinAzucar;

    public Postre(String codigo, String nombre, double precio,
                  int calorias, boolean sinAzucar) {
        super(codigo, nombre, precio);
        this.calorias = calorias;
        this.sinAzucar = sinAzucar;
    }

    @Override
    public CategoriaProducto getCategoria() {
        return CategoriaProducto.POSTRE;
    }

    @Override
    public String getDetalleEspecifico() {
        return "Calorías: " + calorias + " kcal"+ (sinAzucar ? " | Sin azúcar" : "");
    }

    public int getCalorias() {
        return calorias;
    }

    public void setCalorias(int calorias) {
        this.calorias = calorias;
    }

    public boolean isSinAzucar() {
        return sinAzucar;
    }

    public void setSinAzucar(boolean sinAzucar) {
        this.sinAzucar = sinAzucar;
    }
}
