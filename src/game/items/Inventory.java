package game.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Inventory<T extends Item> {
    private final List<T> bag = new ArrayList<>();
    private final int capacity;
    private final Class<T> allowedType;

    public Inventory(Class<T> allowedType, int capacity) {
        this.allowedType = allowedType;
        this.capacity = capacity;
    }

    public boolean add(T item) {
        return bag.size() < capacity && bag.add(item);
    }

    public boolean tryAdd(Item item) {
        return Optional.ofNullable(item)
                .filter(allowedType::isInstance)
                .map(allowedType::cast)
                .map(this::add)
                .orElse(false);
    }

    public int size() {
        return bag.size();
    }

    public int capacity() {
        return capacity;
    }

    public Class<T> allowedType() {
        return allowedType;
    }
}
