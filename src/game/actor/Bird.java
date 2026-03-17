package game.actor;

import game.actor.strategy.BirdReactionStrategy;
import game.core.Cell;
import game.items.BagAdapter;
import game.items.Berry;
import game.items.Inventory;
import game.items.Item;

public class Bird extends Actor {
    public static final String NAME = "Bird";

    public Bird(Cell spawnLocation) {
        super(
                "images/bird.png",
                spawnLocation,
                new BagAdapter<>(new Inventory<>(Berry.class, DEFAULT_BAG_CAPACITY)),
                new BirdReactionStrategy());
    }

    @Override
    public String getActorName() {
        return NAME;
    }

    @Override
    public boolean canWalkOnWater() {
        return true;
    }

    @Override
    public Class<? extends Item> preferredItemType() {
        return Berry.class;
    }
}
