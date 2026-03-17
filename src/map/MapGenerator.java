package map;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import game.core.Cell;
import game.core.Grid;
import game.terrain.Flowers;
import game.terrain.Grass;
import game.terrain.Mud;
import game.terrain.Sand;
import game.terrain.Terrain;
import game.terrain.Water;

public final class MapGenerator {
    private final Grid grid;
    private final Random random;
    private final Scatterer scatterer;
    private final Placer<Terrain> terrainOnGrass;
    private final Map<Class<? extends Terrain>, Double> terrainRatios = new HashMap<>();
    {
        terrainRatios.put(Mud.class, 0.15);
        terrainRatios.put(Flowers.class, 0.025);
        terrainRatios.put(Water.class, 0.05);
        terrainRatios.put(Sand.class, 0.2);
    }

    public MapGenerator(Grid g, long seed) {
        this.grid = g;
        this.random = new Random(seed);
        this.scatterer = new Scatterer();
        this.terrainOnGrass = new Placer<>(
                grid,
                (cell, terrain, world) -> cell.getTerrain() instanceof Grass,
                Cell::setTerrain);
    }

    public void generateMap() {
        terrainRatios.forEach((terrainClass, ratio) -> {
            int count = (int) (grid.getTotalCells() * ratio);

            Supplier<Terrain> terrainFactory = () -> {
                try {
                    return terrainClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Cannot create terrain: " + terrainClass.getSimpleName(), e);
                }
            };

            scatterer.scatter(grid, random, terrainFactory, count, terrainOnGrass);
        });
    }
}
