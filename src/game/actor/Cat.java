package game.actor;

import game.items.Fish;
import game.actor.strategy.CatReactionStrategy;
import game.core.Cell;
import game.items.Item;
import game.items.BagAdapter;
import game.items.Inventory;

public class Cat extends Actor {
    public static final String NAME = "Cat";

    public Cat(Cell spawnLocation) {
        super(
                "images/cat.png",
                spawnLocation,
                new BagAdapter<>(new Inventory<>(Fish.class, DEFAULT_BAG_CAPACITY)),
                new CatReactionStrategy());
    }

    @Override
    public String getActorName() {
        return NAME;
    }

    @Override
    public Class<? extends Item> preferredItemType() {
        return Fish.class;
    }
}