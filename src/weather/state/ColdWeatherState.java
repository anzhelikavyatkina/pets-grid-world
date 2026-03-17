package weather.state;

import java.awt.*;
import java.util.Optional;
import javax.swing.ImageIcon;

import game.actor.Actor;
import game.items.Item;
import game.terrain.Terrain;
import game.terrain.Water;

public class ColdWeatherState implements WeatherStateBehavior {
    @Override
    public Color getColor(Terrain terrain) {
        return terrain.getColor();
    }

    @Override
    public Optional<Image> getTexture(Terrain terrain) {
        if (terrain instanceof Water) {
            return Optional.of(new ImageIcon("images/ice_overlay.png").getImage());
        }
        return Optional.of(new ImageIcon("images/snow_overlay.png").getImage());
    }

    @Override
    public boolean isWalkable(Actor actor, Terrain terrain) {
        return true;
    }

    @Override
    public String getName() {
        return "Cold";
    }

    @Override
    public boolean isDangerous() {
        return true;
    }

    @Override
    public void affectItem(Item item) {
        item.setTint(Color.CYAN);
    }
}
