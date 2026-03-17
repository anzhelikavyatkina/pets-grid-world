package game.network;

import java.io.IOException;

import weather.WeatherFeedReader;
import weather.WeatherManager;

public class Client {
    private final WeatherManager weatherManager;
    private static final String SERVER_URL = "http://localhost:8080/weather";
    private boolean running = true;
    private Thread thread;

    public Client(WeatherManager weatherManager) {
        this.weatherManager = weatherManager;
    }

    public void start() {
        running = true;
        thread = new Thread(() -> {
            try {
                System.out.println("Connecting to weather server...");
                WeatherFeedReader.startFeed(SERVER_URL, weatherManager::analyzeWeatherBatch, () -> running);
            } catch (IOException | InterruptedException e) {
                System.err.println("Weather client failed: " + e.getMessage());
            }
        }, "WeatherClientThread");
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            System.out.println("Weather client thread interrupted");
        }
    }
}
