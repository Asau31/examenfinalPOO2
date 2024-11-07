package GUI;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.sql.*;
import java.io.File;
import java.io.FileOutputStream;
import GUI.menu;

public class ExportarPDF extends JFrame {
    private JTextField txtTitulo;
    private JTextField txtFecha;
    private JButton btnExportar;
    private JButton btnSeleccionarRuta;
    private JButton btnAccionExtra;
    private String rutaSeleccionada = null; // Ruta predeterminada nula para forzar selección

    public ExportarPDF() {
        // Configuración de la ventana
        setTitle("Exportar Base de Datos a PDF");
        setSize(550, 250); // Tamaño ajustado para que todos los componentes quepan bien
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Campo de texto para el título
        JLabel lblTitulo = new JLabel("Título:");
        lblTitulo.setBounds(50, 20, 80, 30);
        add(lblTitulo);
        txtTitulo = new JTextField();
        txtTitulo.setBounds(130, 20, 350, 30);
        add(txtTitulo);

        // Campo de texto para la fecha
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setBounds(50, 60, 80, 30);
        add(lblFecha);
        txtFecha = new JTextField();
        txtFecha.setBounds(130, 60, 350, 30);
        add(txtFecha);

        // Botón para seleccionar la ruta de guardado
        btnSeleccionarRuta = new JButton("Seleccionar Ruta");
        btnSeleccionarRuta.setBounds(50, 120, 150, 30);
        add(btnSeleccionarRuta);

        btnSeleccionarRuta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarRuta();
            }
        });

        // Botón para exportar
        btnExportar = new JButton("Exportar a PDF");
        btnExportar.setBounds(210, 120, 130, 30);
        add(btnExportar);

        // Acción del botón Exportar
        btnExportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (rutaSeleccionada == null) {
                    JOptionPane.showMessageDialog(null, "Por favor, selecciona una ruta antes de exportar.");
                } else {
                    exportarPDF();
                }
            }
        });

        // Botón extra con acción personalizada
        btnAccionExtra = new JButton("Menu");
        btnAccionExtra.setBounds(350, 120, 130, 30);
        add(btnAccionExtra);

        // Aquí puedes agregar la acción deseada para btnAccionExtra
        btnAccionExtra.addActionListener(new ActionListener() {
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
        fileChooser.setDialogTitle("Guardar reporte PDF");
        fileChooser.setSelectedFile(new File("ReporteBD.pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            rutaSeleccionada = archivoSeleccionado.getAbsolutePath() + (archivoSeleccionado.getAbsolutePath().endsWith(".pdf") ? "" : ".pdf");
            JOptionPane.showMessageDialog(this, "Ruta seleccionada: " + rutaSeleccionada);
        }
    }

    private void exportarPDF() {
        String titulo = txtTitulo.getText();
        String fecha = txtFecha.getText();
        Document documento = new Document();
        
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(rutaSeleccionada));
            documento.open();

            // Título y fecha en el PDF
            Paragraph parrafoTitulo = new Paragraph(titulo, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(parrafoTitulo);

            Paragraph parrafoFecha = new Paragraph("Fecha: " + fecha);
            parrafoFecha.setAlignment(Element.ALIGN_CENTER);
            documento.add(parrafoFecha);

            documento.add(new Paragraph("\n"));

            // Conexión a la base de datos y obtención de datos
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/basedatos", "root", "12345678");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM producto");

            // Tabla en el PDF
            PdfPTable tabla = new PdfPTable(5);
            tabla.addCell("Código");
            tabla.addCell("Nombre");
            tabla.addCell("Precio Unitario");
            tabla.addCell("Cantidad");
            tabla.addCell("Fecha de Vencimiento");

            while (rs.next()) {
                tabla.addCell(rs.getString("codigoProducto"));
                tabla.addCell(rs.getString("nombreProducto"));
                tabla.addCell(rs.getString("precioUnitario"));
                tabla.addCell(rs.getString("cantidadProducto"));
                tabla.addCell(rs.getString("fechaVencimiento"));
            }
            
            documento.add(tabla);
            JOptionPane.showMessageDialog(this, "PDF generado con éxito en: " + rutaSeleccionada);

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar el PDF.");
        } finally {
            documento.close();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExportarPDF ventana = new ExportarPDF();
            ventana.setLocationRelativeTo(null); // Centra la ventana
            ventana.setVisible(true);
        });
    }
}
