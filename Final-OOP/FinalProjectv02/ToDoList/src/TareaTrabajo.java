
public class TareaTrabajo extends Tarea {
    private String empresa;
    private int prioridad;

    public TareaTrabajo(String titulo, String empresa, int prioridad) {
        super(titulo);
        if (empresa == null || empresa.trim().isEmpty()) {
            throw new IllegalArgumentException("La empresa no puede estar vacía.");
        }
        if (prioridad < 1 || prioridad > 3) {
            throw new IllegalArgumentException("La prioridad debe ser 1, 2 o 3.");
        }
        this.empresa = empresa;
        this.prioridad = prioridad;
    }

    @Override
    public String getCategoria() {
        return "Trabajo";
    }

    private String prioridadTexto() {
        switch (prioridad) {
            case 1: return "Alta";
            case 2: return "Media";
            case 3: return "Baja";
            default: return "Desconocida";
        }
    }

    @Override
    public String getInfo() {
        return "[Trabajo] " + super.getInfo() + " | Empresa: " + empresa + " | Prioridad: " + prioridadTexto();
    }

   
}