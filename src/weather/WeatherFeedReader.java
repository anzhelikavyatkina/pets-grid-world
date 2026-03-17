package weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WeatherFeedReader {
    private static final Map<String, List<WeatherRecord>> buffer = new HashMap<>();

    public static void startFeed(String url,
            Consumer<List<WeatherRecord>> batchProcessor,
            Supplier<Boolean> keepRunning)
            throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "text/event-stream")
                .build();

        CompletableFuture<Void> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body)
                .thenAccept(inputStream -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        String line;
                        while (keepRunning.get() && (line = reader.readLine()) != null) {
                            WeatherRecord record = parseLine(line);
                            if (record != null) {
                                processRecord(record, batchProcessor);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading weather stream: " + e.getMessage());
                    } finally {
                        System.out.println("Weather feed stopped.");
                    }
                });

        future.join();
    }

    private static void processRecord(WeatherRecord record, Consumer<List<WeatherRecord>> batchProcessor) {
        String key = record.timestamp + ":" + record.xCoordinate + "," + record.yCoordinate;
        List<WeatherRecord> records = buffer.computeIfAbsent(key, k -> new ArrayList<>());
        records.add(record);

        if (records.size() == 4) {
            batchProcessor.accept(new ArrayList<>(records));
            buffer.remove(key);
        }
    }

    private static WeatherRecord parseLine(String line) {
        String[] parts = line.split(" ");
        if (parts.length != 5)
            return null;

        try {
            long timestamp = Long.parseLong(parts[0]);
            String attribute = parts[1];
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            double value = Double.parseDouble(parts[4]);
            return new WeatherRecord(timestamp, attribute, x, y, value);
        } catch (Exception e) {
            System.err.println("Invalid line: " + line);
            return null;
        }
    }
}
