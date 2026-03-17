package game.items;

import javax.swing.ImageIcon;

import game.core.Cell;

public class Berry extends Item {
    public Berry(Cell inLocation) {
        super(() -> new ImageIcon("images/berry.png").getImage(), inLocation);
    }
}