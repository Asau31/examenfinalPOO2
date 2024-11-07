package GUI;

import Logica.ConexionBD;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class NombreSearch extends JFrame {

    private JTextField nombreField;
    private JButton buscarButton;
    private JButton accionButton; // Botón de acción extra

    public NombreSearch() {
        setTitle("Buscar Producto por Nombre");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Elementos de la interfaz
        nombreField = new JTextField(15);
        buscarButton = new JButton("Buscar Producto");
        accionButton = new JButton("Menú"); // Inicializa el botón de acción extra

        add(new JLabel("Nombre del Producto:"));
        add(nombreField);
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
        String nombreProducto = nombreField.getText().trim(); // Obtener el nombre del producto

        // Validar que el nombre no esté vacío
        if (nombreProducto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un nombre de producto válido.");
            return;
        }

        String query = "SELECT * FROM producto WHERE LOWER(nombreProducto) = LOWER(?)"; // Comparar sin importar mayúsculas o minúsculas

        try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, nombreProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int codigo = rs.getInt("codigoProducto");
                    double precio = rs.getDouble("precioUnitario");
                    int cantidad = rs.getInt("cantidadProducto");

                    mostrarDetallesProducto(codigo, nombreProducto, precio, cantidad);
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
