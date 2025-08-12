import java.util.ArrayList;

public class GestorTareas {
    private ArrayList<Tarea> tareas;

    public GestorTareas() {
        tareas = new ArrayList<>();
    }

    public void agregarTarea(Tarea tarea) {
        if (tarea == null) {
            throw new IllegalArgumentException("La tarea no puede ser nula.");
        }
        for (Tarea t : tareas) {
            if (t.getTitulo().equalsIgnoreCase(tarea.getTitulo())) {
                throw new IllegalArgumentException("Ya existe una tarea con ese título.");
            }
        }
        tareas.add(tarea);
    }

    public void eliminarTarea(int indice) {
        if (indice < 0 || indice >= tareas.size()) {
            throw new IndexOutOfBoundsException("Índice inválido para eliminar.");
        }
        tareas.remove(indice);
    }

    public ArrayList<Tarea> getTareas() {
        return tareas;
    }

    public int getCantidadTareas() {
        return tareas.size();
    }
}