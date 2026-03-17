package game.terrain;

import java.awt.Color;
import java.awt.Image;
import java.util.Optional;
import java.util.Random;

import game.actor.Actor;
import game.core.Stage;

public class Mud extends Terrain {
    public Mud() {
        name = "Mud";
    }

    @Override
    public Color getColor() {
        return new Color(121, 85, 72);
    }

    @Override
    public Optional<Image> getTexture() {
        return Optional.empty();
    }

    @Override
    public boolean isWalkable(Actor a) {
        return true;
    }

    @Override
    public String onEnter(Actor a, Stage s, Random rng) {
        if (rng.nextInt(4) == 0) {
            a.applySkipTurns(1);
            return a.getActorName() + " got stuck in mud and will skip a turn";
        }
        return a.getActorName() + " trudges through mud";
    }
}