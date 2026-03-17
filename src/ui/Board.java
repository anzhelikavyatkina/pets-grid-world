package ui;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.util.function.Consumer;
import java.util.Optional;

public abstract class Board implements Paintable {
    protected int panelX, panelY, panelWidth, panelHeight;

    public Board(int panelX, int panelY, int panelWidth, int panelHeight) {
        this.panelX = panelX;
        this.panelY = panelY;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    public void setLayout(int x, int y, int w, int h) {
        this.panelX = x;
        this.panelY = y;
        this.panelWidth = w;
        this.panelHeight = h;
    }

    public int getPanelX() {
        return panelX;
    }

    public int getPanelY() {
        return panelY;
    }

    public int getPanelWidth() {
        return panelWidth;
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public void drawPanel(Graphics g, Consumer<Graphics> drawer) {
        if (g == null || drawer == null)
            return;
        drawer.accept(g);
    }

    public void fillPanelBg(Graphics g, Color color, int x, int y, int w, int h, int corner) {
        g.setColor(color);
        g.fillRoundRect(x, y, w, h, corner, corner);
    }

    public void fillPanelRect(Graphics g, Color color, int x, int y, int w, int h) {
        if (g != null) {
            g.setColor(color);
            g.fillRect(x, y, w, h);
        }
    }

    public void drawTitle(Graphics g, String text, int x, int y, float fontSize) {
        Optional.ofNullable(g).ifPresent(gr -> {
            gr.setColor(Color.BLACK);
            gr.setFont(gr.getFont().deriveFont(Font.BOLD, fontSize));
            gr.drawString(text, x, y);
        });
    }

    public void fillHighlight(Graphics g, int x, int y, int w, int h, Color color) {
        g.setColor(color);
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(Color.BLACK);
    }

    @Override
    public abstract void paint(Graphics g);
}
