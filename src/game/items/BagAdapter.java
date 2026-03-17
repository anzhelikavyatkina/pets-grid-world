package game.items;

import java.util.Optional;

public final class BagAdapter<T extends Item> implements Bag {
    private final Inventory<T> inventory;

    public BagAdapter(Inventory<T> inventory) {
        this.inventory = inventory;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public int capacity() {
        return inventory.capacity();
    }

    @Override
    public boolean tryAdd(Item item) {
        return Optional.ofNullable(item)
                .map(inventory::tryAdd)
                .orElse(false);
    }

    public Inventory<T> inventory() {
        return inventory;
    }
}
