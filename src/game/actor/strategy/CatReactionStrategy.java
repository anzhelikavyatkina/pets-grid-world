package game.actor.strategy;

import java.util.Random;

import game.actor.Actor;
import game.terrain.Mud;
import game.terrain.Terrain;
import weather.state.WeatherStateBehavior;

public class CatReactionStrategy implements WeatherReactionStrategy {
    private final Random rng = new Random();

    @Override
    public String react(Actor actor, WeatherStateBehavior weather, Terrain terrain) {
        String name = actor.getActorName();
        String message;

        switch (weather.getName().toLowerCase()) {
            case "sunny":
                message = name + " enjoys the perfect weather!";
                break;

            case "rain":
                actor.decreaseCuriosity(1);
                if (terrain instanceof Mud) {
                    actor.applySkipTurns(1);
                    message = name + " refuses to walk on mud in the rain!";
                } else {
                    message = name + " feels so wet but stil exploring!";
                }
                break;

            case "wind":
                actor.decreaseCuriosity(1);
                if (rng.nextDouble() < 0.5) {
                    actor.applySkipTurns(1);
                    message = name + " skips the turn due to strong wind!";
                } else {
                    message = name + " struggles to keep balance but stil moves!";
                }
                break;

            case "cold":
                actor.decreaseCuriosity(2);
                boolean feelLonely = rng.nextBoolean();
                if (feelLonely) {
                    actor.applySkipTurns(1);
                    message = name + " feels lonely and refuses to move in the cold.";
                } else {
                    message = name + " moving to find warmer place!";
                }
                break;

            case "heat":
                actor.decreaseCuriosity(1);
                if (rng.nextDouble() < 0.3) {
                    actor.applySkipTurns(1);
                    message = name + " does not move due to heat!";
                } else {
                    message = name + " lazily keeps exploring despite the heat.";
                }
                break;

            case "storm":
                actor.decreaseCuriosity(2);
                actor.applySkipTurns(1);
                message = name + " hides under shelter and skips the turn during the storm.";
                break;

            default:
                message = name + " does not react to this weather.";
        }

        return message;
    }
}
