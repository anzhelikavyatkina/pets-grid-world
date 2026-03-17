package ui;

import java.awt.Graphics;
import java.util.Optional;
import java.util.function.Consumer;
import java.awt.Color;
import java.awt.Font;

public class Instructions {
    public static void displayInstructions(Graphics g, int height, int width, Color color, String instructions) {
        Consumer<Graphics> drawText = graphics -> {
            graphics.setFont(new Font("Arial", Font.BOLD, 27));
            graphics.setColor(color);
            graphics.drawString(instructions, width, height);
        };

        Optional.ofNullable(instructions)
                .filter(s -> !s.isEmpty())
                .ifPresent(s -> drawText.accept(g));
    }
}
