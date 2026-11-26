/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author USER
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import excepciones.PersistenciaException;
import excepciones.ValidacionException;
import modelo.DetallePedido;
import modelo.ModalidadServicio;
import modelo.Pedido;
import modelo.Producto;
import servicio.GestorRestaurante;

public class PanelPedidos extends JPanel implements Refrescable {

    private final GestorRestaurante gestor;
    private final Consumer<String> notificador;
    private Pedido borrador;
    private List<Producto> catalogoVisible;

    private final DefaultTableModel modeloCatalogo =
            Estilo.modeloSoloLectura("Código", "Categoría", "Producto", "Precio");
    private final JTable tablaCatalogo = Estilo.tabla(modeloCatalogo);
    private final JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

    private final DefaultTableModel modeloDetalle =
            Estilo.modeloSoloLectura("Código", "Producto", "Precio unit.", "Cantidad", "Subtotal");
    private final JTable tablaDetalle = Estilo.tabla(modeloDetalle);
    private final JLabel lblTotal = new JLabel();

    private final JComboBox<ModalidadServicio> cbModalidad = new JComboBox<>(ModalidadServicio.values());
    private final JLabel lblReferencia = new JLabel();
    private final JTextField txtReferencia = new JTextField(18);

    public PanelPedidos(GestorRestaurante gestor, Consumer<String> notificador) {
        this.gestor = gestor;
        this.notificador = notificador;
        this.borrador = gestor.crearBorrador();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(Estilo.titulo("Nuevo pedido"), BorderLayout.NORTH);

        JSplitPane division = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, crearCatalogo(), crearDetalle());
        division.setResizeWeight(0.45);
        division.setBorder(null);
        add(division, BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        cbModalidad.addActionListener(e -> ajustarReferencia());
        ajustarReferencia();
        actualizarDetalle();
        refrescar();
    }

    private JPanel crearCatalogo() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(Estilo.seccion("1. Seleccione productos del catálogo"));
        panel.add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);

        JButton btnAgregar = new JButton("Agregar al pedido  >>");
        btnAgregar.addActionListener(e -> agregar());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        acciones.add(new JLabel("Cantidad:"));
        acciones.add(spCantidad);
        acciones.add(btnAgregar);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearDetalle() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(Estilo.seccion("2. Detalle del pedido"));
        panel.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);

        JButton btnQuitar = new JButton("Quitar seleccionado");
        JButton btnVaciar = new JButton("Vaciar");
        btnQuitar.addActionListener(e -> quitar());
        btnVaciar.addActionListener(e -> vaciar());
        lblTotal.setFont(Estilo.SUBTITULO);
        lblTotal.setForeground(Estilo.PRIMARIO);

        JPanel pie = new JPanel(new BorderLayout());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botones.add(btnQuitar);
        botones.add(btnVaciar);
        pie.add(botones, BorderLayout.WEST);
        pie.add(lblTotal, BorderLayout.EAST);
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPie() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(Estilo.seccion("3. Datos del servicio"));

        JPanel campos = new JPanel(new GridLayout(1, 0, 8, 0));
        JPanel modalidad = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        modalidad.add(new JLabel("Modalidad:"));
        modalidad.add(cbModalidad);
        JPanel referencia = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        referencia.add(lblReferencia);
        referencia.add(txtReferencia);
        campos.add(modalidad);
        campos.add(referencia);

        JButton btnRegistrar = new JButton("Registrar pedido");
        btnRegistrar.setFont(Estilo.SUBTITULO);
        btnRegistrar.addActionListener(e -> registrar());

        panel.add(campos, BorderLayout.CENTER);
        panel.add(btnRegistrar, BorderLayout.EAST);
        return panel;
    }

    private void ajustarReferencia() {
        boolean domicilio = cbModalidad.getSelectedItem() == ModalidadServicio.A_DOMICILIO;
        lblReferencia.setText(domicilio ? "Dirección de entrega:" : "Número de mesa:");
    }

    private void agregar() {
        int fila = tablaCatalogo.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del catálogo.",
                    "Agregar producto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int cantidad = gestor.validarCantidad(spCantidad.getValue());
            borrador.agregarProducto(catalogoVisible.get(fila), cantidad);
            spCantidad.setValue(1);
            actualizarDetalle();
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cantidad no válida",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void quitar() {
        int fila = tablaDetalle.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un artículo del detalle.",
                    "Quitar producto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        borrador.eliminarDetalle(fila);
        actualizarDetalle();
    }

    private void vaciar() {
        borrador = gestor.crearBorrador();
        actualizarDetalle();
    }

    private void registrar() {
        try {
            Pedido pedido = gestor.registrarPedido(borrador,
                    (ModalidadServicio) cbModalidad.getSelectedItem(), txtReferencia.getText());
            JOptionPane.showMessageDialog(this,
                    String.format("Pedido #%d registrado.%nArtículos: %d%nTotal: %s%nEstado: %s",
                            pedido.getId(), pedido.getCantidadArticulos(),
                            Estilo.dinero(pedido.getTotal()), pedido.getEstado()),
                    "Pedido registrado", JOptionPane.INFORMATION_MESSAGE);
            notificador.accept("Pedido #" + pedido.getId() + " registrado (" + Estilo.dinero(pedido.getTotal())
                    + "). Recuerde guardar en Almacenamiento.");
            txtReferencia.setText("");
            vaciar();
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo registrar",
                    JOptionPane.WARNING_MESSAGE);
        } catch (PersistenciaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de archivo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarDetalle() {
        modeloDetalle.setRowCount(0);
        for (DetallePedido d : borrador.getDetalles()) {
            Producto p = d.getProducto();
            modeloDetalle.addRow(new Object[]{
                p.getCodigo(), p.getNombre(), Estilo.dinero(p.getPrecio()),
                d.getCantidad(), Estilo.dinero(d.getSubtotal())
            });
        }
        lblTotal.setText(String.format("Artículos: %d    TOTAL: %s",
                borrador.getCantidadArticulos(), Estilo.dinero(borrador.getTotal())));
    }

    @Override
    public void refrescar() {
        catalogoVisible = gestor.getProductos();
        modeloCatalogo.setRowCount(0);
        for (Producto p : catalogoVisible) {
            modeloCatalogo.addRow(new Object[]{
                p.getCodigo(), p.getCategoria(), p.getNombre(), Estilo.dinero(p.getPrecio())
            });
        }
    }
}