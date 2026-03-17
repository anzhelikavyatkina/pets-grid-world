package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeatherEffectsBoard extends Board {
    private final List<EventEntry> weatherEvents = new ArrayList<>();
    private final int maxLines;

    public WeatherEffectsBoard(int maxLines, int panelX, int panelY, int panelWidth, int canvasHeight) {
        super(panelX, panelY, panelWidth, canvasHeight);
        this.maxLines = Math.max(1, maxLines);
    }

    public WeatherEffectsBoard(int maxLines) {
        this(maxLines, 0, 0, 0, 0);
    }

    public void setLayout(int panelX, int panelY, int panelWidth, int canvasHeight) {
        super.setLayout(panelX, panelY, panelWidth, canvasHeight);
    }

    public void add(String message, boolean highlight) {
        Optional.ofNullable(message)
                .filter(m -> !m.isEmpty())
                .ifPresent(m -> {
                    weatherEvents.add(highlight ? EventEntry.highlighted(m)
                            : EventEntry.normal(m));
                    if (weatherEvents.size() > maxLines) {
                        weatherEvents.remove(0);
                    }
                });
    }

    public void clear() {
        weatherEvents.clear();
    }

    @Override
    public void paint(Graphics g) {
        int panelHeight = Math.max(120, 40 + weatherEvents.size() * 16);

        fillPanelBg(g, new Color(204, 229, 255),
                getPanelX(), panelY, getPanelWidth(), panelHeight, 12);
        drawTitle(g, "Weather Effects", getPanelX() + 12, panelY + 20, 18f);

        g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
        int[] y = { panelY + 40 };

        for (EventEntry entry : weatherEvents) {
            if (entry.isHighlighted()) {
                Color softYellow = new Color(255, 255, 230);
                fillHighlight(g, getPanelX() + 8, y[0] - 12, getPanelWidth() - 16, 16, softYellow);
            }
            g.drawString(entry.getText(), getPanelX() + 12, y[0]);
            y[0] += 16;
        }
    }
}
