package game.core;

import java.util.Random;

public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    public final int dx, dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public static Direction randomDirection(Random random) {
        Direction[] directions = values();
        return directions[random.nextInt(directions.length)];
    }
}
