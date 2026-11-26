/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author USER
 */

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {

    private final int id;
    private final LocalDateTime fecha;
    private final ArrayList<DetallePedido> detalles;
    private ModalidadServicio modalidad;
    private EstadoPedido estado;
    private String referencia;

    public Pedido(int id, ModalidadServicio modalidad, String referencia) {
        this(id, LocalDateTime.now(), modalidad, EstadoPedido.PENDIENTE, referencia);
    }

    public Pedido(int id, LocalDateTime fecha, ModalidadServicio modalidad,
                  EstadoPedido estado, String referencia) {
        this.id = id;
        this.fecha = fecha;
        this.detalles = new ArrayList<>();
        this.modalidad = modalidad;
        this.estado = estado;
        this.referencia = referencia;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        for (DetallePedido d : detalles) {
            if (d.getProducto().equals(producto)) {
                d.setCantidad(d.getCantidad() + cantidad);
                return;
            }
        }
        detalles.add(new DetallePedido(producto, cantidad));
    }

    public void eliminarDetalle(int indice) {
        detalles.remove(indice);
    }

    public double getTotal() {
        return CalculadoraImporte.calcularTotal(detalles);
    }

    public int getCantidadArticulos() {
        return CalculadoraImporte.contarArticulos(detalles);
    }

    public boolean estaVacio() {
        return detalles.isEmpty();
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public ModalidadServicio getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadServicio modalidad) {
        this.modalidad = modalidad;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}