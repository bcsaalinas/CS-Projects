
public class TareaEstudio extends Tarea {
    private String materia;

    public TareaEstudio(String titulo, String materia) {
        super(titulo);
        if (materia == null || materia.trim().isEmpty()) {
            throw new IllegalArgumentException("La materia no puede estar vacía.");
        }
        this.materia = materia;
    }

    @Override
    public String getCategoria() {
        return "Estudio";
    }

    @Override
    public String getInfo() {
        return "[Estudio] " + super.getInfo() + " | Materia: " + materia;
    }

   
}