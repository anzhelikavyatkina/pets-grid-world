package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventBoard extends Board {
    private final List<EventEntry> lines = new ArrayList<>();
    private final int maxLines;

    public EventBoard(int maxLines, int panelX, int panelY, int panelWidth, int panelHeight) {
        super(panelX, panelY, panelWidth, panelHeight);
        this.maxLines = Math.max(1, maxLines);
    }

    public EventBoard(int maxLines) {
        this(maxLines, 0, 0, 0, 0);
    }

    public void setLayout(int panelX, int panelY, int panelWidth, int canvasHeight) {
        super.setLayout(panelX, panelY, panelWidth, canvasHeight);
    }

    public int getPanelX() {
        return panelX;
    }

    public int getPanelWidth() {
        return panelWidth;
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public void add(String message, boolean highlight) {
        Optional.ofNullable(message)
                .filter(m -> !m.isEmpty())
                .ifPresent(m -> {
                    lines.add(highlight ? EventEntry.highlighted(m)
                            : EventEntry.normal(m));
                    if (lines.size() > maxLines) {
                        lines.remove(0);
                    }
                });
    }

    public void clear() {
        lines.clear();
    }

    @Override
    public void paint(Graphics g) {
        int panelHeight = 120;

        fillPanelBg(g, new Color(255, 204, 229),
                getPanelX(), panelY, getPanelWidth(), panelHeight, 12);
        drawTitle(g, "Events", getPanelX() + 12, panelY + 20, 18f);

        g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
        int[] y = { panelY + 40 };

        lines.forEach(entry -> {
            if (entry.isHighlighted()) {
                fillHighlight(g, getPanelX() + 8, y[0] - 12,
                        getPanelWidth() - 16, 16, new Color(230, 240, 255));
            }
            g.drawString(entry.getText(), getPanelX() + 12, y[0]);
            y[0] += 16;
        });
    }
}
