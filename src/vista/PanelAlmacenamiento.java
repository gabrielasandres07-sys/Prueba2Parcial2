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
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import excepciones.PersistenciaException;
import servicio.GestorRestaurante;

public class PanelAlmacenamiento extends JPanel {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final GestorRestaurante gestor;
    private final Consumer<String> notificador;
    private final JTextArea bitacora = new JTextArea();

    private interface Operacion {
        String ejecutar() throws PersistenciaException;
    }

    public PanelAlmacenamiento(GestorRestaurante gestor, Consumer<String> notificador) {
        this.gestor = gestor;
        this.notificador = notificador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        add(Estilo.titulo("Almacenamiento de información"), BorderLayout.NORTH);

        JPanel izquierda = new JPanel(new BorderLayout(0, 10));

        JPanel archivos = new JPanel(new GridLayout(0, 1, 0, 2));
        archivos.setBorder(Estilo.seccion("Archivos binarios (RandomAccessFile)"));
        archivos.add(new JLabel("Catálogo:  " + gestor.getRutaProductos()));
        archivos.add(new JLabel("Pedidos:   " + gestor.getRutaPedidos()));

        JPanel botones = new JPanel(new GridLayout(0, 2, 8, 8));
        botones.setBorder(Estilo.seccion("Operaciones"));
        botones.add(boton("Guardar catálogo",
                () -> { gestor.guardarProductos(); return gestor.getProductos().size() + " productos guardados."; }));
        botones.add(boton("Recuperar catálogo",
                () -> gestor.recuperarProductos() + " productos recuperados."));
        botones.add(boton("Guardar pedidos",
                () -> { gestor.guardarPedidos(); return gestor.getPedidos().size() + " pedidos guardados."; }));
        botones.add(boton("Recuperar pedidos",
                () -> gestor.recuperarPedidos() + " pedidos recuperados."));
        botones.add(boton("Guardar todo", () -> {
            gestor.guardarProductos();
            gestor.guardarPedidos();
            return gestor.getProductos().size() + " productos y " + gestor.getPedidos().size() + " pedidos guardados.";
        }));
        botones.add(boton("Recuperar todo", () -> {
            int prod = gestor.recuperarProductos();
            int ped = gestor.recuperarPedidos();
            return prod + " productos y " + ped + " pedidos recuperados.";
        }));

        JButton btnEjemplo = new JButton("Cargar productos de ejemplo");
        btnEjemplo.addActionListener(e -> {
            int n = gestor.cargarDatosEjemplo();
            registrar("Datos de ejemplo", n + " productos de ejemplo agregados (Plato, Bebida, Postre).");
            JOptionPane.showMessageDialog(this, n + " productos de ejemplo agregados al catálogo.",
                    "Datos de ejemplo", JOptionPane.INFORMATION_MESSAGE);
        });
        botones.add(btnEjemplo);

        izquierda.add(archivos, BorderLayout.NORTH);
        izquierda.add(botones, BorderLayout.CENTER);

        bitacora.setEditable(false);
        bitacora.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(bitacora);
        scroll.setBorder(Estilo.seccion("Resultado de las operaciones"));

        JPanel centro = new JPanel(new GridLayout(1, 2, 10, 0));
        JPanel envoltura = new JPanel(new BorderLayout());
        envoltura.add(izquierda, BorderLayout.NORTH);
        centro.add(envoltura);
        centro.add(scroll);
        add(centro, BorderLayout.CENTER);
    }

    private JButton boton(String texto, Operacion operacion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> {
            try {
                String resultado = operacion.ejecutar();
                registrar(texto, "OK - " + resultado);
                JOptionPane.showMessageDialog(this, resultado, texto,
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (PersistenciaException ex) {
                registrar(texto, "ERROR - " + ex.getMessage());
                JOptionPane.showMessageDialog(this, ex.getMessage(), texto,
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        return boton;
    }

    private void registrar(String operacion, String mensaje) {
        bitacora.append("[" + LocalTime.now().format(HORA) + "] " + operacion + ": " + mensaje + "\n");
        notificador.accept(operacion + ": " + mensaje);
    }
}
