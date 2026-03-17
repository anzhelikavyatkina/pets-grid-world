package game.actor.strategy;

import game.actor.Actor;
import game.core.Direction;
import game.core.Game;

@FunctionalInterface
public interface MoveStrategy {
    Direction chooseDirection(Actor actor, Game game);
}
