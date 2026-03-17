package game.terrain;

import java.awt.Color;
import java.awt.Image;
import java.util.Optional;
import java.util.Random;

import game.actor.Actor;
import game.core.Stage;

public class Water extends Terrain {
    public Water() {
        name = "Water";
    }

    @Override
    public Color getColor() {
        return new Color(64, 196, 255);
    }

    @Override
    public Optional<Image> getTexture() {
        return Optional.empty();
    }

    @Override
    public boolean isWalkable(Actor a) {
        return a.canWalkOnWater();
    }

    @Override
    public String onEnter(Actor a, Stage s, Random rng) {
        if (a.canWalkOnWater())
            return a.getActorName() + " gracefully moves over the water";
        return a.getActorName() + " cannot move into water";
    }
}
