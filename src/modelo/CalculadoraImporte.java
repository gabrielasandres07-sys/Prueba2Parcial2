/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author USER
 */

import java.util.List;

public final class CalculadoraImporte {

    private CalculadoraImporte() {
    }

   
    public static double calcularTotal(List<DetallePedido> detalles) {
        return calcularTotal(detalles, 0);
    }

    private static double calcularTotal(List<DetallePedido> detalles, int indice) {
        if (indice >= detalles.size()) {
            return 0.0;
        }
        DetallePedido actual = detalles.get(indice);
        double subtotal = calcularSubtotal(actual.getProducto().getPrecio(), actual.getCantidad());
        return subtotal + calcularTotal(detalles, indice + 1);
    }

 
    public static double calcularSubtotal(double precio, int cantidad) {
        if (cantidad <= 0) {
            return 0.0;
        }
        return precio + calcularSubtotal(precio, cantidad - 1);
    }

    public static int contarArticulos(List<DetallePedido> detalles) {
        return contarArticulos(detalles, 0);
    }

    private static int contarArticulos(List<DetallePedido> detalles, int indice) {
        if (indice >= detalles.size()) {
            return 0;
        }
        return detalles.get(indice).getCantidad() + contarArticulos(detalles, indice + 1);
    }
}