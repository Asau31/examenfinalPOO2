package GUI;

import Logica.ConexionBD;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EliminarProducto extends JFrame {

    private JTextField codigoField;
    private JButton eliminarButton;
    private JButton accionButton; // Botón de acción extra

    public EliminarProducto() {
        setTitle("Eliminar Producto");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Elementos de la interfaz
        codigoField = new JTextField(15);
        eliminarButton = new JButton("Eliminar Producto");
        accionButton = new JButton("Menú"); // Inicializa el botón de acción extra

        add(new JLabel("Código del Producto:"));
        add(codigoField);
        add(eliminarButton);
        add(accionButton); // Agrega el botón de acción extra

        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProducto();
            }
        });

        // Lógica para el botón de acción extra
        accionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menu Menu = new menu();
                Menu.setVisible(true);
                Menu.setLocationRelativeTo(null);
                dispose();
            }
        });
    }

    private void eliminarProducto() {
        String codigoString = codigoField.getText().trim(); // Obtener el código del producto

        // Validar que el código no esté vacío
        if (codigoString.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un código de producto válido.");
            return;
        }

        // Confirmación casual
        int casualConfirm = JOptionPane.showConfirmDialog(null, 
                "¿Estás seguro de que deseas eliminar el producto con código: " + codigoString + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);
        
        if (casualConfirm == JOptionPane.NO_OPTION) {
            return; // Si el usuario cancela la eliminación
        }

        // Confirmación seria
        int seriousConfirm = JOptionPane.showConfirmDialog(null, 
                "¡ATENCIÓN! Esta acción no se puede deshacer. ¿Estás seguro de que deseas continuar?",
                "Confirmación Seria",
                JOptionPane.YES_NO_OPTION);

        if (seriousConfirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM producto WHERE codigoProducto = ?";

            try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
                pst.setInt(1, Integer.parseInt(codigoString));
                int filasAfectadas = pst.executeUpdate();
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(null, "Producto eliminado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró un producto con ese código.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al eliminar el producto: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EliminarProducto().setVisible(true);
            }
        });
    }
}
