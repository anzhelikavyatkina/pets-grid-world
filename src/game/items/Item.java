package game.items;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.function.Supplier;

import game.core.Cell;
import ui.Paintable;
import weather.state.WeatherStateBehavior;

import java.awt.Color;

public abstract class Item implements Paintable {
    private final Supplier<Image> iconSupplier;
    private Cell location;
    private int alpha = 255;
    private Color tint = Color.WHITE;
    private int offsetX = 0, offsetY = 0;

    public Item(Supplier<Image> iconSupplier, Cell loc) {
        this.iconSupplier = iconSupplier;
        this.location = loc;
    }

    public void setAlpha(int a) {
        this.alpha = Math.max(50, Math.min(255, a));
    }

    public void nudge(int dx, int dy) {
        offsetX += dx;
        offsetY += dy;
    }

    public void resetVisualState() {
        this.setTint(Color.WHITE);
        this.setAlpha(255);
        this.nudge(-offsetX, -offsetY);
    }

    public void setLocation(Cell loc) {
        location = loc;
    }

    public Cell getLocation() {
        return location;
    }

    public void setTint(Color c) {
        this.tint = c;
    }

    @Override
    public void paint(Graphics g) {
        if (location == null)
            return;
        Graphics2D g2 = (Graphics2D) g;
        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
        g2.drawImage(iconSupplier.get(), location.x + offsetX, location.y + offsetY, Cell.size, Cell.size, null);
        if (!tint.equals(Color.WHITE)) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
            g2.setColor(tint);
            g2.fillRect(location.x + offsetX, location.y + offsetY, Cell.size, Cell.size);
        }

        g2.setComposite(old);
    }

    public void reactToWeather(WeatherStateBehavior state) {
        state.affectItem(this);
        System.out.println(getClass().getSimpleName() + " affected by " + state.getName());
    }
}
