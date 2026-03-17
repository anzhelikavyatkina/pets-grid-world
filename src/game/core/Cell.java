package game.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Optional;

import game.actor.Actor;
import game.items.Item;
import game.terrain.Grass;
import game.terrain.Terrain;
import ui.Paintable;
import weather.state.SunnyWeatherState;
import weather.state.WeatherStateBehavior;

public class Cell extends Rectangle implements Paintable {
    public static int size = 35;
    private char col;
    private Color hoverOverlay;
    private int row;
    private Terrain baseTerrain;
    private Point mousePos;
    private Optional<Actor> occupant = Optional.empty();
    private Optional<Item> item = Optional.empty();
    private WeatherStateBehavior weatherState = new SunnyWeatherState();

    public Cell(char inCol, int inRow, int x, int y, Terrain base) {
        super(x, y, size, size);
        col = inCol;
        row = inRow;
        baseTerrain = base;
        hoverOverlay = new Color(255, 255, 102, 150);

    }

    public void setWeatherState(WeatherStateBehavior newState) {
        this.weatherState = newState;
    }

    public WeatherStateBehavior getWeatherState() {
        return weatherState;
    }

    public Terrain getTerrain() {
        return baseTerrain;
    }

    public void setTerrain(Terrain terrain) {
        this.baseTerrain = terrain;
    }

    public Cell(char inCol, int inRow, int x, int y) {
        this(inCol, inRow, x, y, new Grass());
    }

    public void setMousePos(Point mousePos) {
        this.mousePos = mousePos;
    }

    public Point getMousePos() {
        return mousePos;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(weatherState.getColor(baseTerrain));
        g.fillRect(x, y, size, size);

        weatherState.getTexture(baseTerrain)
                .ifPresent(tex -> g.drawImage(tex, x, y, size, size, null));

        if (contains(mousePos)) {
            g.setColor(hoverOverlay);
            g.fillRect(x, y, size, size);
        }

        g.setColor(Color.BLACK);
        g.drawRect(x, y, size, size);
    }

    public boolean contains(Point p) {
        return Optional.ofNullable(p).map(super::contains).orElse(false);
    }

    public int getColumnAsInt() {
        int c = col;
        if (c >= 'A' && c <= 'Z')
            return c - 'A';
        if (c >= 'a' && c <= 'z')
            return c - 'a';
        return c;
    }

    public int getRow() {
        return row;
    }

    public char getColumn() {
        return col;
    }

    public void addItem(Item itemToPlace) {
        if (itemToPlace != null) {
            itemToPlace.setLocation(this);
            this.item = Optional.of(itemToPlace);
        }
    }

    public boolean isEmpty() {
        return item.isEmpty() && occupant.isEmpty();
    }

    public boolean hasOccupant() {
        return occupant.isPresent();
    }

    public void clearOccupant() {
        occupant = Optional.empty();
    }

    public void setOccupant(Actor actor) {
        occupant = Optional.ofNullable(actor);
        if (actor != null) {
            actor.setLocation(this);
        }
    }

    public Optional<Actor> getOccupant() {
        return occupant;
    }

    public static String convertToLabel(int worldX, int worldY, int gridSize) {
        int half = gridSize / 2;
        int col = half + worldX;
        int row = half - worldY;
        if (col < 0 || col >= gridSize || row < 0 || row >= gridSize) {
            return String.format("OUT(%d,%d)", worldX, worldY);
        }
        char colLetter = (char) ('A' + (col % 26));
        return String.format("%c%d", colLetter, row);
    }

    public void removeItem(Item i) {
        item.ifPresent(existing -> {
            if (existing == i) {
                item = Optional.empty();
            }
        });
    }
}
