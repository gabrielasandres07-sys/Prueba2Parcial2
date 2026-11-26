/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package prueba2parcialii;



/**
 *
 * @author USER
 */
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import excepciones.PersistenciaException;
import servicio.GestorRestaurante;
import vista.VentanaPrincipal;
public class Prueba2ParcialII {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(() -> {
            try {
                VentanaPrincipal ventana = new VentanaPrincipal(new GestorRestaurante());
                ventana.setVisible(true);
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error al iniciar",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    
}
