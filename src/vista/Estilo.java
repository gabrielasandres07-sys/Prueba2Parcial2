/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author USER
 */


import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

final class Estilo {

    static final Color PRIMARIO = new Color(0x8C2F1B);
    static final Color FONDO_MENU = new Color(0x2F2A26);
    static final Font TITULO = new Font("SansSerif", Font.BOLD, 20);
    static final Font SUBTITULO = new Font("SansSerif", Font.BOLD, 14);
    static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Estilo() {
    }

    static String dinero(double valor) {
        return String.format("$ %,.2f", valor);
    }

    static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(TITULO);
        etiqueta.setForeground(PRIMARIO);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return etiqueta;
    }

    static Border seccion(String texto) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(texto),
                BorderFactory.createEmptyBorder(4, 6, 6, 6));
    }

    static DefaultTableModel modeloSoloLectura(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }
    static JTable tabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        return tabla;
    }
}