package weather.observer;

import weather.WeatherStateChange;

public interface WeatherObserver {
    void update(WeatherStateChange event);
}
