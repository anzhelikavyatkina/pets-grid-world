package game.actor.strategy;

import java.util.*;
import java.util.stream.Collectors;

import game.actor.Actor;
import game.core.Cell;
import game.core.Direction;
import game.core.Game;
import game.items.Item;

public class SmartMoveStrategy implements MoveStrategy {
    private Cell lastCell = null;

    @Override
    public Direction chooseDirection(Actor actor, Game game) {
        List<Item> items = game.stage().getItems();

        Item target = items.stream()
                .filter(i -> actor.preferredItemType().isInstance(i))
                .min(Comparator.comparingDouble(i -> distance(actor.getLocation(), i.getLocation())))
                .orElse(null);

        if (target == null || distance(actor.getLocation(), target.getLocation()) < 1.1) {
            return Direction.randomDirection(game.random());
        }

        int dx = Integer.compare(target.getLocation().getColumnAsInt(),
                actor.getLocation().getColumnAsInt());
        int dy = Integer.compare(target.getLocation().getRow(),
                actor.getLocation().getRow());

        List<Direction> preferredOrder = (Math.abs(dx) > Math.abs(dy))
                ? List.of(dx < 0 ? Direction.LEFT : Direction.RIGHT,
                        dy < 0 ? Direction.UP : Direction.DOWN)
                : List.of(dy < 0 ? Direction.UP : Direction.DOWN,
                        dx < 0 ? Direction.LEFT : Direction.RIGHT);

        List<Direction> validDirections = preferredOrder.stream()
                .filter(dir -> game.stage().canMove(actor, dir))
                .collect(Collectors.toList());

        if (validDirections.isEmpty()) {
            return Direction.randomDirection(game.random());
        }

        Direction chosen = validDirections.stream()
                .filter(dir -> !wouldGoBack(actor, dir))
                .findFirst()
                .orElse(validDirections.get(0));
        lastCell = actor.getLocation();

        return chosen;
    }

    private double distance(Cell a, Cell b) {
        int dx = a.getColumnAsInt() - b.getColumnAsInt();
        int dy = a.getRow() - b.getRow();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private boolean wouldGoBack(Actor actor, Direction dir) {
        if (lastCell == null)
            return false;
        int nx = actor.getLocation().getColumnAsInt() + dir.dx;
        int ny = actor.getLocation().getRow() + dir.dy;
        return lastCell.getColumnAsInt() == nx && lastCell.getRow() == ny;
    }
}
