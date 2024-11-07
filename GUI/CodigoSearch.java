package GUI;

import Logica.ConexionBD;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CodigoSearch extends JFrame {

    private JTextField codigoField;
    private JButton buscarButton;
    private JButton accionButton; // Botón de acción extra

    public CodigoSearch() {
        setTitle("Buscar Producto");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Elementos de la interfaz
        codigoField = new JTextField(15);
        buscarButton = new JButton("Buscar Producto");
        accionButton = new JButton("Menú"); // Inicializa el botón de acción extra

        add(new JLabel("Código del Producto:"));
        add(codigoField);
        add(buscarButton);
        add(accionButton); // Agrega el botón de acción extra

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProducto();
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

    private void buscarProducto() {
        String codigoText = codigoField.getText();

        // Validar que el código solo contenga números
        if (!codigoText.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un código válido (solo números).");
            return;
        }

        int codigoProducto = Integer.parseInt(codigoText);
        String query = "SELECT * FROM producto WHERE codigoProducto = ?";

        try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, codigoProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombreProducto");
                    double precio = rs.getDouble("precioUnitario");
                    int cantidad = rs.getInt("cantidadProducto");

                    mostrarDetallesProducto(codigoProducto, nombre, precio, cantidad);
                } else {
                    JOptionPane.showMessageDialog(null, "Producto no encontrado.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar el producto: " + e.getMessage());
        }
    }

    private void mostrarDetallesProducto(int codigo, String nombre, double precio, int cantidad) {
        JFrame detallesFrame = new JFrame("Detalles del Producto");
        detallesFrame.setSize(400, 250);
        detallesFrame.setLayout(new GridLayout(5, 1)); // Usar GridLayout para organización

        // Títulos y contenidos
        JLabel codigoLabel = new JLabel("Código: " + codigo, SwingConstants.CENTER);
        JLabel nombreLabel = new JLabel("Nombre: " + nombre, SwingConstants.CENTER);
        JLabel precioLabel = new JLabel("Precio: $" + String.format("%.2f", precio), SwingConstants.CENTER);
        JLabel cantidadLabel = new JLabel("Cantidad: " + cantidad, SwingConstants.CENTER);

        // Botón OK para cerrar la ventana
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detallesFrame.dispose(); // Cierra la ventana al presionar OK
            }
        });

        // Personalizar la ventana
        detallesFrame.add(codigoLabel);
        detallesFrame.add(nombreLabel);
        detallesFrame.add(precioLabel);
        detallesFrame.add(cantidadLabel);
        detallesFrame.add(okButton); // Agrega el botón OK

        // Estilo de la ventana
        detallesFrame.getContentPane().setBackground(Color.LIGHT_GRAY);
        detallesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Centrar la ventana en la pantalla
        detallesFrame.setLocationRelativeTo(null);
        
        detallesFrame.setVisible(true);
    }
}
