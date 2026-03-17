package weather.state;

import java.awt.*;
import java.util.Optional;
import java.util.Random;

import javax.swing.ImageIcon;

import game.actor.Actor;
import game.items.Item;
import game.terrain.Terrain;

public class WindWeatherState implements WeatherStateBehavior {
    @Override
    public Color getColor(Terrain terrain) {
        return terrain.getColor();
    }

    @Override
    public Optional<Image> getTexture(Terrain terrain) {
        return Optional.of(new ImageIcon("images/wind_overlay.png").getImage());
    }

    @Override
    public boolean isWalkable(Actor actor, Terrain terrain) {
        return terrain.isWalkable(actor);
    }

    @Override
    public String getName() {
        return "Wind";
    }

    @Override
    public boolean isDangerous() {
        return false;
    }

    @Override
    public void affectItem(Item item) {
        Random rng = new Random();
        item.nudge(rng.nextInt(7) - 3, rng.nextInt(5) - 2);
    }
}
