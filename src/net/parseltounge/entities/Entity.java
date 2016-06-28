package net.parseltounge.entities;

import net.parseltounge.components.phys_comp.Hitbox;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Jack on 9/6/2014.
 */
public abstract class Entity {
    //Abstract entity class
    //Contains x-y coordinates, hitbox, current image to draw, and speed
    //Has update() and draw() methods which must be overloaded

    public BufferedImage current_img;  //Current image to be drawn to screen

    public int update_counter;  //Number of updates since creation (if ups=100, would take 0.68 years to overflow!)

    public int x_pos;  //Position of character
    public int y_pos;

    public Hitbox hitbox;  //Hit box of entity

    public int dx;  //Speed in x-axis (right=positive, left=negative)
    public int dy;  //Speed in y_axis (down=positive, up=negative)

    //Methods (getters and setters)

    public void set_x(int x_val) {
        x_pos = x_val;
    }

    public void set_y(int y_val) {
        y_pos = y_val;
    }

    public int get_x() {
        return x_pos;
    }

    public int get_y() {
        return y_pos;
    }

    public void set_dx(int dx_new) {  //Set speed in x_axis to new val
        dx = dx_new;
    }

    public void set_dy(int dy_new) {  //Set speed in y_axis to new val
        dy = dy_new;
    }

    public int get_dx() {  //Set speed in y_axis to new val
        return dx;
    }

    public int get_dy() {  //Set speed in y_axis to new val
        return dy;
    }

    public Hitbox get_hitbox() {  //get hitbox of entity
        return hitbox;
    }

    public boolean collides_with(Entity other) {
        return hitbox.intersects(other.get_hitbox());
    }

    //Methods to be overloaded

    public abstract void collision(Entity other);  //About to collide, take action

    public abstract void draw(Graphics g);  //Draw image to g

    public abstract void update();  //Update entity state
}