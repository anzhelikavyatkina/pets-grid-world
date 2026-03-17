package game.actor.strategy;

import java.util.Random;

import game.actor.Actor;
import game.terrain.Mud;
import game.terrain.Terrain;
import weather.state.WeatherStateBehavior;

public class DogReactionStrategy implements WeatherReactionStrategy {
    private final Random rng = new Random();
    private int turnCounter = 0;

    @Override
    public String react(Actor actor, WeatherStateBehavior weather, Terrain terrain) {
        String name = actor.getActorName();
        turnCounter++;
        String message;

        switch (weather.getName().toLowerCase()) {
            case "sunny":
                message = name + " feels happy under the sun.";
                break;

            case "rain":
                int loss = (terrain instanceof Mud) ? 2 : 1;
                actor.decreaseCuriosity(loss);
                if (rng.nextDouble() < 0.1) {
                    actor.applySkipTurns(1);
                    message = name + " shakes off the rain and skips a turn.";
                } else {
                    message = name + " gets wet and loses curiosity (" + loss + ").";
                }
                break;

            case "wind":
                actor.decreaseCuriosity(1);
                if (rng.nextDouble() < 0.2) {
                    actor.applySkipTurns(1);
                    message = name + " cannot keep balance and skips movement.";
                } else {
                    message = name + " resists the wind bravely.";
                }
                break;

            case "cold":
                actor.decreaseCuriosity(1);
                if (rng.nextDouble() < 0.5) {
                    actor.applySkipTurns(1);
                    message = name + " freezes for a moment and skips this turn.";
                } else {
                    message = name + " does not feel so cold with warm fur ";
                }
                break;

            case "heat":
                actor.decreaseCuriosity(1);
                if (turnCounter % 2 == 0) {
                    actor.applySkipTurns(1);
                    message = name + " is tired from the heat and skips this turn.";
                } else {
                    message = name + " feels so hot but keeps moving.";
                }
                break;

            case "storm":
                actor.decreaseCuriosity(2);
                actor.applySkipTurns(1);
                message = name + " is stressed by the storm and refuses to move.";
                break;

            default:
                message = name + " does not react to this weather.";
        }

        return message;
    }
}
