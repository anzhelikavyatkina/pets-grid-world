package game.items;

public interface Bag {
    int size();

    int capacity();

    boolean tryAdd(Item item);
}
