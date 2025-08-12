import java.awt.*;
import java.util.List;
import javax.swing.*;

public class VentanaCategoria extends JFrame {
    private GestorTareas gestor;
    private DefaultListModel<String> modelo;
    private JList<String> lista;
    private String categoria;

    public VentanaCategoria(String categoria) {
        this.categoria = categoria;
        this.gestor = GestorTareasCompartido.getInstancia();
        modelo = new DefaultListModel<>();
        lista = new JList<>(modelo);
        lista.setFixedCellHeight(30);

        setTitle("Tareas de " + categoria);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        initGUI();
        actualizarLista();
    }

    private void initGUI() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(lista);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        botones.setBackground(Color.WHITE);

        ImageIcon iconAgregar = new ImageIcon(getClass().getResource("/imagenes/agregar.png"));
        Image imgAgregar = iconAgregar.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JButton btnAgregar = new JButton(new ImageIcon(imgAgregar));
        btnAgregar.setToolTipText("Agregar tarea");
        btnAgregar.setPreferredSize(new Dimension(40, 40));
        btnAgregar.setContentAreaFilled(false);
        btnAgregar.setBorderPainted(false);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setMargin(new Insets(2,2,2,2));
        btnAgregar.addActionListener(e -> agregarTarea());

        ImageIcon iconCompletar = new ImageIcon(getClass().getResource("/imagenes/completar.png"));
        Image imgCompletar = iconCompletar.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JButton btnCompletar = new JButton(new ImageIcon(imgCompletar));
        btnCompletar.setToolTipText("Completar tarea");
        btnCompletar.setPreferredSize(new Dimension(40, 40));
        btnCompletar.setContentAreaFilled(false);
        btnCompletar.setBorderPainted(false);
        btnCompletar.setFocusPainted(false);
        btnCompletar.setMargin(new Insets(2,2,2,2));
        btnCompletar.addActionListener(e -> completarTarea());

        botones.add(btnAgregar);
        botones.add(btnCompletar);

        JPanel etiquetas = new JPanel(new GridLayout(1, 2));
        etiquetas.setBackground(Color.WHITE);
        etiquetas.add(new JLabel("Agregar", SwingConstants.CENTER));
        etiquetas.add(new JLabel("Completar", SwingConstants.CENTER));

        JPanel seccionInferior = new JPanel(new BorderLayout());
        seccionInferior.setBackground(Color.WHITE);
        seccionInferior.add(botones, BorderLayout.CENTER);
        seccionInferior.add(etiquetas, BorderLayout.SOUTH);

        panel.add(seccionInferior, BorderLayout.SOUTH);
        add(panel);
    }

    private void agregarTarea() {
        try {
            String titulo = JOptionPane.showInputDialog(this, "Título:");
            if (titulo == null || titulo.trim().isEmpty()) throw new IllegalArgumentException("Título vacío.");

            if (categoria.equals("Estudio")) {
                String materia = JOptionPane.showInputDialog(this, "Materia:");
                if (materia == null || materia.trim().isEmpty()) throw new IllegalArgumentException("Materia vacía.");
                gestor.agregarTarea(new TareaEstudio(titulo, materia));
            } else if (categoria.equals("Trabajo")) {
                String empresa = JOptionPane.showInputDialog(this, "Empresa:");
                String[] opciones = {"1", "2", "3"};
                String prioridadStr = (String) JOptionPane.showInputDialog(this, "Prioridad (1 Alta - 3 Baja):", "Prioridad",
                        JOptionPane.PLAIN_MESSAGE, null, opciones, "2");
                int prioridad = Integer.parseInt(prioridadStr);
                gestor.agregarTarea(new TareaTrabajo(titulo, empresa, prioridad));
            } else if (categoria.equals("Personal")) {
                String descripcion = JOptionPane.showInputDialog(this, "Descripción:");
                if (descripcion == null || descripcion.trim().isEmpty()) throw new IllegalArgumentException("Descripción vacía.");
                gestor.agregarTarea(new TareaPersonal(titulo, descripcion));
            }

            actualizarLista();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completarTarea() {
        int index = lista.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea.");
            return;
        }

        List<Tarea> tareas = gestor.getTareas();
        int count = 0;
        for (int i = 0; i < tareas.size(); i++) {
            if (tareas.get(i).getCategoria().equals(categoria)) {
                if (count == index) {
                    gestor.eliminarTarea(i);
                    actualizarLista();
                    return;
                }
                count++;
            }
        }
    }

    private void actualizarLista() {
        modelo.clear();
        for (Tarea t : gestor.getTareas()) {
            if (t.getCategoria().equals(categoria)) {
                modelo.addElement(t.getInfo());
            }
        }
    }
}