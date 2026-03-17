package game.items;

import javax.swing.ImageIcon;

import game.core.Cell;

public class Fish extends Item {
    public Fish(Cell inLocation) {
        super(() -> new ImageIcon("images/fish.png").getImage(), inLocation);
    }
}