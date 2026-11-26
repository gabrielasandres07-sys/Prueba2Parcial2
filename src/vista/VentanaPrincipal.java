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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import excepciones.PersistenciaException;
import servicio.GestorRestaurante;

public class VentanaPrincipal extends JFrame {

    private static final String NOMBRE_SISTEMA = "Sistema de Gestión de Pedidos - Restaurante";

    private final GestorRestaurante gestor;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final Map<String, Component> vistas = new LinkedHashMap<>();
    private final JLabel barraEstado = new JLabel(" ");

    public VentanaPrincipal(GestorRestaurante gestor) {
        super(NOMBRE_SISTEMA);
        this.gestor = gestor;

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 640));

        vistas.put("Inicio", crearPanelInicio());
        vistas.put("Productos", new PanelProductos(gestor));
        vistas.put("Nuevo pedido", new PanelPedidos(gestor, this::mostrarEstado));
        vistas.put("Consulta de pedidos", new PanelConsultaPedidos(gestor));
        vistas.put("Almacenamiento", new PanelAlmacenamiento(gestor, this::mostrarEstado));
        for (Map.Entry<String, Component> e : vistas.entrySet()) {
            contenido.add(e.getValue(), e.getKey());
        }

        setLayout(new BorderLayout());
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearMenu(), BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);
        barraEstado.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        add(barraEstado, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });

        cargarAlIniciar();
        setLocationRelativeTo(null);
    }

    public void mostrarEstado(String mensaje) {
        barraEstado.setText(mensaje);
    }

    private void navegar(String nombre) {
        Component vista = vistas.get(nombre);
        if (vista instanceof Refrescable) {
            ((Refrescable) vista).refrescar();
        }
        tarjetas.show(contenido, nombre);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Estilo.PRIMARIO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        JLabel titulo = new JLabel(NOMBRE_SISTEMA);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        encabezado.add(titulo, BorderLayout.WEST);
        return encabezado;
    }

    private JPanel crearMenu() {
        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 4));
        menu.setBackground(Estilo.FONDO_MENU);
        menu.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        for (String nombre : vistas.keySet()) {
            JButton boton = new JButton(nombre);
            boton.setFocusPainted(false);
            boton.setHorizontalAlignment(SwingConstants.LEFT);
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.addActionListener(e -> navegar(nombre));
            menu.add(boton);
        }
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Estilo.FONDO_MENU);
        contenedor.setPreferredSize(new Dimension(200, 0));
        contenedor.add(menu, BorderLayout.NORTH);
        return contenedor;
    }

    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel caja = new JPanel();
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Bienvenido");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(Estilo.PRIMARIO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.add(titulo);
        caja.add(Box.createVerticalStrut(12));

        String[] lineas = {
            "Productos: registre platos, bebidas y postres del menú.",
            "Nuevo pedido: seleccione productos, cantidades y modalidad del servicio.",
            "Consulta de pedidos: revise importes y actualice el estado de cada pedido.",
            "Almacenamiento: guarde y recupere la información en archivos binarios."
        };
        for (String linea : lineas) {
            JLabel l = new JLabel(linea);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            caja.add(l);
            caja.add(Box.createVerticalStrut(6));
        }
        panel.add(caja);
        return panel;
    }

    private void cargarAlIniciar() {
        if (!gestor.existenArchivos()) {
            mostrarEstado("Sin datos guardados. Registre productos o cargue datos de ejemplo en Almacenamiento.");
            return;
        }
        StringBuilder sb = new StringBuilder("Datos recuperados: ");
        try {
            sb.append(gestor.recuperarProductos()).append(" productos");
        } catch (PersistenciaException e) {
            sb.append("productos no disponibles (").append(e.getMessage()).append(")");
        }
        try {
            sb.append(", ").append(gestor.recuperarPedidos()).append(" pedidos.");
        } catch (PersistenciaException e) {
            sb.append(", pedidos no disponibles (").append(e.getMessage()).append(")");
        }
        mostrarEstado(sb.toString());
    }

    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea guardar los datos antes de salir?", "Salir",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.CANCEL_OPTION || opcion == JOptionPane.CLOSED_OPTION) {
            return;
        }
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                gestor.guardarProductos();
                gestor.guardarPedidos();
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error al guardar",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        dispose();
        System.exit(0);
    }
}