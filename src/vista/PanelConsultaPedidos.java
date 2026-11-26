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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import excepciones.ValidacionException;
import modelo.DetallePedido;
import modelo.EstadoPedido;
import modelo.Pedido;
import servicio.GestorRestaurante;

public class PanelConsultaPedidos extends JPanel implements Refrescable {

    private final GestorRestaurante gestor;
    private List<Pedido> pedidos;

    private final DefaultTableModel modeloPedidos = Estilo.modeloSoloLectura(
            "N.º", "Fecha", "Modalidad", "Mesa / Dirección", "Artículos", "Importe", "Estado");
    private final JTable tablaPedidos = Estilo.tabla(modeloPedidos);
    private final DefaultTableModel modeloDetalle =
            Estilo.modeloSoloLectura("Código", "Producto", "Precio unit.", "Cantidad", "Subtotal");
    private final JTable tablaDetalle = Estilo.tabla(modeloDetalle);
    private final JLabel lblDetalle = new JLabel(" ");
    private final JComboBox<EstadoPedido> cbEstado = new JComboBox<>(EstadoPedido.values());

    public PanelConsultaPedidos(GestorRestaurante gestor) {
        this.gestor = gestor;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(Estilo.titulo("Consulta de pedidos"), BorderLayout.NORTH);

        JPanel lista = new JPanel(new BorderLayout(6, 6));
        lista.setBorder(Estilo.seccion("Pedidos registrados"));
        lista.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);

        JButton btnActualizar = new JButton("Actualizar estado");
        btnActualizar.addActionListener(e -> actualizarEstado());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        acciones.add(new JLabel("Nuevo estado:"));
        acciones.add(cbEstado);
        acciones.add(btnActualizar);
        lista.add(acciones, BorderLayout.SOUTH);

        JPanel detalle = new JPanel(new BorderLayout(6, 6));
        detalle.setBorder(Estilo.seccion("Detalle del pedido seleccionado"));
        lblDetalle.setFont(Estilo.SUBTITULO);
        detalle.add(lblDetalle, BorderLayout.NORTH);
        detalle.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);

        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, lista, detalle);
        division.setResizeWeight(0.55);
        division.setBorder(null);
        add(division, BorderLayout.CENTER);

        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });
        refrescar();
    }

    private Pedido pedidoSeleccionado() {
        int fila = tablaPedidos.getSelectedRow();
        return fila < 0 ? null : pedidos.get(fila);
    }

    private void actualizarEstado() {
        Pedido pedido = pedidoSeleccionado();
        try {
            gestor.actualizarEstado(pedido, (EstadoPedido) cbEstado.getSelectedItem());
            int fila = tablaPedidos.getSelectedRow();
            refrescar();
            tablaPedidos.setRowSelectionInterval(fila, fila);
            JOptionPane.showMessageDialog(this,
                    "El pedido #" + pedido.getId() + " ahora está: " + pedido.getEstado(),
                    "Estado actualizado", JOptionPane.INFORMATION_MESSAGE);
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Actualizar estado",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void mostrarDetalle() {
        modeloDetalle.setRowCount(0);
        Pedido pedido = pedidoSeleccionado();
        if (pedido == null) {
            lblDetalle.setText(" ");
            return;
        }
        cbEstado.setSelectedItem(pedido.getEstado());
        for (DetallePedido d : pedido.getDetalles()) {
            modeloDetalle.addRow(new Object[]{
                d.getProducto().getCodigo(), d.getProducto().getNombre(),
                Estilo.dinero(d.getProducto().getPrecio()), d.getCantidad(),
                Estilo.dinero(d.getSubtotal())
            });
        }
        lblDetalle.setText(String.format("Pedido #%d  |  %s  |  %s  |  Total: %s",
                pedido.getId(), pedido.getModalidad(), pedido.getEstado(),
                Estilo.dinero(pedido.getTotal())));
    }

    @Override
    public void refrescar() {
        pedidos = gestor.getPedidos();
        modeloPedidos.setRowCount(0);
        for (Pedido p : pedidos) {
            modeloPedidos.addRow(new Object[]{
                p.getId(), p.getFecha().format(Estilo.FECHA), p.getModalidad(),
                p.getReferencia(), p.getCantidadArticulos(),
                Estilo.dinero(p.getTotal()), p.getEstado()
            });
        }
        mostrarDetalle();
    }
}