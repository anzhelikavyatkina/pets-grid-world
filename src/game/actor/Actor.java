package game.actor;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.Optional;
import java.awt.Color;
import java.awt.Graphics2D;

import game.actor.strategy.MoveStrategy;
import game.actor.strategy.SmartMoveStrategy;
import game.actor.strategy.WeatherReactionStrategy;
import game.core.Cell;
import game.core.Direction;
import game.core.Game;
import game.core.Movable;
import game.items.Bag;
import game.items.Item;
import game.terrain.Terrain;
import ui.Paintable;
import ui.WeatherEffectsBoard;
import weather.state.WeatherStateBehavior;

public abstract class Actor implements Paintable, Movable {
    private Cell location;
    private Image icon;
    private boolean choosen = false;
    public static final int DEFAULT_BAG_CAPACITY = 3;
    private int score = 0;
    private final Bag bag;
    private int skipTurns = 0;
    private MoveStrategy moveStrategy;
    protected WeatherReactionStrategy reactionStrategy;
    protected int curiosity = 7;
    protected boolean resting = false;
    protected WeatherEffectsBoard weatherBoard;

    public Actor(String iconPath, Cell loc, Bag b, WeatherReactionStrategy strategy) {
        location = loc;
        icon = new ImageIcon(iconPath).getImage();
        bag = b;
        reactionStrategy = strategy;
    }

    public abstract String getActorName();

    public void setLocation(Cell loc) {
        location = loc;
    }

    public Cell getLocation() {
        return location;
    }

    public void actorIsChosen() {
        choosen = true;
    }

    public boolean isChosen() {
        return choosen;
    }

    @Override
    public void paint(Graphics g) {
        Optional.ofNullable(location).ifPresent(loc -> {
            g.drawImage(icon, loc.x, loc.y, Cell.size, Cell.size, null);

            if (isResting()) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.fillRect(loc.x, loc.y, Cell.size, Cell.size);
                g2d.dispose();
            }
        });
    }

    @Override
    public boolean takeTurn(Game game) {
        if (isResting()) {
            applySkipTurns(100);
            return true;
        }

        if (isSkippingTurn()) {
            reduceSkipCounter();
            return true;
        }

        if (moveStrategy == null) {
            moveStrategy = new SmartMoveStrategy();
        }

        Direction dir = moveStrategy.chooseDirection(this, game);

        boolean validMove = game.stage().tryMove(this, dir);

        if (!validMove) {
            System.out.println((isChosen() ? "PLAYER" : "BOT " + getActorName()) + " invalid move");
        }

        boolean ate = game.stage().tryEatHere(this);
        boolean didSomething = validMove || ate;

        if (didSomething) {
            System.out.println((isChosen() ? "PLAYER" : "BOT " + getActorName()) + " takes turn");
        }

        return didSomething;
    }

    public boolean canWalkOnWater() {
        return false;
    }

    public int getScore() {
        return score;
    }

    public Bag bag() {
        return bag;
    }

    public abstract Class<? extends Item> preferredItemType();

    public boolean tryEat(Item item) {
        if (bag.tryAdd(item)) {
            score++;
            return true;
        }
        return false;
    }

    public boolean isSkippingTurn() {
        return skipTurns > 0;
    }

    public void applySkipTurns(int n) {
        skipTurns += Math.max(0, n);
    }

    public void reduceSkipCounter() {
        skipTurns = Math.max(0, skipTurns - 1);
    }

    public void setMoveStrategy(MoveStrategy strategy) {
        this.moveStrategy = strategy;
    }

    public MoveStrategy getMoveStrategy() {
        return moveStrategy;
    }

    public void setWeatherBoard(WeatherEffectsBoard board) {
        this.weatherBoard = board;
    }

    public String reactToWeather(WeatherStateBehavior weather, Terrain terrain) {
        if (resting) {
            return getActorName() + " ignores weather while resting.";
        }
        return reactionStrategy.react(this, weather, terrain);
    }

    public void decreaseCuriosity(int amount) {
        if (resting)
            return;
        curiosity = Math.max(0, curiosity - amount);

        if (curiosity == 0) {
            resting = true;
            System.out.println(getActorName() + " lost all curiosity and goes to rest");
        }
    }

    public void increaseCuriosity(int amount) {
        if (resting)
            return;
        curiosity = Math.min(7, curiosity + amount);
        System.out.println(getActorName() + " gains curiosity → " + curiosity);
    }

    public boolean isResting() {
        return resting;
    }

    public int getCuriosity() {
        return curiosity;
    }

    public void setCuriosity(int cur) {
        curiosity = cur;
    }

    public void changeRest() {
        resting = !resting;
    }
}
