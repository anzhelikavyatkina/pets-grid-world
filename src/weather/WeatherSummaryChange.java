package weather;

public class WeatherSummaryChange extends WeatherStateChange {
    private final WeatherAnalyzer.WeatherSummary summary;

    public WeatherSummaryChange(WeatherAnalyzer.WeatherSummary summary) {
        super(-1, -1, null);
        this.summary = summary;
    }

    public WeatherAnalyzer.WeatherSummary getSummary() {
        return summary;
    }
}