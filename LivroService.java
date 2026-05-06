package biblioteca.service;

import biblioteca.model.Livro;
import biblioteca.repository.LivroRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LivroService {

    private final LivroRepository repository;

    public LivroService() {
        this.repository = LivroRepository.getInstance();
    }

    public Livro criar(Livro livro) {
        validar(livro);
        livro.setId(null);
        return repository.salvar(livro);
    }

    public Livro atualizar(Long id, Livro livroAtualizado) {
        Livro existente = repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Livro com ID " + id + " não encontrado."));
        validar(livroAtualizado);
        livroAtualizado.setId(id);
        livroAtualizado.setDataCadastro(existente.getDataCadastro());
        return repository.salvar(livroAtualizado);
    }

    public Optional<Livro> buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public List<Livro> buscarTodos() {
        return repository.buscarTodos();
    }

    public List<Livro> buscar(String termo, String tipo) {
        if (termo == null || termo.isBlank()) return buscarTodos();
        return switch (tipo != null ? tipo : "titulo") {
            case "autor"  -> repository.buscarPorAutor(termo);
            case "genero" -> repository.buscarPorGenero(termo);
            default       -> repository.buscarPorTitulo(termo);
        };
    }

    public List<Livro> buscarDisponiveis() {
        return repository.buscarDisponiveis();
    }

    public boolean deletar(Long id) {
        if (repository.buscarPorId(id).isEmpty())
            throw new RuntimeException("Livro com ID " + id + " não encontrado.");
        return repository.deletar(id);
    }

    public Livro alternarDisponibilidade(Long id) {
        Livro livro = repository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Livro com ID " + id + " não encontrado."));
        livro.setDisponivel(!livro.isDisponivel());
        return repository.salvar(livro);
    }

    public long contarTotal() { return repository.contarTotal(); }
    public long contarDisponiveis() { return repository.contarDisponiveis(); }
    public Set<String> listarGeneros() { return repository.listarGeneros(); }

    private void validar(Livro livro) {
        if (livro.getTitulo() == null || livro.getTitulo().isBlank())
            throw new IllegalArgumentException("Título é obrigatório.");
        if (livro.getAutor() == null || livro.getAutor().isBlank())
            throw new IllegalArgumentException("Autor é obrigatório.");
        if (livro.getIsbn() == null || livro.getIsbn().isBlank())
            throw new IllegalArgumentException("ISBN é obrigatório.");
        if (livro.getAnoPublicacao() < 1 || livro.getAnoPublicacao() > 2100)
            throw new IllegalArgumentException("Ano de publicação inválido.");
    }
}
