package weather.state;

import java.awt.Color;
import java.awt.Image;
import java.util.Optional;
import javax.swing.ImageIcon;

import game.actor.Actor;
import game.items.Item;
import game.terrain.Terrain;
import game.terrain.Water;

public class HeatWeatherState implements WeatherStateBehavior {

    @Override
    public String getName() {
        return "Heat";
    }

    @Override
    public boolean isDangerous() {
        return true;
    }

    @Override
    public void affectItem(Item item) {
        item.setTint(Color.RED);
        item.setAlpha(150);
    }

    @Override
    public Color getColor(Terrain terrain) {
        return terrain.getColor();
    }

    @Override
    public Optional<Image> getTexture(Terrain terrain) {
        if (terrain instanceof Water) {
            return Optional.of(new ImageIcon("images/dry_water_overlay.png").getImage());
        }
        return Optional.of(new ImageIcon("images/heat_overlay.png").getImage());
    }

    @Override
    public boolean isWalkable(Actor actor, Terrain terrain) {
        return true;
    }
}
