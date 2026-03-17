package game.terrain;

import java.awt.Color;
import java.awt.Image;
import java.util.Optional;
import java.util.Random;

import game.actor.Actor;
import game.core.Stage;

public class Grass extends Terrain {
    public Grass() {
        name = "Grass";
    }

    @Override
    public Color getColor() {
        return new Color(102, 204, 0);
    }

    @Override
    public Optional<Image> getTexture() {
        return Optional.empty();
    }

    @Override
    public boolean isWalkable(Actor actor) {
        return true;
    }

    @Override
    public String onEnter(Actor a, Stage s, Random r) {
        return a.getActorName() + " walks safely on grass";
    }
}