package weather.state;

import java.awt.Color;
import java.awt.Image;
import java.util.Optional;
import javax.swing.ImageIcon;

import game.actor.Actor;
import game.items.Item;
import game.terrain.Terrain;

public class RainWeatherState implements WeatherStateBehavior {

    @Override
    public String getName() {
        return "Rain";
    }

    @Override
    public boolean isDangerous() {
        return false;
    }

    @Override
    public void affectItem(Item item) {
        item.setAlpha(180);
    }

    @Override
    public Color getColor(Terrain terrain) {
        return terrain.getColor();
    }

    @Override
    public Optional<Image> getTexture(Terrain terrain) {
        Image rainOverlay = new ImageIcon("images/rain_overlay.png").getImage();
        return Optional.ofNullable(rainOverlay);
    }

    @Override
    public boolean isWalkable(Actor actor, Terrain terrain) {
        return true;
    }
}
