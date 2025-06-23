package ui.models;

public class Anotacao {
    private String titulo;
    private String descricao;
    private String horta;
    private String autor;
    private String data;

    public Anotacao(String titulo, String descricao, String horta, String autor, String data) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.horta = horta;
        this.autor = autor;
        this.data = data;
    }

    // Getters
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getHorta() { return horta; }
    public String getAutor() { return autor; }
    public String getData() { return data; }

    // Setters
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setHorta(String horta) { this.horta = horta; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setData(String data) { this.data = data; }
} 