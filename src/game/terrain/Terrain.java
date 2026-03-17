package game.terrain;

import java.awt.Color;
import java.awt.Image;
import java.util.Optional;
import java.util.Random;

import game.actor.Actor;
import game.core.Stage;

public abstract class Terrain {
    protected String name = "Unknown terrain";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract Color getColor();

    public abstract Optional<Image> getTexture();

    public boolean isWalkable(Actor actor) {
        return true;
    }

    public String onEnter(Actor actor, Stage stage, Random rng) {
        return actor.getActorName() + " walks on " + name;
    }
}