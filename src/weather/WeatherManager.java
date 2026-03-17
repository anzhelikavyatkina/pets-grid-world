package weather;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import game.core.Grid;
import weather.observer.WeatherObserver;
import weather.observer.WeatherSubject;
import weather.state.WeatherStateBehavior;

public class WeatherManager implements WeatherSubject {

    private final List<WeatherObserver> observers = new ArrayList<>();
    private final Map<String, CellWeatherData> cellData = new ConcurrentHashMap<>();
    private final List<WeatherRecord> analysisBuffer = new ArrayList<>();
    private Grid grid;
    private static final int ANALYSIS_WINDOW = 28;

    @Override
    public void registerObserver(WeatherObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(WeatherObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(WeatherStateChange event) {
        observers.forEach(o -> o.update(event));
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public synchronized void analyzeWeatherBatch(List<WeatherRecord> batch) {
        if (grid == null || batch == null || batch.isEmpty())
            return;

        batch.forEach(this::handleSingleRecord);

        synchronized (analysisBuffer) {
            analysisBuffer.addAll(batch);

            if (analysisBuffer.size() >= ANALYSIS_WINDOW) {
                List<WeatherRecord> snapshot = new ArrayList<>(analysisBuffer);
                analysisBuffer.clear();

                new Thread(() -> analyzeSnapshot(snapshot), "WeatherAnalysisThread").start();
            }
        }
    }

    private void analyzeSnapshot(List<WeatherRecord> snapshot) {
        WeatherAnalyzer.WeatherSummary summary = WeatherAnalyzer.trendsAnalysis(snapshot, grid.getSize());
        notifyObservers(new WeatherSummaryChange(summary));
    }

    private void handleSingleRecord(WeatherRecord record) {
        String key = record.xCoordinate + "," + record.yCoordinate;
        CellWeatherData data = cellData.computeIfAbsent(key, k -> new CellWeatherData());

        switch (record.attribute.toLowerCase()) {
            case "rain" -> data.rain = record.value;
            case "windx" -> data.windx = record.value;
            case "windy" -> data.windy = record.value;
            case "temp" -> data.temp = record.value;
        }

        data.received.add(record.attribute.toLowerCase());

        if (data.received.containsAll(List.of("rain", "windx", "windy", "temp"))) {
            WeatherStateBehavior state = WeatherStateFactory.createState(
                    data.rain, data.windx, data.windy, data.temp);

            grid.findByWorldCoords(record.xCoordinate, record.yCoordinate)
                    .filter(cell -> !cell.hasOccupant())
                    .ifPresent(cell -> {
                        cell.setWeatherState(state);
                        notifyObservers(new WeatherStateChange(
                                record.xCoordinate, record.yCoordinate, state));
                    });

            data.received.clear();
        }
    }

    private static class CellWeatherData {
        double rain, windx, windy, temp;
        Set<String> received = new HashSet<>();
    }
}
