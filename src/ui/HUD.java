package ui;

import java.awt.Graphics;
import java.util.Optional;
import java.awt.Font;
import java.awt.Color;
import java.awt.FontMetrics;

public class HUD {
    private String message = "";

    public void show(String text) {
        this.message = text;
    }

    public void clear() {
        this.message = "";
    }

    public void paint(Graphics g, int width, int height, Color color) {
        Optional.ofNullable(message)
                .filter(m -> !m.isEmpty())
                .ifPresent(m -> {
                    g.setFont(new Font("Arial", Font.BOLD, 45));
                    g.setColor(color);

                    FontMetrics metrics = g.getFontMetrics();
                    int textWidth = metrics.stringWidth(m);
                    int x = (width - textWidth) / 2;
                    int y = (int) (height * 0.5);

                    g.drawString(m, x, y);
                });
    }
}
