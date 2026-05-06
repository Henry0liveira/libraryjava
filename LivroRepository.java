package biblioteca.repository;

import biblioteca.model.Livro;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class LivroRepository {

    private static LivroRepository instance;
    private final Map<Long, Livro> livros = new LinkedHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    private LivroRepository() {
        // Dados iniciais de exemplo
        salvar(new Livro(null, "Dom Casmurro", "Machado de Assis", "978-85-359-0277-5", "Romance", 1899));
        salvar(new Livro(null, "Grande Sertão: Veredas", "João Guimarães Rosa", "978-85-209-2113-8", "Romance", 1956));
        salvar(new Livro(null, "O Cortiço", "Aluísio Azevedo", "978-85-326-0116-0", "Naturalismo", 1890));
        salvar(new Livro(null, "Memórias Póstumas de Brás Cubas", "Machado de Assis", "978-85-359-0278-2", "Romance", 1881));
        salvar(new Livro(null, "Capitães da Areia", "Jorge Amado", "978-85-290-0001-1", "Romance", 1937));
    }

    public static LivroRepository getInstance() {
        if (instance == null) instance = new LivroRepository();
        return instance;
    }

    public Livro salvar(Livro livro) {
        if (livro.getId() == null) {
            livro.setId(idCounter.getAndIncrement());
        }
        livros.put(livro.getId(), livro);
        return livro;
    }

    public Optional<Livro> buscarPorId(Long id) {
        return Optional.ofNullable(livros.get(id));
    }

    public List<Livro> buscarTodos() {
        return new ArrayList<>(livros.values());
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        return livros.values().stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Livro> buscarPorAutor(String autor) {
        return livros.values().stream()
                .filter(l -> l.getAutor().toLowerCase().contains(autor.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Livro> buscarPorGenero(String genero) {
        return livros.values().stream()
                .filter(l -> l.getGenero().equalsIgnoreCase(genero))
                .collect(Collectors.toList());
    }

    public List<Livro> buscarDisponiveis() {
        return livros.values().stream()
                .filter(Livro::isDisponivel)
                .collect(Collectors.toList());
    }

    public boolean deletar(Long id) {
        return livros.remove(id) != null;
    }

    public long contarTotal() { return livros.size(); }

    public long contarDisponiveis() {
        return livros.values().stream().filter(Livro::isDisponivel).count();
    }

    public Set<String> listarGeneros() {
        return livros.values().stream()
                .map(Livro::getGenero)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}
