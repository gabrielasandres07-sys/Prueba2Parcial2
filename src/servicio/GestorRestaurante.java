/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

/**
 *
 * @author USER
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import excepciones.PersistenciaException;
import excepciones.ValidacionException;
import modelo.Bebida;
import modelo.CategoriaProducto;
import modelo.DetallePedido;
import modelo.EstadoPedido;
import modelo.ModalidadServicio;
import modelo.Pedido;
import modelo.Plato;
import modelo.Postre;
import modelo.Producto;
import persistencia.RestauranteManager;

public class GestorRestaurante {

    private final List<Producto> productos = new ArrayList<>();
    private final List<Pedido> pedidos = new ArrayList<>();
    private final RestauranteManager archivos;

    public GestorRestaurante() throws PersistenciaException {
        this.archivos = new RestauranteManager();
    }

    public Producto registrarProducto(CategoriaProducto categoria, String codigo, String nombre,
                                      String precioTexto, String valorNumerico, boolean opcion)
            throws ValidacionException {
        if (categoria == null) {
            throw new ValidacionException("Seleccione una categoría.");
        }
        codigo = codigo == null ? "" : codigo.trim().toUpperCase();
        nombre = nombre == null ? "" : nombre.trim();
        if (codigo.isEmpty()) {
            throw new ValidacionException("El código es obligatorio.");
        }
        if (!codigo.matches("[A-Z0-9-]{1,10}")) {
            throw new ValidacionException("El código solo admite letras, números y guiones (máx. 10).");
        }
        if (buscarProducto(codigo) != null) {
            throw new ValidacionException("Ya existe un producto con el código " + codigo + ".");
        }
        if (nombre.isEmpty()) {
            throw new ValidacionException("El nombre es obligatorio.");
        }
        double precio = validarPrecio(precioTexto);

        Producto producto;
        switch (categoria) {
            case PLATO:
                producto = new Plato(codigo, nombre, precio,
                        validarEnteroPositivo(valorNumerico, "El tiempo de preparación"), opcion);
                break;
            case BEBIDA:
                producto = new Bebida(codigo, nombre, precio,
                        validarEnteroPositivo(valorNumerico, "El volumen"), opcion);
                break;
            case POSTRE:
                producto = new Postre(codigo, nombre, precio,
                        validarEnteroPositivo(valorNumerico, "Las calorías"), opcion);
                break;
            default:
                throw new ValidacionException("Categoría no soportada.");
        }
        productos.add(producto);
        return producto;
    }

    public Producto buscarProducto(String codigo) {
        for (Producto p : productos) {
            if (p.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return p;
            }
        }
        return null;
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public List<Producto> getProductos(CategoriaProducto categoria) {
        if (categoria == null) {
            return getProductos();
        }
        List<Producto> filtrados = new ArrayList<>();
        for (Producto p : productos) {
            if (p.getCategoria() == categoria) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    public Pedido crearBorrador() {
        return new Pedido(0, ModalidadServicio.EN_ESTABLECIMIENTO, "");
    }

    public int validarCantidad(Object valor) throws ValidacionException {
        int cantidad;
        try {
            cantidad = Integer.parseInt(String.valueOf(valor).trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException("La cantidad debe ser un número entero.");
        }
        if (cantidad <= 0 || cantidad > 100) {
            throw new ValidacionException("La cantidad debe estar entre 1 y 100.");
        }
        return cantidad;
    }

    public Pedido registrarPedido(Pedido borrador, ModalidadServicio modalidad, String referencia)
            throws ValidacionException, PersistenciaException {
        if (borrador == null || borrador.estaVacio()) {
            throw new ValidacionException("No se puede registrar un pedido sin productos.");
        }
        if (modalidad == null) {
            throw new ValidacionException("Seleccione la modalidad del servicio.");
        }
        referencia = referencia == null ? "" : referencia.trim();
        if (referencia.isEmpty()) {
            throw new ValidacionException(modalidad == ModalidadServicio.A_DOMICILIO
                    ? "Ingrese la dirección de entrega."
                    : "Ingrese el número de mesa.");
        }
        if (modalidad == ModalidadServicio.EN_ESTABLECIMIENTO) {
            validarEnteroPositivo(referencia, "El número de mesa");
        }
        Pedido pedido = new Pedido(archivos.getCode(), modalidad, referencia);
        for (DetallePedido d : borrador.getDetalles()) {
            pedido.agregarProducto(d.getProducto(), d.getCantidad());
        }
        pedidos.add(pedido);
        return pedido;
    }

    public void actualizarEstado(Pedido pedido, EstadoPedido nuevoEstado) throws ValidacionException {
        if (pedido == null) {
            throw new ValidacionException("Seleccione un pedido.");
        }
        if (nuevoEstado == null) {
            throw new ValidacionException("Seleccione un estado.");
        }
        pedido.setEstado(nuevoEstado);
    }

    public List<Pedido> getPedidos() {
        return Collections.unmodifiableList(pedidos);
    }

    public void guardarProductos() throws PersistenciaException {
        archivos.guardarProductos(productos);
    }

    public void guardarPedidos() throws PersistenciaException {
        archivos.guardarPedidos(pedidos);
    }

    public int recuperarProductos() throws PersistenciaException {
        List<Producto> leidos = archivos.cargarProductos();
        productos.clear();
        productos.addAll(leidos);
        return leidos.size();
    }

    public int recuperarPedidos() throws PersistenciaException {
        List<Pedido> leidos = archivos.cargarPedidos(productos);
        pedidos.clear();
        pedidos.addAll(leidos);
        return leidos.size();
    }

    public boolean existenArchivos() {
        return archivos.hayDatos();
    }

    public String getRutaProductos() {
        return archivos.getRutaProductos();
    }

    public String getRutaPedidos() {
        return archivos.getRutaPedidos();
    }

    public int cargarDatosEjemplo() {
        Producto[] ejemplos = {
            new Plato("P001", "Lomo saltado", 12.50, 20, false),
            new Plato("P002", "Ensalada César", 8.00, 10, true),
            new Plato("P003", "Pasta al pesto", 10.75, 15, true),
            new Bebida("B001", "Limonada natural", 2.50, 500, false),
            new Bebida("B002", "Agua mineral", 1.25, 600, false),
            new Bebida("B003", "Cerveza artesanal", 4.00, 355, true),
            new Postre("D001", "Cheesecake de maracuyá", 4.50, 380, false),
            new Postre("D002", "Gelatina light", 2.00, 60, true)
        };
        int agregados = 0;
        for (Producto p : ejemplos) {
            if (buscarProducto(p.getCodigo()) == null) {
                productos.add(p);
                agregados++;
            }
        }
        return agregados;
    }

    private double validarPrecio(String texto) throws ValidacionException {
        double precio;
        try {
            precio = Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidacionException("El precio debe ser un número (ej. 12.50).");
        }
        if (precio <= 0 || precio > 10000 || Double.isNaN(precio)) {
            throw new ValidacionException("El precio debe ser mayor que 0 y menor que 10000.");
        }
        return Math.round(precio * 100.0) / 100.0;
    }

    private int validarEnteroPositivo(String texto, String campo) throws ValidacionException {
        int valor;
        try {
            valor = Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValidacionException(campo + " debe ser un número entero.");
        }
        if (valor <= 0) {
            throw new ValidacionException(campo + " debe ser mayor que 0.");
        }
        return valor;
    }
}