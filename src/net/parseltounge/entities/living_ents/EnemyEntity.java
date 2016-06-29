package net.parseltounge.entities.living_ents;

import net.parseltounge.components.ai_comp.EnemyAI;
import net.parseltounge.components.ai_comp.EnemyAIUpdater;
import net.parseltounge.components.phys_comp.Hitbox;
import net.parseltounge.entities.Entity;
import net.parseltounge.entities.wall_ents.WallEntity;
import net.parseltounge.entities.attack_ents.AttackEntity;
import net.parseltounge.components.graph_comp.ImgManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;

public class EnemyEntity extends LivingEntity {

    private ImgManager im_man;  //Image manager
    private EnemyAIUpdater updater;

    public boolean facing_left;
    public boolean facing_wall; // used by AI to check if just hit a wall horizontally

    private AffineTransformOp tf_hor_flip;

    public EnemyEntity(int x, int y) {
        health = 50;
        x_pos = x;
        y_pos = y;
        dx = 5;
        dy = 0;

        im_man = new ImgManager();
        im_man.load_image("resources/enemy_img.png", "enemy_img");
        current_img = im_man.get_img("enemy_img");

        //Create transform op to flip player image when turning around
        // NB: make all enemy sprites face left in spritesheet
        AffineTransform tf = AffineTransform.getScaleInstance(-1, 1);
        tf.translate(-current_img.getWidth(), 0);
        tf_hor_flip = new AffineTransformOp(tf, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        updater = new EnemyAIUpdater(EnemyAI.STILL);    // default AI = stand still
        hitbox = new Hitbox(x_pos, y_pos, 100, 100);
    }

    public void draw(Graphics g) {
        g.drawImage(current_img, x_pos, y_pos, 100, 100, null);
    }

    public void update(ArrayList<Entity> ents) {

        updater.update_enemy(this);

        x_pos += dx;  //Update position with respect to speed
        y_pos += dy;

        if(dy < 15)
            dy += 1; //Accelerate

        if(!facing_left)
            tf_hor_flip.filter(current_img, null);  // Flip image appropriately

        hitbox.setLocation(x_pos, y_pos);  //Update hitbox

        if(health <= 0) {
            //Enemy death logic
            health = 0;
            dead = true;
        }

        facing_wall = false;    // if still colliding with wall, will become true again
    }

    public void set_updater(EnemyAI new_ai) {
        updater.set_chosen_ai(new_ai);
    }

    public AttackEntity get_attack() {  //get current attack
        return null;
    }

    public void collision(Entity other) {  //Handle collisions with other entities

        if(other instanceof WallEntity) {  //If collided with wall
            facing_wall = true;
            wall_collision((WallEntity) other);
        } else if(other instanceof AttackEntity) {  //if hit by attack
            AttackEntity oth = (AttackEntity) other;
            if(oth.get_source() instanceof PlayerEntity) {
                //Hit by player, take action
                health -= oth.get_damage();
            }
        }

    }
}
