package biblioteca.controller;

import biblioteca.model.Livro;
import biblioteca.service.LivroService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class LivroController implements HttpHandler {

    private final LivroService service = new LivroService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        try {
            if (method.equals("OPTIONS")) {
                sendResponse(exchange, 200, "");
                return;
            }

            // Rota: /api/livros
            if (path.equals("/api/livros")) {
                switch (method) {
                    case "GET":
                        handleGetAll(exchange);
                        break;
                    case "POST":
                        handleCreate(exchange);
                        break;
                    default:
                        sendResponse(exchange, 405, erro("Método não permitido"));
                }
            }
            // Rota: /api/livros/{id}
            else if (path.matches("/api/livros/\\d+")) {
                long id = Long.parseLong(path.split("/")[3]);
                switch (method) {
                    case "GET":
                        handleGetById(exchange, id);
                        break;
                    case "PUT":
                        handleUpdate(exchange, id);
                        break;
                    case "DELETE":
                        handleDelete(exchange, id);
                        break;
                    default:
                        sendResponse(exchange, 405, erro("Método não permitido"));
                }
            }
            // Rota: /api/livros/{id}/disponibilidade
            else if (path.matches("/api/livros/\\d+/disponibilidade")) {
                long id = Long.parseLong(path.split("/")[3]);
                if (method.equals("PATCH")) handleToggle(exchange, id);
                else sendResponse(exchange, 405, erro("Método não permitido"));
            }
            // Rota: /api/stats
            else if (path.equals("/api/stats") && method.equals("GET")) {
                handleStats(exchange);
            }
            else {
                sendResponse(exchange, 404, erro("Rota não encontrada"));
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, erro(e.getMessage()));
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String busca = params.get("busca");
        String tipo  = params.get("tipo");

        List<Livro> livros = service.buscar(busca, tipo);
        sendJson(exchange, 200, livrosToJson(livros));
    }

    private void handleGetById(HttpExchange exchange, long id) throws IOException {
        service.buscarPorId(id).ifPresentOrElse(
            l -> {
                try { sendJson(exchange, 200, livroToJson(l)); }
                catch (IOException e) { throw new UncheckedIOException(e); }
            },
            () -> {
                try { sendResponse(exchange, 404, erro("Livro não encontrado")); }
                catch (IOException e) { throw new UncheckedIOException(e); }
            }
        );
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Livro livro = jsonToLivro(body, new Livro());
        Livro criado = service.criar(livro);
        sendJson(exchange, 201, livroToJson(criado));
    }

    private void handleUpdate(HttpExchange exchange, long id) throws IOException {
        String body = readBody(exchange);
        Livro livro = jsonToLivro(body, new Livro());
        Livro atualizado = service.atualizar(id, livro);
        sendJson(exchange, 200, livroToJson(atualizado));
    }

    private void handleDelete(HttpExchange exchange, long id) throws IOException {
        service.deletar(id);
        sendJson(exchange, 200, "{\"mensagem\":\"Livro removido com sucesso\"}");
    }

    private void handleToggle(HttpExchange exchange, long id) throws IOException {
        Livro livro = service.alternarDisponibilidade(id);
        sendJson(exchange, 200, livroToJson(livro));
    }

    private void handleStats(HttpExchange exchange) throws IOException {
        long total = service.contarTotal();
        long disponiveis = service.contarDisponiveis();
        String generos = service.listarGeneros().stream()
                .map(g -> "\"" + g + "\"")
                .collect(Collectors.joining(",", "[", "]"));
        String json = String.format(
            "{\"total\":%d,\"disponiveis\":%d,\"indisponiveis\":%d,\"generos\":%s}",
            total, disponiveis, total - disponiveis, generos
        );
        sendJson(exchange, 200, json);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String livroToJson(Livro l) {
        return String.format(
            "{\"id\":%d,\"titulo\":%s,\"autor\":%s,\"isbn\":%s,\"genero\":%s,\"anoPublicacao\":%d,\"disponivel\":%b,\"dataCadastro\":\"%s\"}",
            l.getId(), q(l.getTitulo()), q(l.getAutor()), q(l.getIsbn()),
            q(l.getGenero()), l.getAnoPublicacao(), l.isDisponivel(), l.getDataCadastro()
        );
    }

    private String livrosToJson(List<Livro> lista) {
        return lista.stream().map(this::livroToJson).collect(Collectors.joining(",", "[", "]"));
    }

    private Livro jsonToLivro(String json, Livro livro) {
        livro.setTitulo(extractString(json, "titulo"));
        livro.setAutor(extractString(json, "autor"));
        livro.setIsbn(extractString(json, "isbn"));
        livro.setGenero(extractString(json, "genero"));
        livro.setDisponivel(extractBoolean(json, "disponivel", true));
        String ano = extractString(json, "anoPublicacao");
        if (ano != null && !ano.isBlank()) {
            try { livro.setAnoPublicacao(Integer.parseInt(ano)); } catch (NumberFormatException ignored) {}
        }
        return livro;
    }

    private String extractString(String json, String key) {
        // Match "key":"value" or "key":value (numbers)
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("\"" + key + "\"\\s*:\\s*(?:\"([^\"]*)\"|([\\d]+))")
            .matcher(json);
        if (m.find()) return m.group(1) != null ? m.group(1) : m.group(2);
        return null;
    }

    private boolean extractBoolean(String json, String key, boolean defaultVal) {
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("\"" + key + "\"\\s*:\\s*(true|false)")
            .matcher(json);
        if (m.find()) return Boolean.parseBoolean(m.group(1));
        return defaultVal;
    }

    private String q(String s) { return s == null ? "null" : "\"" + s.replace("\"", "\\\"") + "\""; }

    private String erro(String msg) { return "{\"erro\":\"" + msg + "\"}"; }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendJson(HttpExchange exchange, int code, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        sendResponse(exchange, code, json);
    }

    private void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }
}
