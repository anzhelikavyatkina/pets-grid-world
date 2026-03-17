package game.network;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;

public class MockWeatherServer {
    private final int port;
    private HttpServer server;
    private final Random random = new Random();

    public MockWeatherServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/weather", this::handleWeatherRequest);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        System.out.println("MockWeatherServer started at http://localhost:" + port + "/weather");
    }

    private void handleWeatherRequest(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(200, 0);

        OutputStream os = exchange.getResponseBody();

        try {
            while (true) {
                String batch = generateBatch();
                os.write(batch.getBytes(StandardCharsets.UTF_8));
                os.flush();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Mock weather client disconnected.");
        } finally {
            os.close();
        }
    }

    private String generateBatch() {
        StringBuilder sb = new StringBuilder();
        long timestamp = System.currentTimeMillis() / 1000;

        for (int i = 0; i < 7; i++) {
            int x = random.nextInt(15) - 7;
            int y = random.nextInt(15) - 7;

            double rain = random.nextDouble();
            double windx = random.nextDouble();
            double windy = random.nextDouble();
            double temp = random.nextDouble();

            sb.append(timestamp).append(" rain ").append(x).append(" ").append(y).append(" ")
                    .append(String.format(Locale.US, "%.2f", rain)).append("\n");

            sb.append(timestamp).append(" windx ").append(x).append(" ").append(y).append(" ")
                    .append(String.format(Locale.US, "%.2f", windx)).append("\n");

            sb.append(timestamp).append(" windy ").append(x).append(" ").append(y).append(" ")
                    .append(String.format(Locale.US, "%.2f", windy)).append("\n");

            sb.append(timestamp).append(" temp ").append(x).append(" ").append(y).append(" ")
                    .append(String.format(Locale.US, "%.2f", temp)).append("\n");
        }

        return sb.toString();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("MockWeatherServer stopped.");
        }
    }
}