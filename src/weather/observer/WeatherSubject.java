package weather.observer;

import weather.WeatherStateChange;

public interface WeatherSubject {
    void registerObserver(WeatherObserver o);

    void removeObserver(WeatherObserver o);

    void notifyObservers(WeatherStateChange event);
}
