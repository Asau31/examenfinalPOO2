package GUI;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Facturacion extends JFrame {
    private JTextField txtCliente;
    private JTextField txtCUI;
    private JTextField txtLugar;
    private JTextField txtCodigoProducto;
    private JTextField txtCantidad;
    private JButton btnGenerarFactura;
    private JButton btnSeleccionarRuta;
    private JButton btnExtra; // Extra button
    private String rutaSeleccionada = null;

    public Facturacion() {
        // Configuración de la ventana
        setTitle("Facturación");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Estilo para el panel de la ventana
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 240, 240));
        panel.setBounds(0, 0, 600, 400);
        panel.setLayout(null);
        add(panel);

        // Campo de texto para el nombre del cliente
        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setBounds(20, 20, 80, 30);
        panel.add(lblCliente);
        txtCliente = new JTextField();
        txtCliente.setBounds(100, 20, 450, 30);
        panel.add(txtCliente);

        // Campo de texto para el CUI del cliente
        JLabel lblCUI = new JLabel("CUI:");
        lblCUI.setBounds(20, 60, 80, 30);
        panel.add(lblCUI);
        txtCUI = new JTextField();
        txtCUI.setBounds(100, 60, 450, 30);
        panel.add(txtCUI);

        // Campo de texto para el lugar
        JLabel lblLugar = new JLabel("Lugar:");
        lblLugar.setBounds(20, 100, 80, 30);
        panel.add(lblLugar);
        txtLugar = new JTextField();
        txtLugar.setBounds(100, 100, 450, 30);
        panel.add(txtLugar);

        // Campo de texto para el código del producto
        JLabel lblCodigoProducto = new JLabel("Código Producto:");
        lblCodigoProducto.setBounds(20, 140, 120, 30);
        panel.add(lblCodigoProducto);
        txtCodigoProducto = new JTextField();
        txtCodigoProducto.setBounds(140, 140, 450, 30);
        panel.add(txtCodigoProducto);

        // Campo de texto para la cantidad
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setBounds(20, 180, 80, 30);
        panel.add(lblCantidad);
        txtCantidad = new JTextField();
        txtCantidad.setBounds(100, 180, 450, 30);
        panel.add(txtCantidad);

        // Botón para seleccionar la ruta de guardado
        btnSeleccionarRuta = new JButton("Seleccionar Ruta");
        btnSeleccionarRuta.setBounds(20, 240, 150, 30);
        btnSeleccionarRuta.setMargin(new java.awt.Insets(5, 2, 5, 2)); // Ajustar el margen
        panel.add(btnSeleccionarRuta);

        btnSeleccionarRuta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarRuta();
            }
        });

        // Botón para generar la factura
        btnGenerarFactura = new JButton("Generar Factura");
        btnGenerarFactura.setBounds(180, 240, 150, 30);
        btnGenerarFactura.setMargin(new java.awt.Insets(5, 2, 5, 2)); // Ajustar el margen
        panel.add(btnGenerarFactura);

        btnGenerarFactura.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rutaSeleccionada == null) {
                    JOptionPane.showMessageDialog(null, "Por favor, selecciona una ruta antes de generar la factura.");
                } else {
                    generarFacturaPDF();
                }
            }
        });

        // Extra button next to "Generar Factura"
        btnExtra = new JButton("Menu");
        btnExtra.setBounds(340, 240, 150, 30); // Position it next to "Generar Factura"
        btnExtra.setMargin(new java.awt.Insets(5, 2, 5, 2)); // Adjust margin
        panel.add(btnExtra);
        btnExtra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               menu Menu = new menu();
               Menu.setVisible(true);
               Menu.setLocationRelativeTo(null);
               dispose();
                

            }
        });
    }

    private void seleccionarRuta() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar factura PDF");
        fileChooser.setSelectedFile(new File("Factura.pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            rutaSeleccionada = archivoSeleccionado.getAbsolutePath() + (archivoSeleccionado.getAbsolutePath().endsWith(".pdf") ? "" : ".pdf");
            JOptionPane.showMessageDialog(this, "Ruta seleccionada: " + rutaSeleccionada);
        }
    }

    private void generarFacturaPDF() {
        String cliente = txtCliente.getText();
        String cui = txtCUI.getText();
        String lugar = txtLugar.getText();
        String codigoProducto = txtCodigoProducto.getText();
        int cantidadComprada = Integer.parseInt(txtCantidad.getText());
        String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        Document documento = new Document();
        
        try {
            // Conexión a la base de datos y validación de stock
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/basedatos", "root", "12345678");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM producto WHERE codigoProducto = '" + codigoProducto + "'");

            if (rs.next()) {
                // Obtener detalles del producto
                String nombreProducto = rs.getString("nombreProducto");
                double precioUnitario = rs.getDouble("precioUnitario");
                int stockActual = rs.getInt("cantidadProducto");

                // Validar si hay suficiente stock
                if (cantidadComprada > stockActual) {
                    JOptionPane.showMessageDialog(this, "Stock insuficiente. Solo hay " + stockActual + " unidades disponibles.");
                    return;
                }

                // Actualizar el stock en la base de datos
                int nuevoStock = stockActual - cantidadComprada;
                String updateQuery = "UPDATE producto SET cantidadProducto = ? WHERE codigoProducto = ?";
                PreparedStatement ps = con.prepareStatement(updateQuery);
                ps.setInt(1, nuevoStock);
                ps.setString(2, codigoProducto);
                ps.executeUpdate();

                // Generar el PDF de la factura
                PdfWriter.getInstance(documento, new FileOutputStream(rutaSeleccionada));
                documento.open();

                // Encabezado con el nombre de la empresa
                Paragraph empresa = new Paragraph("CodiCorp", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE));
                empresa.setAlignment(Element.ALIGN_CENTER);
                documento.add(empresa);

                documento.add(new Paragraph(" ")); // Espacio

                // Información de la factura
                Paragraph fecha = new Paragraph("Fecha: " + fechaActual, FontFactory.getFont(FontFactory.HELVETICA, 12));
                Paragraph cuiCliente = new Paragraph("CUI: " + cui, FontFactory.getFont(FontFactory.HELVETICA, 12));
                Paragraph lugarFactura = new Paragraph("Lugar: " + lugar, FontFactory.getFont(FontFactory.HELVETICA, 12));

                documento.add(fecha);
                documento.add(cuiCliente);
                documento.add(lugarFactura);

                documento.add(new Paragraph(" ")); // Espacio

                // Detalles del producto en tabla
                PdfPTable tabla = new PdfPTable(5); // 5 columnas
                tabla.setWidthPercentage(100);
                tabla.setSpacingBefore(10);
                
                // Encabezados de la tabla
                tabla.addCell("Código Producto");
                tabla.addCell("Producto");
                tabla.addCell("Cantidad");
                tabla.addCell("Precio Unitario");
                tabla.addCell("Subtotal");
                
                // Calcular subtotal
                double subtotal = cantidadComprada * precioUnitario;
                
                // Agregar detalles del producto en la tabla
                tabla.addCell(codigoProducto);
                tabla.addCell(nombreProducto);
                tabla.addCell(String.valueOf(cantidadComprada));
                tabla.addCell("Q" + precioUnitario);
                tabla.addCell("Q" + subtotal);
                
                // Total de la factura
                double total = subtotal;
                
                // Agregar tabla al documento
                documento.add(tabla);
                
                documento.add(new Paragraph(" ")); // Espacio

                // Resumen total
                Paragraph parrafoTotal = new Paragraph("Total: Q" + total, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
                parrafoTotal.setAlignment(Element.ALIGN_RIGHT);
                documento.add(parrafoTotal);

                // Cerrar documento PDF
                documento.close();
                JOptionPane.showMessageDialog(this, "Factura generada con éxito en la ruta: " + rutaSeleccionada);
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.");
            }

            // Cerrar la conexión
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar la factura: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Facturacion().setVisible(true);
            }
        });
    }
}
