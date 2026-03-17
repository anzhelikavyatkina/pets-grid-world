package map;

import java.util.Random;
import java.util.function.Supplier;

import game.core.Cell;
import game.core.Grid;

public class Scatterer {
    public <T> void scatter(Grid grid, Random random, Supplier<T> factory, int count, Placer<T> placer) {

        final int maxAttempts = count * 10;
        int placed = 0, attempts = 0;

        while (placed < count && attempts < maxAttempts) {
            attempts++;
            Cell cell = grid.getRandomCell(random);
            T thing = factory.get();

            if (placer.canPlace(cell, thing)) {
                placer.place(cell, thing);
                placed++;
            }
        }
    }
}
