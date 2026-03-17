package weather;

public class WeatherRecord {
    public long timestamp;
    public String attribute;
    public int xCoordinate;
    public int yCoordinate;
    public double value;

    public WeatherRecord(long timestamp, String attribute, int xCoordinate, int yCoordinate, double value) {
        this.timestamp = timestamp;
        this.attribute = attribute;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%d, %d) = %.2f", timestamp, attribute, xCoordinate, yCoordinate, value);
    }
}