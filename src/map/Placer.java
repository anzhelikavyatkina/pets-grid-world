package map;

import java.util.Optional;
import java.util.function.BiConsumer;

import game.core.Cell;
import game.core.Grid;

public class Placer<T> {
    private final Grid grid;
    private final PlacementStrategy<T> strategy;
    private final BiConsumer<Cell, T> placementAction;

    public Placer(Grid grid, PlacementStrategy<T> strategy, BiConsumer<Cell, T> placementAction) {
        this.grid = grid;
        this.strategy = strategy;
        this.placementAction = placementAction;
    }

    public boolean canPlace(Cell cell, T thing) {
        return Optional.ofNullable(cell)
                .filter(c -> strategy.canPlace(c, thing, grid))
                .isPresent();
    }

    public void place(Cell cell, T thing) {
        Optional.ofNullable(cell).ifPresent(c -> placementAction.accept(c, thing));
    }
}