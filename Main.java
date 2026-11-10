import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );

        server.createContext("/", exchange -> {
            String response = "Java Redis App is running!";
            int status = 200;

            if (exchange.getRequestURI().getPath().equals("/error")) {
                response = "Internal Server Error";
                status = 500;
                System.out.println("ERROR: Internal Server Error at /error");
            } else {
                try (Socket socket = new Socket("redis", 6379)) {
                    socket.setSoTimeout(2000);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                    );

                    out.print("*1\r\n$4\r\nPING\r\n");
                    out.flush();

                    String redisResponse = in.readLine();
                    response += " Redis: " + redisResponse;
                    System.out.println("Redis response: " + redisResponse);
                } catch (Exception e) {
                    response += " Redis: unavailable";
                    System.out.println("Redis connection error: " + e.getMessage());
                }
            }

            byte[] body = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });

        server.start();
        System.out.println("Java server started on port 8080");
    }
}
