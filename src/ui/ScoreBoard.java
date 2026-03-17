package ui;

import java.util.List;

import game.actor.Actor;
import game.score.ScoreSpecificator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;

public final class ScoreBoard extends Board {
    private final List<Actor> actors;
    private final List<ScoreSpecificator<?, ?>> specs;

    public ScoreBoard(List<Actor> actors,
            List<ScoreSpecificator<?, ?>> specs,
            int panelX, int panelY, int panelWidth, int canvasHeight) {
        super(panelX, panelY, panelWidth, canvasHeight);
        this.actors = actors;
        this.specs = specs;
    }

    @Override
    public void paint(Graphics g) {
        int panelHeight = 50 + actors.size() * 24;

        fillPanelRect(g, new Color(204, 255, 255), panelX, panelY, getPanelWidth(), panelHeight);
        drawTitle(g, "Scores goal: " + Actor.DEFAULT_BAG_CAPACITY, panelX + 12, panelY + 20, 18f);

        g.setFont(g.getFont().deriveFont(Font.PLAIN, 14f));
        int[] y = { panelY + 50 };

        actors.forEach(actor -> {
            String line = specs.stream()
                    .filter(spec -> spec.matches(actor))
                    .findFirst()
                    .map(spec -> spec.line(actor))
                    .orElse(actor.getActorName() + " has eaten " + actor.bag().size() + " items");

            if (actor.isChosen()) {
                fillHighlight(g, panelX + 8, y[0] - 14, getPanelWidth() - 20, 22,
                        new Color(229, 255, 204));
            }

            g.drawString(line, panelX + 12, y[0]);
            y[0] += 22;
        });
    }
}
