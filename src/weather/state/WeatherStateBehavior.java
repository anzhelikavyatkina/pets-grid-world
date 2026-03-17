package weather.state;

import java.awt.*;
import java.util.Optional;

import game.actor.Actor;
import game.items.Item;
import game.terrain.Terrain;

public interface WeatherStateBehavior {
    Color getColor(Terrain terrain);

    Optional<Image> getTexture(Terrain terrain);

    boolean isWalkable(Actor actor, Terrain terrain);

    String getName();

    boolean isDangerous();

    void affectItem(Item item);
}
