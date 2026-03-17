package game.actor;

import game.actor.strategy.DogReactionStrategy;
import game.core.Cell;
import game.items.Bone;
import game.items.Item;
import game.items.BagAdapter;
import game.items.Inventory;

public class Dog extends Actor {
    public static final String NAME = "Dog";

    public Dog(Cell spawnLocation) {
        super(
                "images/dog.png",
                spawnLocation,
                new BagAdapter<>(new Inventory<>(Bone.class, DEFAULT_BAG_CAPACITY)),
                new DogReactionStrategy());
    }

    @Override
    public String getActorName() {
        return NAME;
    }

    @Override
    public Class<? extends Item> preferredItemType() {
        return Bone.class;
    }
}