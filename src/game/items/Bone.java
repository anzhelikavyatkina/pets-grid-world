package game.items;

import javax.swing.ImageIcon;

import game.core.Cell;

public class Bone extends Item {
    public Bone(Cell inLocation) {
        super(() -> new ImageIcon("images/bone.png").getImage(), inLocation);
    }
}