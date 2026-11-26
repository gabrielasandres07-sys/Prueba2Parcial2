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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import excepciones.ValidacionException;
import modelo.CategoriaProducto;
import modelo.Producto;
import servicio.GestorRestaurante;

public class PanelProductos extends JPanel implements Refrescable {

    private static final String TODAS = "Todas las categorías";

    private final GestorRestaurante gestor;

    private final JComboBox<CategoriaProducto> cbCategoria = new JComboBox<>(CategoriaProducto.values());
    private final JTextField txtCodigo = new JTextField(10);
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtPrecio = new JTextField(8);
    private final JLabel lblNumerico = new JLabel();
    private final JTextField txtNumerico = new JTextField(8);
    private final JCheckBox chkOpcion = new JCheckBox();

    private final JComboBox<Object> cbFiltro = new JComboBox<>();
    private final DefaultTableModel modelo =
            Estilo.modeloSoloLectura("Código", "Categoría", "Nombre", "Precio", "Características");
    private final JTable tabla = Estilo.tabla(modelo);
    private final JLabel lblResumen = new JLabel();

    public PanelProductos(GestorRestaurante gestor) {
        this.gestor = gestor;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        add(Estilo.titulo("Gestión de productos"), BorderLayout.NORTH);
        add(crearFormulario(), BorderLayout.WEST);
        add(crearCatalogo(), BorderLayout.CENTER);

        cbCategoria.addActionListener(e -> ajustarCamposCategoria());
        ajustarCamposCategoria();
        refrescar();
    }

    private JPanel crearFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(Estilo.seccion("Registrar producto"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        Object[][] filas = {
            {"Categoría:", cbCategoria},
            {"Código:", txtCodigo},
            {"Nombre:", txtNombre},
            {"Precio ($):", txtPrecio},
            {lblNumerico, txtNumerico},
            {"", chkOpcion}
        };
        for (int i = 0; i < filas.length; i++) {
            c.gridy = i;
            c.gridx = 0;
            c.weightx = 0;
            Object etiqueta = filas[i][0];
            form.add(etiqueta instanceof JLabel ? (JLabel) etiqueta : new JLabel((String) etiqueta), c);
            c.gridx = 1;
            c.weightx = 1;
            form.add((Component) filas[i][1], c);
        }

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnRegistrar.addActionListener(e -> registrar());
        btnLimpiar.addActionListener(e -> limpiar());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.add(btnLimpiar);
        botones.add(btnRegistrar);
        c.gridy = filas.length;
        c.gridx = 0;
        c.gridwidth = 2;
        c.insets = new Insets(12, 4, 4, 4);
        form.add(botones, c);

        c.gridy++;
        c.weighty = 1;
        form.add(new JLabel(), c);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(form, BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearCatalogo() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(Estilo.seccion("Catálogo de productos"));

        cbFiltro.addItem(TODAS);
        for (CategoriaProducto c : CategoriaProducto.values()) {
            cbFiltro.addItem(c);
        }
        cbFiltro.addActionListener(e -> refrescar());
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filtro.add(new JLabel("Mostrar:"));
        filtro.add(cbFiltro);
        filtro.add(lblResumen);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(170);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(230);

        panel.add(filtro, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void ajustarCamposCategoria() {
        CategoriaProducto cat = (CategoriaProducto) cbCategoria.getSelectedItem();
        if (cat == CategoriaProducto.PLATO) {
            lblNumerico.setText("Preparación (min):");
            chkOpcion.setText("Vegetariano");
        } else if (cat == CategoriaProducto.BEBIDA) {
            lblNumerico.setText("Volumen (ml):");
            chkOpcion.setText("Contiene alcohol");
        } else {
            lblNumerico.setText("Calorías (kcal):");
            chkOpcion.setText("Sin azúcar");
        }
    }

    private void registrar() {
        try {
            Producto p = gestor.registrarProducto(
                    (CategoriaProducto) cbCategoria.getSelectedItem(),
                    txtCodigo.getText(), txtNombre.getText(), txtPrecio.getText(),
                    txtNumerico.getText(), chkOpcion.isSelected());
            JOptionPane.showMessageDialog(this,
                    p.getCategoria() + " registrado correctamente:\n" + p,
                    "Producto registrado", JOptionPane.INFORMATION_MESSAGE);
            limpiar();
            refrescar();
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos no válidos",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtNumerico.setText("");
        chkOpcion.setSelected(false);
        txtCodigo.requestFocusInWindow();
    }

    @Override
    public void refrescar() {
        Object filtro = cbFiltro.getSelectedItem();
        CategoriaProducto categoria = filtro instanceof CategoriaProducto ? (CategoriaProducto) filtro : null;
        modelo.setRowCount(0);
        for (Producto p : gestor.getProductos(categoria)) {
            modelo.addRow(new Object[]{
                p.getCodigo(), p.getCategoria(), p.getNombre(),
                Estilo.dinero(p.getPrecio()), p.getDetalleEspecifico()
            });
        }
        lblResumen.setText("   " + modelo.getRowCount() + " producto(s)");
    }
}