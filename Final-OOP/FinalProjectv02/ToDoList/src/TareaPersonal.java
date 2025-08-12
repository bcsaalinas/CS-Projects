
public class TareaPersonal extends Tarea {
    private String descripcion;

    public TareaPersonal(String titulo, String descripcion) {
        super(titulo);
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        this.descripcion = descripcion;
    }

    @Override
    public String getCategoria() {
        return "Personal";
    }

    @Override
    public String getInfo() {
        return "[Personal] " + super.getInfo() + " | Descripción: " + descripcion;
    }

    
}