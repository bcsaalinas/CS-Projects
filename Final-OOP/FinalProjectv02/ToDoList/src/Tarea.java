public abstract class Tarea {
    private String titulo;
    private boolean completada;

    public Tarea(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        this.titulo = titulo;
        this.completada = false;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean estaCompletada() {
        return completada;
    }

    public void marcarCompletada() {
        this.completada = true;
    }

    public abstract String getCategoria();

    public String getInfo() {
        return titulo + (completada ? " (completada)" : "");
    }

   
}