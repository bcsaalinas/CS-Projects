import java.awt.*;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {
    private String nombreUsuario;

    public VentanaPrincipal(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        setTitle("ToDoList - Inicio");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initGUI();
    }

    private void initGUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 255));
        getContentPane().add(panel);

        JLabel saludo = new JLabel("¡Hola, " + nombreUsuario + "!");
        saludo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        saludo.setAlignmentX(Component.CENTER_ALIGNMENT);
        saludo.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        panel.add(saludo);

        panel.add(crearBotonConEtiqueta("Estudio", "/imagenes/estudio.png"));
        panel.add(crearBotonConEtiqueta("Trabajo", "/imagenes/trabajo.png"));
        panel.add(crearBotonConEtiqueta("Personal", "/imagenes/personal.png"));
    }

    private JPanel crearBotonConEtiqueta(String categoria, String iconoPath) {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setOpaque(false);
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        ImageIcon icono = new ImageIcon(getClass().getResource(iconoPath));
        Image img = icono.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JButton boton = new JButton();
        boton.setIcon(new ImageIcon(img));
        boton.setPreferredSize(new Dimension(70, 70));
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.addActionListener(e -> new VentanaCategoria(categoria).setVisible(true));

        JLabel etiqueta = new JLabel(categoria);
        etiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiqueta.setForeground(new Color(40, 40, 40));

        contenedor.add(boton);
        contenedor.add(Box.createRigidArea(new Dimension(0, 5)));
        contenedor.add(etiqueta);

        return contenedor;
    }
}