package weather.state;

import java.awt.*;
import javax.swing.ImageIcon;
import java.util.Optional;
import java.util.Random;

import game.actor.Actor;
import game.items.Item;
import game.terrain.Terrain;

public class StormWeatherState implements WeatherStateBehavior {
    @Override
    public Color getColor(Terrain terrain) {
        return terrain.getColor().darker();
    }

    @Override
    public Optional<Image> getTexture(Terrain terrain) {
        return Optional.ofNullable(new ImageIcon("images/storm_overlay.png").getImage());
    }

    @Override
    public boolean isWalkable(Actor actor, Terrain terrain) {
        return terrain.isWalkable(actor);
    }

    @Override
    public String getName() {
        return "Storm";
    }

    @Override
    public boolean isDangerous() {
        return true;
    }

    @Override
    public void affectItem(Item item) {
        Random rng = new Random();
        item.nudge(rng.nextInt(21) - 10, rng.nextInt(11) - 5);
        item.setAlpha(120);
    }
}
