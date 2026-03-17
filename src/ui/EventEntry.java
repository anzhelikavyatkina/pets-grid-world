package ui;

public class EventEntry {
    public final String text;
    public final boolean highlight;

    private EventEntry(String text, boolean highlight) {
        this.text = text;
        this.highlight = highlight;
    }

    public static EventEntry highlighted(String text) {
        return new EventEntry(text, true);
    }

    public static EventEntry normal(String text) {
        return new EventEntry(text, false);
    }

    public String getText() {
        return text;
    }

    public boolean isHighlighted() {
        return highlight;
    }
}
