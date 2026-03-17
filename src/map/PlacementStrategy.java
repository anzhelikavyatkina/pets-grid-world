package map;

import game.core.Cell;
import game.core.Grid;

@FunctionalInterface
public interface PlacementStrategy<T> {
    boolean canPlace(Cell cell, T thing, Grid grid);
}