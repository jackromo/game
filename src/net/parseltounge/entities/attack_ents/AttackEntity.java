package net.parseltounge.entities.attack_ents;

import net.parseltounge.components.graph_comp.ImgManager;

import java.awt.*;
import java.awt.image.BufferedImage;

import net.parseltounge.components.phys_comp.Hitbox;
import net.parseltounge.entities.Entity;

public class AttackEntity extends Entity {
    //Any attack in game has a hitbox, updates and renders, so is an entity.
    //eg. bullet, punch, fireball, etc.
    //One AttackEntity can contain several
    //eg. if enemy shoots 3 bombs, then uses BombGroup entity, which manages each bomb

    int damage;  //Damage than attack deals
    Entity source;  //Entity which initiated attack - if null, then from nothing in particular

    ImgManager img_manager;
    BufferedImage current_img;

    public AttackEntity(Entity src, int start_damage) {
        source = src;
        damage = start_damage;

        img_manager = new ImgManager();
        current_img = null;  //No image for now

        hitbox = new Hitbox(x_pos, y_pos, 10, 10);
    }

    public void collision(Entity other) {
        //Nothing happens to entity when colliding yet
    }

    public void draw(Graphics g) {
        g.setColor(Color.black);
        g.drawRect(x_pos, y_pos, hitbox.width, hitbox.height);
        if(current_img != null)  //If any image exists
            g.drawImage(current_img, x_pos, y_pos, null);
    }

    public void update() {
        x_pos += dx;
        y_pos += dy;
        hitbox.setBounds(x_pos, y_pos, hitbox.width, hitbox.height);
    }

    public int get_damage() {
        return damage;
    }

    public Entity get_source() {
        return source;
    }
}