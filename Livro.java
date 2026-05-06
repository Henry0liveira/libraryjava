package biblioteca.model;

import java.time.LocalDate;

public class Livro {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private String genero;
    private int anoPublicacao;
    private boolean disponivel;
    private LocalDate dataCadastro;

    public Livro() {
        this.disponivel = true;
        this.dataCadastro = LocalDate.now();
    }

    public Livro(Long id, String titulo, String autor, String isbn, String genero, int anoPublicacao) {
        this();
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.anoPublicacao = anoPublicacao;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    @Override
    public String toString() {
        return String.format("Livro{id=%d, titulo='%s', autor='%s', isbn='%s', genero='%s', ano=%d, disponivel=%b}",
                id, titulo, autor, isbn, genero, anoPublicacao, disponivel);
    }
}
