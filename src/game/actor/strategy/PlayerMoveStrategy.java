package game.actor.strategy;

import game.actor.Actor;
import game.core.Direction;
import game.core.Game;

public class PlayerMoveStrategy implements MoveStrategy {
    @Override
    public Direction chooseDirection(Actor actor, Game game) {
        return game.getPlayerDir();
    }
}
