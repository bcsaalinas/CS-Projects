import java.awt.*;
import javax.swing.*;

public class PantallaInicio extends JFrame {
    private JTextField campoNombre;
    private JButton botonEntrar;

    public PantallaInicio() {
        setTitle("Bienvenido a ToDoList");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initGUI();
    }

    private void initGUI() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(230, 240, 255));
        getContentPane().add(panel);

        JLabel etiqueta = new JLabel("¿Cómo te llamas?");
        etiqueta.setBounds(120, 50, 200, 30);
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        etiqueta.setForeground(new Color(33, 37, 41));
        panel.add(etiqueta);

        campoNombre = new JTextField();
        campoNombre.setBounds(90, 100, 220, 30);
        panel.add(campoNombre);

        ImageIcon iconoEntrar = new ImageIcon(getClass().getResource("/imagenes/entrar.png"));
        Image imgEntrar = iconoEntrar.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        botonEntrar = new JButton(new ImageIcon(imgEntrar));
        botonEntrar.setBounds(180, 160, 40, 40);
        botonEntrar.setContentAreaFilled(false);
        botonEntrar.setBorderPainted(false);
        botonEntrar.setFocusPainted(false);
        panel.add(botonEntrar);

        botonEntrar.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            if (!nombre.isEmpty()) {
                dispose();
                new VentanaPrincipal(nombre).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa tu nombre.");
            }
        });
    }

}