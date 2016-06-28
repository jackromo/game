package net.parseltounge.entities.wall_ents;

import net.parseltounge.components.phys_comp.Hitbox;
import net.parseltounge.entities.Entity;
import net.parseltounge.components.graph_comp.ImgManager;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Jack on 9/7/2014.
 */
public class WallEntity extends Entity {
    //Wall/floor/obstacle in level, does not move, immovable

    ImgManager im_man;

    public boolean left_collides;  //Booleans to show if wall's top, bottom, left or right can be collided with
    public boolean right_collides;
    public boolean top_collides;
    public boolean bottom_collides;

    //Methods

    public void draw(Graphics g) {
        g.drawImage(current_img, x_pos, y_pos, null);
    }

    public void update() {}

    public void collision(Entity other) {}
}
