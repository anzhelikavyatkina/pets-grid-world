package weather.state;

import java.awt.*;
import java.util.Optional;

import game.actor.Actor;
import game.items.Item;
import game.terrain.Terrain;

public class SunnyWeatherState implements WeatherStateBehavior {
    @Override
    public Color getColor(Terrain terrain) {
        return terrain.getColor();
    }

    @Override
    public Optional<Image> getTexture(Terrain terrain) {
        return terrain.getTexture();
    }

    @Override
    public boolean isWalkable(Actor actor, Terrain terrain) {
        return terrain.isWalkable(actor);
    }

    @Override
    public String getName() {
        return "Sunny";
    }

    @Override
    public boolean isDangerous() {
        return false;
    }

    @Override
    public void affectItem(Item item) {
    }
}
