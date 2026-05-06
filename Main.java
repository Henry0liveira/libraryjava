package biblioteca;

import biblioteca.controller.LivroController;
import biblioteca.controller.StaticHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        int porta = 8080;
        String staticDir = Paths.get("src/main/resources/static").toAbsolutePath().toString();

        HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);
        server.createContext("/api/", new LivroController());
        server.createContext("/",    new StaticHandler(staticDir));
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   📚 Biblioteca CRUD - Iniciado!     ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf ("║   🌐 http://localhost:%d              ║%n", porta);
        System.out.println("║   📖 API: /api/livros                ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("\nPressione Ctrl+C para encerrar.");
    }
}
