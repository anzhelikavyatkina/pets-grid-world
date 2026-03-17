package game.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import javax.swing.Timer;

import game.actor.Actor;
import game.actor.Bird;
import game.actor.Cat;
import game.actor.Dog;
import game.actor.strategy.PlayerMoveStrategy;
import game.actor.strategy.SmartMoveStrategy;
import game.items.Berry;
import game.items.Bone;
import game.items.Fish;
import game.items.Item;
import game.network.Client;
import game.terrain.Grass;
import game.terrain.Terrain;
import game.terrain.Water;
import ui.WeatherEffectsBoard;
import map.MapGenerator;
import map.Placer;
import map.Scatterer;
import ui.Instructions;
import ui.Paintable;
import weather.WeatherManager;
import weather.WeatherStateChange;
import weather.observer.WeatherObserver;
import weather.state.SunnyWeatherState;
import weather.state.WeatherStateBehavior;

public class Stage implements Paintable, WeatherObserver {
    private final Grid grid;
    private final List<Actor> actors;
    private final List<Item> items;
    private Point mousePos;
    private final WeatherManager weatherManager;
    private WeatherEffectsBoard weatherEffectsBoard;
    private Timer windTimer;
    private Client weatherClient;

    Random random = new Random();
    Scatterer scatterer = new Scatterer();

    public Stage(WeatherManager weatherManager) {
        this.weatherManager = weatherManager;
        grid = new Grid(20);
        actors = new ArrayList<Actor>();
        long seed = System.currentTimeMillis();
        MapGenerator generator = new MapGenerator(grid, seed);
        generator.generateMap();
        items = new ArrayList<>();
        if (weatherManager != null) {
            weatherManager.setGrid(grid);
            weatherManager.registerObserver(this);
        }
    }

    public void startWeatherClient() {
        stopWeatherClient();

        weatherClient = new Client(weatherManager);
        new Thread(() -> weatherClient.start()).start();
        System.out.println("Weather client started");
    }

    public void stopWeatherClient() {
        if (weatherClient != null) {
            weatherClient.stop();
            weatherClient = null;
            System.out.println("Weather client stopped");
        }
    }

    public void attachWeatherEffectsBoard(WeatherEffectsBoard board) {
        this.weatherEffectsBoard = board;
    }

    public WeatherManager getWeatherManager() {
        return weatherManager;
    }

    public void setMousePos(Point p) {
        mousePos = p;
    }

    @Override
    public void paint(Graphics g) {
        grid.setMousePos(mousePos);
        grid.paint(g);

        items.forEach(item -> item.paint(g));

        for (Actor actor : actors) {
            actor.paint(g);
            if (actor.isChosen()) {
                Instructions.displayInstructions(g, 100, 720, Color.PINK,
                        "Wow! Your pet is a " + actor.getActorName() + "!!!");
            }
        }

        grid.cellAtPoint(mousePos)
                .ifPresent(cell -> Instructions.displayInstructions(
                        g,
                        30,
                        720,
                        Color.BLACK,
                        "Mouse Position at: " + cell.getColumn() + cell.getRow()));
    }

    public void placeItems(Random random) {
        Placer<Item> itemPlacer = new Placer<>(
                grid,
                (cell, item, world) -> cell != null
                        && cell.getTerrain() instanceof Grass
                        && cell.isEmpty()
                        && !world.surroundByType(cell, Water.class),
                (cell, item) -> {
                    item.setLocation(cell);
                    cell.addItem(item);
                    items.add(item);
                });

        List<Supplier<Item>> itemSuppliers = List.of(
                () -> new Bone(null),
                () -> new Berry(null),
                () -> new Fish(
                        null));
        itemSuppliers.forEach(supplier -> scatterer.scatter(grid, random, supplier, 15, itemPlacer));
        startWeatherAnimation();
    }

    public void startWeatherAnimation() {
        if (windTimer != null && windTimer.isRunning())
            return;

        windTimer = new Timer(300, e -> {
            for (Item item : items) {
                Cell cell = item.getLocation();
                if (cell == null)
                    continue;

                WeatherStateBehavior weather = cell.getWeatherState();

                weather.affectItem(item);
            }
        });
        windTimer.start();
    }

    public List<Actor> getActors() {
        return actors;
    }

    public boolean tryMove(Actor a, Direction dir) {
        if (!canMove(a, dir)) {
            return false;
        }

        Cell moveFrom = a.getLocation();
        int column = moveFrom.getColumnAsInt();
        int row = moveFrom.getRow();
        int nx = column + dir.dx;
        int ny = row + dir.dy;

        Cell moveTo = grid.cellAtColRow(nx, ny).get();

        moveFrom.clearOccupant();
        moveTo.setOccupant(a);
        a.setLocation(moveTo);

        return true;
    }

    public boolean canMove(Actor a, Direction dir) {
        Cell moveFrom = a.getLocation();
        if (moveFrom == null || dir == null)
            return false;

        int column = moveFrom.getColumnAsInt();
        int row = moveFrom.getRow();
        int nx = column + dir.dx;
        int ny = row + dir.dy;

        if (!grid.inBounds(nx, ny))
            return false;

        Optional<Cell> optional = grid.cellAtColRow(nx, ny);
        if (optional.isEmpty())
            return false;

        Cell moveTo = optional.get();
        Terrain terrain = moveTo.getTerrain();

        if (!terrain.isWalkable(a))
            return false;
        if (moveTo.hasOccupant())
            return false;

        return true;
    }

    public boolean tryEatHere(Actor a) {
        return items.removeIf(item -> Optional.ofNullable(item.getLocation())
                .filter(loc -> loc.equals(a.getLocation()))
                .filter(loc -> a.tryEat(item))
                .isPresent());
    }

    public String applyTileEffects(Actor a) {
        return Optional.ofNullable(a)
                .map(Actor::getLocation)
                .map(Cell::getTerrain)
                .map(terrain -> terrain.onEnter(a, this, random))
                .filter(msg -> msg != null && !msg.isEmpty())
                .map(msg -> msg + " at (" + a.getLocation().getColumn() + a.getLocation().getRow() + ")")
                .orElse(null);
    }

    public void placeActors(Random rng, Class<? extends Actor> selected) {
        Placer<Actor> actorPlacer = new Placer<>(
                grid,
                (cell, actor, world) -> actor.canWalkOnWater()
                        ? cell.isEmpty()
                        : cell.isEmpty()
                                && !(cell.getTerrain() instanceof Water)
                                && !world.surroundByType(cell, Water.class),
                (cell, actor) -> {
                    actor.setLocation(cell);
                    cell.setOccupant(actor);
                    actors.add(actor);
                });

        List<Supplier<Actor>> actorSuppliers = List.of(
                () -> new Dog(null),
                () -> new Cat(null),
                () -> new Bird(null));
        actorSuppliers.forEach(supplier -> scatterer.scatter(grid, rng, supplier, 1, actorPlacer));

        if (selected != null) {
            actors.forEach(actor -> {
                if (actor.getClass().equals(selected)) {
                    actor.actorIsChosen();
                    actor.setMoveStrategy(new PlayerMoveStrategy());
                } else {
                    actor.setMoveStrategy(new SmartMoveStrategy());
                }
            });
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public void reset() {
        if (windTimer != null) {
            windTimer.stop();
            windTimer = null;
        }
        items.forEach(Item::resetVisualState);
        actors.forEach(actor -> {
            actor.setCuriosity(10);
            actor.changeRest();
            ;
            actor.applySkipTurns(0);
        });
        actors.clear();
        items.clear();
        grid.getAllCells().forEach(cell -> cell.setWeatherState(new SunnyWeatherState()));
    }

    public Grid getGrid() {
        return grid;
    }

    public WeatherEffectsBoard getWeatherEffectsBoard() {
        return weatherEffectsBoard;
    }

    @Override
    public void update(WeatherStateChange event) {
        int x = event.getX();
        int y = event.getY();

        WeatherStateBehavior newWeather = event.getNewState();
        grid.cellAtColRow(x, y).ifPresent(cell -> cell.setWeatherState(newWeather));

    }
}
