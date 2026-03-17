package weather;

import weather.state.ColdWeatherState;
import weather.state.HeatWeatherState;
import weather.state.RainWeatherState;
import weather.state.StormWeatherState;
import weather.state.SunnyWeatherState;
import weather.state.WeatherStateBehavior;
import weather.state.WindWeatherState;

public class WeatherStateFactory {

    public static WeatherStateBehavior createState(double rain, double windX, double windY, double temp) {
        double wind = Math.sqrt(windX * windX + windY * windY);

        if (rain > 0.49 && wind > 0.74)
            return new StormWeatherState();

        if (rain > 0.46 && wind > 0.7)
            return new RainWeatherState();

        if (wind > 0.75)
            return new WindWeatherState();

        if (temp >= 0.56)
            return new HeatWeatherState();

        if (temp < 0.52)
            return new ColdWeatherState();

        return new SunnyWeatherState();

    }

    public static WeatherStateBehavior createFromString(String name) {
        if (name == null)
            return new SunnyWeatherState();

        return switch (name.toLowerCase()) {
            case "rain" -> new RainWeatherState();
            case "wind" -> new WindWeatherState();
            case "cold" -> new ColdWeatherState();
            case "heat" -> new HeatWeatherState();
            case "storm" -> new StormWeatherState();
            default -> new SunnyWeatherState();
        };
    }
}
