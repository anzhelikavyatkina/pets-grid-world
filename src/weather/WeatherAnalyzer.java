package weather;

import java.util.*;
import java.util.stream.Collectors;

import game.core.Cell;
import weather.state.WeatherStateBehavior;

public class WeatherAnalyzer {

        public static WeatherSummary trendsAnalysis(List<WeatherRecord> batch, int gridSize) {
                if (batch == null || batch.isEmpty()) {
                        return new WeatherSummary("No data available for weather analysis.");
                }

                int half = gridSize / 2;

                List<WeatherRecord> filteredBatch = batch.stream()
                                .filter(r -> {
                                        int col = half + r.xCoordinate;
                                        int row = half - r.yCoordinate;
                                        return col >= 0 && col < gridSize && row >= 0 && row < gridSize;
                                })
                                .toList();

                if (filteredBatch.isEmpty()) {
                        return new WeatherSummary("No in-bounds weather data to analyze.");
                }

                Map<String, Double> averages = filteredBatch.stream()
                                .collect(Collectors.groupingBy(r -> r.attribute.toLowerCase(),
                                                Collectors.averagingDouble(r -> r.value)));

                Map<String, WeatherStateBehavior> cellStates = filteredBatch.stream()
                                .collect(Collectors.groupingBy(
                                                r -> Cell.convertToLabel(r.xCoordinate, r.yCoordinate, gridSize)))
                                .entrySet().stream()
                                .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                e -> {
                                                        Map<String, Double> values = e.getValue().stream()
                                                                        .collect(Collectors.toMap(
                                                                                        r -> r.attribute.toLowerCase(),
                                                                                        r -> r.value,
                                                                                        (a, b) -> b));
                                                        return WeatherStateFactory.createState(
                                                                        values.getOrDefault("rain", 0.0),
                                                                        values.getOrDefault("windx", 0.0),
                                                                        values.getOrDefault("windy", 0.0),
                                                                        values.getOrDefault("temp", 0.0));
                                                }));

                long dangerCount = cellStates.values().stream()
                                .filter(WeatherStateBehavior::isDangerous)
                                .count();

                List<String> analyzedCells = new ArrayList<>(cellStates.keySet());

                StringBuilder report = new StringBuilder("Weather Report\n");
                report.append("Analyzed records: ").append(filteredBatch.size()).append("\n");
                averages.forEach((attr, avg) -> report.append("Average ").append(attr).append(": ")
                                .append(String.format("%.3f", avg)).append("\n"));
                report.append("Total cells: ").append(cellStates.size()).append("\n");
                report.append("Dangerous zones: ").append(dangerCount).append("\n");

                return new WeatherSummary(report.toString(), averages, (int) dangerCount, analyzedCells);
        }

        public record WeatherSummary(String text, Map<String, Double> averages, int dangerCount,
                        List<String> analyzedCells) {
                public WeatherSummary(String text) {
                        this(text, Map.of(), 0, List.of());
                }
        }
}
