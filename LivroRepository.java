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
        // Literatura Brasileira
        salvar(new Livro(null, "Dom Casmurro", "Machado de Assis", "978-85-359-0277-5", "Romance", 1899));
        salvar(new Livro(null, "Memórias Póstumas de Brás Cubas", "Machado de Assis", "978-85-359-0278-2", "Romance", 1881));
        salvar(new Livro(null, "Quincas Borba", "Machado de Assis", "978-85-359-0279-9", "Romance", 1891));
        salvar(new Livro(null, "Grande Sertão: Veredas", "João Guimarães Rosa", "978-85-209-2113-8", "Romance", 1956));
        salvar(new Livro(null, "Sagarana", "João Guimarães Rosa", "978-85-209-2114-5", "Contos", 1946));
        salvar(new Livro(null, "O Cortiço", "Aluísio Azevedo", "978-85-326-0116-0", "Naturalismo", 1890));
        salvar(new Livro(null, "Capitães da Areia", "Jorge Amado", "978-85-290-0001-1", "Romance", 1937));
        salvar(new Livro(null, "Gabriela, Cravo e Canela", "Jorge Amado", "978-85-290-0002-8", "Romance", 1958));
        salvar(new Livro(null, "A Hora da Estrela", "Clarice Lispector", "978-85-359-0300-0", "Romance", 1977));
        salvar(new Livro(null, "Perto do Coração Selvagem", "Clarice Lispector", "978-85-359-0301-7", "Romance", 1943));
        salvar(new Livro(null, "Iracema", "José de Alencar", "978-85-260-0001-2", "Romance", 1865));
        salvar(new Livro(null, "O Guarani", "José de Alencar", "978-85-260-0002-9", "Romance", 1857));
        salvar(new Livro(null, "Vidas Secas", "Graciliano Ramos", "978-85-209-3001-7", "Regionalismo", 1938));
        salvar(new Livro(null, "São Bernardo", "Graciliano Ramos", "978-85-209-3002-4", "Regionalismo", 1934));
        salvar(new Livro(null, "Macunaíma", "Mário de Andrade", "978-85-260-1001-1", "Modernismo", 1928));
        salvar(new Livro(null, "Serafim Ponte Grande", "Oswald de Andrade", "978-85-260-1002-8", "Modernismo", 1933));
        // Literatura Internacional
        salvar(new Livro(null, "1984", "George Orwell", "978-85-359-0001-1", "Distopia", 1949));
        salvar(new Livro(null, "A Revolução dos Bichos", "George Orwell", "978-85-359-0002-8", "Fábula", 1945));
        salvar(new Livro(null, "O Senhor dos Anéis", "J.R.R. Tolkien", "978-85-325-2482-3", "Fantasia", 1954));
        salvar(new Livro(null, "O Hobbit", "J.R.R. Tolkien", "978-85-325-2483-0", "Fantasia", 1937));
        salvar(new Livro(null, "Cem Anos de Solidão", "Gabriel García Márquez", "978-85-209-1001-9", "Realismo Mágico", 1967));
        salvar(new Livro(null, "Crime e Castigo", "Fiódor Dostoiévski", "978-85-209-4001-6", "Romance", 1866));
        salvar(new Livro(null, "O Idiota", "Fiódor Dostoiévski", "978-85-209-4002-3", "Romance", 1869));
        salvar(new Livro(null, "Guerra e Paz", "Liev Tolstói", "978-85-209-4003-0", "Romance Histórico", 1869));
        salvar(new Livro(null, "Anna Karenina", "Liev Tolstói", "978-85-209-4004-7", "Romance", 1878));
        salvar(new Livro(null, "O Processo", "Franz Kafka", "978-85-209-5001-5", "Ficção", 1925));
        salvar(new Livro(null, "A Metamorfose", "Franz Kafka", "978-85-209-5002-2", "Ficção", 1915));
        salvar(new Livro(null, "Dom Quixote", "Miguel de Cervantes", "978-85-209-6001-4", "Romance", 1605));
        salvar(new Livro(null, "Orgulho e Preconceito", "Jane Austen", "978-85-209-7001-3", "Romance", 1813));
        salvar(new Livro(null, "Moby Dick", "Herman Melville", "978-85-209-8001-2", "Aventura", 1851));
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
