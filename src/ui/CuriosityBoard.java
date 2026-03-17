package ui;

import java.awt.*;
import java.util.List;

import game.actor.Actor;

public class CuriosityBoard extends Board {

    private List<Actor> actors;

    public CuriosityBoard(int panelX, int panelY, int panelWidth, int canvasHeight, List<Actor> actors) {
        super(panelX, panelY, panelWidth, canvasHeight);
        this.actors = actors;
    }

    @Override
    public void paint(Graphics g) {
        int panelHeight = 120;
        fillPanelRect(g, new Color(245, 245, 250), panelX, panelY, panelWidth, panelHeight);
        drawTitle(g, "Curiosity Monitor", panelX + 12, panelY + 22, 18f);

        if (actors == null || actors.isEmpty()) {
            g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
            g.setColor(new Color(90, 90, 90));
            g.drawString("No active pets to monitor.", panelX + 12, panelY + 55);
            return;
        }

        int y = panelY + 50;
        int barWidth = (int) (panelWidth / 2);

        for (Actor a : actors) {
            int curiosity = a.isResting() ? 0 : a.getCuriosity();
            double pct = curiosity / 7.0;
            int barFill = (int) (barWidth * pct);
            Color fillColor = new Color(153, 255, 51);
            if (pct < 0.8) {
                fillColor = new Color(255, 255, 51);
            }
            if (pct < 0.5) {
                fillColor = new Color(255, 153, 51);
            }
            if (pct < 0.3) {
                fillColor = new Color(255, 51, 51);
            }
            g.setColor(fillColor);
            if (a.isChosen()) {
                fillHighlight(g, panelX + 8, y - 16, panelWidth - 16, 28, new Color(255, 229, 204));
            }
            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, 14f));
            g.drawString(a.getActorName() + ":", panelX + 16, y);
            g.setColor(fillColor);
            g.fillRoundRect(panelX + 80, y - 14, barFill, 14, 6, 6);
            g.setColor(Color.BLACK);
            g.drawRoundRect(panelX + 80, y - 14, barWidth, 14, 6, 6);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 13f));
            g.drawString(String.valueOf(curiosity), panelX + 80 + barWidth + 10, y - 2);

            y += 28;
        }
    }
}
