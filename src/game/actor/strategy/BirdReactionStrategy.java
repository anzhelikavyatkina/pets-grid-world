package game.actor.strategy;

import java.util.Random;

import game.actor.Actor;
import game.terrain.Terrain;
import weather.state.WeatherStateBehavior;

public class BirdReactionStrategy implements WeatherReactionStrategy {
    private final Random rng = new Random();

    @Override
    public String react(Actor actor, WeatherStateBehavior weather, Terrain terrain) {
        String name = actor.getActorName();
        String message;

        switch (weather.getName().toLowerCase()) {
            case "sunny":
                message = name + " enjoys the sunny sky!";
                break;

            case "rain":
                actor.decreaseCuriosity(1);
                actor.applySkipTurns(1);
                message = name + " refused to fly due to wet wings!";
                break;

            case "wind":
                actor.decreaseCuriosity(2);
                actor.applySkipTurns(1);
                message = name + " is unable to fly due to strong wind!";
                break;

            case "cold":
                actor.decreaseCuriosity(2);
                actor.applySkipTurns(1);
                message = name + " has problems with wings due to cold weather!";
                break;

            case "heat":
                actor.decreaseCuriosity(0);
                if (rng.nextInt(5) == 0) {
                    actor.applySkipTurns(1);
                    message = name + " stays in shade to cool down instead of flying!";
                } else {
                    message = name + " trying to fly away to the cooler area!";
                }
                break;

            case "storm":
                actor.decreaseCuriosity(2);
                actor.applySkipTurns(1);
                message = name + " hides from the storm and cannot fly or move.";
                break;

            default:
                message = name + " does not react to this weather.";
        }

        return message;
    }
}
