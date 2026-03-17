package game.terrain;

import java.awt.Color;
import java.awt.Image;
import java.util.Optional;
import java.util.Random;

import game.actor.Actor;
import game.core.Stage;

public class Sand extends Terrain {
    public Sand() {
        name = "Sand";
    }

    @Override
    public Color getColor() {
        return new Color(237, 201, 175);
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
        if (rng.nextBoolean()) {
            a.applySkipTurns(1);
            return a.getActorName() + " got stuck in the sand and will skip next turn";
        }
        return a.getActorName() + " trudges through hot sand";
    }
}
