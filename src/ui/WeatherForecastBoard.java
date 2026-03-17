package ui;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import weather.WeatherAnalyzer;
import weather.WeatherStateChange;
import weather.WeatherSummaryChange;
import weather.observer.WeatherObserver;

public class WeatherForecastBoard extends Board implements WeatherObserver {

    private Map<String, Double> averages = Map.of();
    private int analyzedRegions = 0;
    private int dangerCount = 0;
    private List<String> analyzedCells = List.of();
    private boolean hasReceivedData = false;

    public WeatherForecastBoard(int panelX, int panelY, int panelWidth, int canvasHeight) {
        super(panelX, panelY, panelWidth, canvasHeight);
    }

    public void displaySummary(WeatherAnalyzer.WeatherSummary summary) {
        Optional.ofNullable(summary).ifPresent(s -> {
            this.hasReceivedData = true;
            this.averages = s.averages();
            this.dangerCount = s.dangerCount();
            this.analyzedRegions = s.analyzedCells().size();
            this.analyzedCells = s.analyzedCells();
        });
    }

    @Override
    public void update(WeatherStateChange event) {
        if (event instanceof WeatherSummaryChange summaryEvent) {
            WeatherAnalyzer.WeatherSummary summary = summaryEvent.getSummary();

            this.hasReceivedData = true;
            this.averages = summary.averages();
            this.dangerCount = summary.dangerCount();
            this.analyzedRegions = summary.analyzedCells().size();
            this.analyzedCells = summary.analyzedCells();
        }
    }

    @Override
    public void paint(Graphics g) {
        int panelHeight = 200;
        fillPanelRect(g, new Color(230, 245, 255), panelX, panelY, panelWidth, panelHeight);
        drawTitle(g, "Meteorological Center", panelX + 12, panelY + 22, 18f);

        if (!hasReceivedData) {
            g.setColor(new Color(90, 90, 90));
            g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
            String msg1 = "The station is initializing...";
            String msg2 = "Awaiting first weather data feed.";
            String msg3 = "Please stand by.";
            int textX = panelX + 20;
            int textY = panelY + 80;
            g.drawString(msg1, textX, textY);
            g.drawString(msg2, textX, textY + 20);
            g.drawString(msg3, textX, textY + 40);
            return;
        }

        g.setFont(g.getFont().deriveFont(Font.PLAIN, 14f));
        int[] y = { panelY + 55 };

        g.drawString("Latest news from the analytical center!", panelX + 12, y[0]);
        y[0] += 25;
        g.drawString(String.format("Average Rainfall: %.3f", averages.getOrDefault("rain", 0.0)), panelX + 12, y[0]);
        y[0] += 20;

        double avgWind = (averages.getOrDefault("windx", 0.0)
                + averages.getOrDefault("windy", 0.0)) / 2.0;
        g.drawString(String.format("Average Wind Power: %.3f", avgWind), panelX + 12, y[0]);
        y[0] += 20;

        g.drawString(String.format("Average Temperature: %.3f",
                averages.getOrDefault("temp", 0.0)), panelX + 12, y[0]);
        y[0] += 25;

        g.drawString(String.format("Analyzed regions: %d", analyzedRegions), panelX + 12, y[0]);
        y[0] += 20;

        g.setColor(Color.DARK_GRAY);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));

        if (analyzedRegions == 0) {
            g.setColor(new Color(100, 100, 100));
            g.setFont(g.getFont().deriveFont(Font.ITALIC, 12f));
            g.drawString("The station is currently analyzing data from other regions...", panelX + 12, y[0]);
        } else {
            String cellsInline = analyzedCells.stream()
                    .limit(12)
                    .map(c -> "(" + c + ")")
                    .collect(Collectors.joining(" "));
            if (analyzedCells.size() > 12)
                cellsInline += " ...";
            g.drawString("Analyzed cells: " + cellsInline, panelX + 12, y[0]);
        }
        y[0] += 20;

        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        g.setColor(new Color(180, 30, 30));
        g.drawString(String.format("Hazard alerts: %d", dangerCount), panelX + 12, y[0]);
    }

    public void clear() {
        this.hasReceivedData = false;
        this.averages = Map.of();
        this.analyzedRegions = 0;
        this.dangerCount = 0;
        this.analyzedCells = List.of();
    }
}
