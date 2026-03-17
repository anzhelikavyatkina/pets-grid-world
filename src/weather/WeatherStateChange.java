package weather;

import weather.state.WeatherStateBehavior;

public class WeatherStateChange {
    private final int x;
    private final int y;
    private final WeatherStateBehavior newState;

    public WeatherStateChange(int x, int y, WeatherStateBehavior newState) {
        this.x = x;
        this.y = y;
        this.newState = newState;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public WeatherStateBehavior getNewState() {
        return newState;
    }
}
