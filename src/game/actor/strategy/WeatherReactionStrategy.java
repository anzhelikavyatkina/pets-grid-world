package game.actor.strategy;

import game.actor.Actor;
import game.terrain.Terrain;
import weather.state.WeatherStateBehavior;

public interface WeatherReactionStrategy {
    String react(Actor animal, WeatherStateBehavior weather, Terrain terrain);
}
