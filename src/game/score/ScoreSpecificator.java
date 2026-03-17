package game.score;

import game.actor.Actor;
import game.items.Item;

public abstract class ScoreSpecificator<T extends Actor, F extends Item> {
    private final Class<T> animalType;
    private final Class<F> foodType;
    private final String foodLabel;

    public ScoreSpecificator(Class<T> animalType, Class<F> foodType, String foodLabel) {
        this.animalType = animalType;
        this.foodType = foodType;
        this.foodLabel = foodLabel;
    }

    public boolean matches(Actor a) {
        return animalType.isInstance(a)
                && foodType.equals(a.preferredItemType());
    }

    public String line(Actor a) {
        return java.util.Optional.ofNullable(a)
                .filter(animalType::isInstance)
                .map(animalType::cast)
                .map(t -> t.getActorName() + " has eaten " + t.bag().size() + " " + foodLabel)
                .orElse("Unknown actor or type mismatch");
    }
}
