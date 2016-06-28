package net.parseltounge.components.phys_comp;

import java.awt.*;

public class Hitbox extends Rectangle {
    //Hit box for entity. Essentially a wrapper for the Rectangle class, may have more functionality later.

    public Hitbox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
