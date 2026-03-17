package game.terrain;

import java.awt.Color;
import java.awt.Image;
import java.util.Optional;
import java.util.Random;
import javax.swing.ImageIcon;

import game.actor.Actor;
import game.core.Stage;

public class Flowers extends Terrain {
    public Flowers() {
        name = "Flowers";
    }

    @Override
    public Color getColor() {
        return new Color(102, 204, 0);
    }

    @Override
    public Optional<Image> getTexture() {
        return Optional.of(new ImageIcon("images/flowers.png").getImage());
    }

    @Override
    public boolean isWalkable(Actor a) {
        return true;
    }

    @Override
    public String onEnter(Actor a, Stage s, Random rng) {
        if (!a.isResting()) {
            a.increaseCuriosity(1);
            return a.getActorName() + " smells the flowers and feels more curious.";
        }
        return a.getActorName() + " smells the flowers and feels refreshed";
    }
}
