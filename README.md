# 📚 Biblioteca CRUD — Java

Sistema de gerenciamento de acervo com API REST e interface web.  
**Sem dependências externas** — usa apenas o JDK padrão (Java 11+).

## Estrutura do projeto

```
biblioteca/
├── biblioteca.jar               ← Executável pronto para rodar
├── src/main/java/biblioteca/
│   ├── Main.java                ← Inicia servidor HTTP na porta 8080
│   ├── model/Livro.java         ← Entidade com campos e getters/setters
│   ├── repository/LivroRepository.java  ← Persistência em memória (Map)
│   ├── service/LivroService.java        ← Validação e regras de negócio
│   └── controller/
│       ├── LivroController.java ← Roteamento da API REST
│       └── StaticHandler.java   ← Servidor de arquivos estáticos
└── src/main/resources/static/
    └── index.html               ← Interface web completa
```

## Como executar

### Opção 1 — JAR (mais rápido)
```bash
java -jar biblioteca.jar
```

### Opção 2 — Compilar do zero
```bash
mkdir out
javac -d out $(find src -name "*.java")
java -cp out biblioteca.Main
```

Acesse: **http://localhost:8080**

## Como hospedar tudo

GitHub Pages não hospeda a API Java, então use um serviço que execute o backend. Este repositório já inclui `nixpacks.toml` para publicar no Railway.

Passos no Railway:
1. Faça push do repositório para o GitHub.
2. No Railway, crie um novo projeto a partir do repositório.
3. O Railway vai ler o arquivo `nixpacks.toml` e usar:
   - Build: `javac -d out *.java`
   - Start: `java -cp out biblioteca.Main`
4. Publique o serviço.

O servidor usa a variável `PORT` quando ela existe, então ele funciona em hosts que atribuem a porta automaticamente.

Se preferir Render, o arquivo `render.yaml` continua disponível.

## Endpoints da API REST

| Método   | Rota                               | Ação                              |
|----------|------------------------------------|-----------------------------------|
| `GET`    | `/api/livros`                      | Listar todos os livros            |
| `GET`    | `/api/livros?busca=X&tipo=titulo`  | Buscar (titulo / autor / genero)  |
| `GET`    | `/api/livros/{id}`                 | Buscar livro por ID               |
| `POST`   | `/api/livros`                      | Criar novo livro                  |
| `PUT`    | `/api/livros/{id}`                 | Atualizar livro existente         |
| `PATCH`  | `/api/livros/{id}/disponibilidade` | Alternar disponível / emprestado  |
| `DELETE` | `/api/livros/{id}`                 | Remover livro                     |
| `GET`    | `/api/stats`                       | Estatísticas do acervo            |

## Exemplo de payload (POST/PUT)

```json
{
  "titulo": "Dom Casmurro",
  "autor": "Machado de Assis",
  "isbn": "978-85-359-0277-5",
  "genero": "Romance",
  "anoPublicacao": 1899,
  "disponivel": true
}
```

## Requisitos
- Java 11 ou superior
- Sem Maven, Gradle ou frameworks externos
