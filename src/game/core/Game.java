package game.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import game.actor.Actor;

public class Game {
    private final Stage stage;
    private final Random random = new Random();
    private Class<? extends Actor> selected;
    private final List<Actor> turnOrder = new ArrayList<Actor>();
    private int turnIndex = 0;
    private Direction playerDir = Direction.UP;

    public Game(Stage stage) {
        this.stage = stage;
    }

    public Stage stage() {
        return stage;
    }

    public Random random() {
        return random;
    }

    public Direction getPlayerDir() {
        return playerDir;
    }

    public void setSelected(Class<? extends Actor> actorType) {
        this.selected = actorType;
    }

    public Class<? extends Actor> getSelected() {
        return selected;
    }

    public Actor currentActor() {
        return turnOrder.isEmpty() ? null : turnOrder.get(turnIndex);
    }

    public void initTurnOrder() {
        turnOrder.clear();
        stage.getActors().stream()
                .filter(actor -> actor != null)
                .forEach(turnOrder::add);
        Collections.shuffle(turnOrder, random);
        turnIndex = 0;
    }

    public List<Actor> getTurnOrder() {
        return turnOrder;
    }

    public void nextTurn() {
        if (!turnOrder.isEmpty()) {
            turnIndex = (turnIndex + 1) % turnOrder.size();
        }
    }

    public void setPlayerDir(Direction dir) {
        this.playerDir = dir;
    }

    public void clearTurnOrder() {
        turnOrder.clear();
        turnIndex = 0;
        selected = null;
    }
}
