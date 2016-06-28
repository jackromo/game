package net.parseltounge.entities.living_ents;

/**
 * Created by Jack on 9/9/2014.
 */

import net.parseltounge.components.phys_comp.Hitbox;
import net.parseltounge.entities.Entity;
import net.parseltounge.entities.wall_ents.WallEntity;
import net.parseltounge.entities.attack_ents.AttackEntity;
import net.parseltounge.components.graph_comp.ImgManager;

import java.awt.*;

public class EnemyEntity extends LivingEntity {
    //Enemy class in game.
    //Will eventually be abstract, parent of all enemy classes

    private ImgManager im_man;  //Image manager

    //Methods

    public EnemyEntity(int x, int y) {  //Constructor
        health = 50;
        x_pos = x;
        y_pos = y;
        dx = 5;
        dy = 0;

        im_man = new ImgManager();
        im_man.load_image("resources/enemy_img.png", "enemy_img");
        current_img = im_man.get_img("enemy_img");

        hitbox = new Hitbox(x_pos, y_pos, 100, 100);
    }

    public void draw(Graphics g) {
        g.drawImage(current_img, x_pos, y_pos, 100, 100, null);
    }

    public void update() {
        x_pos += dx;  //Update position with respect to speed
        y_pos += dy;

        if(dy < 15)
            dy += 1; //Accelerate


        //Temporary AI for enemy, will be more intricate later
        if(x_pos < 375 || x_pos > 425) //Move left to right in between 500 and 800
            dx = -dx;

        hitbox.setLocation(x_pos, y_pos);  //Update hitbox

        if(health <= 0) {
            //Enemy death logic
            health = 0;
            dead = true;
        }
    }

    public AttackEntity get_attack() {  //get current attack
        return null;
    }

    public void collision(Entity other) {  //Handle collisions with other entities

        if(other instanceof WallEntity) {  //If collided with wall
            wall_collision((WallEntity) other);
        }
        else if(other instanceof PlayerEntity) {
            //Do nothing
        } else if(other instanceof AttackEntity) {  //if hit by attack

            AttackEntity oth = (AttackEntity) other;
            if(oth.get_source() instanceof PlayerEntity) {
                //Hit by player, take action
                health -= oth.get_damage();
            }
        }

    }
}
