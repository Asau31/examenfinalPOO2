package GUI;

import Logica.ConexionBD;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ActualizarProducto extends JFrame {

    private JTextField codigoField;
    private JButton buscarButton;
    private JButton accionButton; // Botón extra

    public ActualizarProducto() {
        setTitle("Actualizar Producto");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        codigoField = new JTextField(15);
        buscarButton = new JButton("Buscar Producto");
        accionButton = new JButton("Menú"); // Inicializa el botón extra

        // Mejora de alineación
        add(new JLabel("Código del Producto:"));
        add(codigoField);
        add(buscarButton);
        add(accionButton); // Añade el botón extra

        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProducto();
            }
        });

        // Lógica para el botón extra
        accionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Acción adicional aquí
                menu Menu = new menu();
                Menu.setVisible(true);
                Menu.setLocationRelativeTo(null);
                dispose();
            }
        });
    }

    private void buscarProducto() {
        int codigoProducto = Integer.parseInt(codigoField.getText());
        String query = "SELECT * FROM producto WHERE codigoProducto = ?";

        try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, codigoProducto);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombreProducto");
                    double precio = rs.getDouble("precioUnitario");
                    int cantidad = rs.getInt("cantidadProducto");
                    String message = String.format("Producto encontrado:\n🔑 Código: %d\n📝 Nombre: %s\n💲 Precio: %.2f\n📊 Cantidad: %d",
                            codigoProducto, nombre, precio, cantidad);

                    int response = JOptionPane.showConfirmDialog(null, message + "\n\n¿Deseas actualizar este producto?", "Confirmar actualización", JOptionPane.YES_NO_OPTION);

                    if (response == JOptionPane.YES_OPTION) {
                        abrirVentanaActualizar(codigoProducto, nombre, precio, cantidad);
                    } else {
                        JOptionPane.showMessageDialog(null, "Búsqueda cancelada.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "⚠️ Producto no encontrado.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "❌ Error al buscar el producto: " + e.getMessage());
        }
    }

    private void abrirVentanaActualizar(int codigo, String nombre, double precio, int cantidad) {
        JFrame actualizarFrame = new JFrame("Modificar Producto");
        actualizarFrame.setSize(300, 250);
        actualizarFrame.setLayout(new FlowLayout());

        JTextField nombreField = new JTextField(nombre, 15);
        JTextField precioField = new JTextField(String.valueOf(precio), 15);
        JTextField cantidadField = new JTextField(String.valueOf(cantidad), 15);
        JButton guardarButton = new JButton("Guardar Cambios");

        // Mejora de alineación
        actualizarFrame.add(new JLabel("Nombre:"));
        actualizarFrame.add(nombreField);
        actualizarFrame.add(new JLabel("Precio:"));
        actualizarFrame.add(precioField);
        actualizarFrame.add(new JLabel("Cantidad:"));
        actualizarFrame.add(cantidadField);
        actualizarFrame.add(guardarButton);

        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarProducto(codigo, nombreField.getText(), Double.parseDouble(precioField.getText()), Integer.parseInt(cantidadField.getText()));
                actualizarFrame.dispose(); // Cierra la ventana después de actualizar
            }
        });

        actualizarFrame.setVisible(true);
        actualizarFrame.setLocationRelativeTo(null);
    }

    private void actualizarProducto(int codigo, String nombre, double precio, int cantidad) {
        String query = "UPDATE producto SET nombreProducto = ?, precioUnitario = ?, cantidadProducto = ? WHERE codigoProducto = ?";
        try (Connection con = ConexionBD.conectar(); PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, nombre);
            pst.setDouble(2, precio);
            pst.setInt(3, cantidad);
            pst.setInt(4, codigo);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "✅ Producto actualizado con éxito.");
            } else {
                JOptionPane.showMessageDialog(null, "⚠️ No se pudo actualizar el producto.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "❌ Error al actualizar el producto: " + e.getMessage());
        }
    }
}
