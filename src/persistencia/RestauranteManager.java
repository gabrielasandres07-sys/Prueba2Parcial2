/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

/**
 *
 * @author USER
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import excepciones.PersistenciaException;
import modelo.Bebida;
import modelo.CategoriaProducto;
import modelo.DetallePedido;
import modelo.EstadoPedido;
import modelo.ModalidadServicio;
import modelo.Pedido;
import modelo.Plato;
import modelo.Postre;
import modelo.Producto;


public class RestauranteManager {

    private static final String CARPETA = "restaurante";

    private RandomAccessFile rcods, rprods, rpeds;

    public RestauranteManager() throws PersistenciaException {
        try {
            
            File mf = new File(CARPETA);
            mf.mkdir();
           
            rcods = new RandomAccessFile(CARPETA + "/codigos.rst", "rw");
            rprods = new RandomAccessFile(CARPETA + "/productos.rst", "rw");
            rpeds = new RandomAccessFile(CARPETA + "/pedidos.rst", "rw");
            
            initCodes();
        } catch (IOException e) {
            throw new PersistenciaException("No se pudieron abrir los archivos: " + e.getMessage(), e);
        }
    }
    private void initCodes() throws IOException {
        if (rcods.length() == 0) {
            rcods.seek(0);
            rcods.writeInt(1);
        }
    } 
    public int getCode() throws PersistenciaException {
        try {
            rcods.seek(0);
            int code = rcods.readInt();
            rcods.seek(0);
            rcods.writeInt(code + 1);
            return code;
        } catch (IOException e) {
            throw new PersistenciaException("Error al generar el código del pedido: " + e.getMessage(), e);
        }
    }

    public void guardarProductos(List<Producto> productos) throws PersistenciaException {
        try {
            rprods.setLength(0);  
            rprods.seek(0);
            for (Producto p : productos) {
                rprods.writeUTF(p.getCodigo());
                rprods.writeUTF(p.getCategoria().name());
                rprods.writeUTF(p.getNombre());
                rprods.writeDouble(p.getPrecio());
                if (p instanceof Plato) {
                    Plato plato = (Plato) p;
                    rprods.writeInt(plato.getTiempoPreparacionMin());
                    rprods.writeBoolean(plato.isVegetariano());
                } else if (p instanceof Bebida) {
                    Bebida bebida = (Bebida) p;
                    rprods.writeInt(bebida.getVolumenMl());
                    rprods.writeBoolean(bebida.isAlcoholica());
                } else if (p instanceof Postre) {
                    Postre postre = (Postre) p;
                    rprods.writeInt(postre.getCalorias());
                    rprods.writeBoolean(postre.isSinAzucar());
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar los productos: " + e.getMessage(), e);
        }
    }

    public List<Producto> cargarProductos() throws PersistenciaException {
        List<Producto> lista = new ArrayList<>();
        try {
            rprods.seek(0);
            while (rprods.getFilePointer() < rprods.length()) {
                String codigo = rprods.readUTF();
                CategoriaProducto categoria = CategoriaProducto.valueOf(rprods.readUTF());
                String nombre = rprods.readUTF();
                double precio = rprods.readDouble();
                int valor = rprods.readInt();
                boolean opcion = rprods.readBoolean();

                switch (categoria) {
                    case PLATO:
                        lista.add(new Plato(codigo, nombre, precio, valor, opcion));
                        break;
                    case BEBIDA:
                        lista.add(new Bebida(codigo, nombre, precio, valor, opcion));
                        break;
                    case POSTRE:
                        lista.add(new Postre(codigo, nombre, precio, valor, opcion));
                        break;
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al leer los productos: " + e.getMessage(), e);
        }
        return lista;
    }
    public void guardarPedidos(List<Pedido> pedidos) throws PersistenciaException {
        try {
            rpeds.setLength(0);
            rpeds.seek(0);
            for (Pedido p : pedidos) {
                rpeds.writeInt(p.getId());
                rpeds.writeLong(p.getFecha().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                rpeds.writeUTF(p.getModalidad().name());
                rpeds.writeUTF(p.getEstado().name());
                rpeds.writeUTF(p.getReferencia());
                rpeds.writeInt(p.getDetalles().size());
                for (DetallePedido d : p.getDetalles()) {
                    rpeds.writeUTF(d.getProducto().getCodigo());
                    rpeds.writeInt(d.getCantidad());
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar los pedidos: " + e.getMessage(), e);
        }
    }
    public List<Pedido> cargarPedidos(List<Producto> catalogo) throws PersistenciaException {
        List<Pedido> lista = new ArrayList<>();
        try {
            rpeds.seek(0);
            while (rpeds.getFilePointer() < rpeds.length()) {
                int id = rpeds.readInt();
                LocalDateTime fecha = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(rpeds.readLong()), ZoneId.systemDefault());
                ModalidadServicio modalidad = ModalidadServicio.valueOf(rpeds.readUTF());
                EstadoPedido estado = EstadoPedido.valueOf(rpeds.readUTF());
                String referencia = rpeds.readUTF();
                int cantidadDetalles = rpeds.readInt();

                Pedido pedido = new Pedido(id, fecha, modalidad, estado, referencia);
                for (int i = 0; i < cantidadDetalles; i++) {
                    String codigo = rpeds.readUTF();
                    int cantidad = rpeds.readInt();
                    Producto producto = buscar(catalogo, codigo);
                    if (producto == null) {
                        throw new PersistenciaException("El pedido #" + id+ " usa el producto " + codigo + ", que no está en el catálogo.", null);
                    }
                    pedido.agregarProducto(producto, cantidad);
                }
                lista.add(pedido);
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new PersistenciaException("Error al leer los pedidos: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean hayDatos() {
        try {
            return rprods.length() > 0 || rpeds.length() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    public String getRutaProductos() {
        return new File(CARPETA + "/productos.rst").getAbsolutePath();
    }

    public String getRutaPedidos() {
        return new File(CARPETA + "/pedidos.rst").getAbsolutePath();
    }

    private Producto buscar(List<Producto> catalogo, String codigo) {
        for (Producto p : catalogo) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }
}