package net.parseltounge.entities.living_ents;

import net.parseltounge.entities.Entity;
import net.parseltounge.entities.wall_ents.TileEntity;
import net.parseltounge.entities.wall_ents.WallEntity;
import net.parseltounge.entities.attack_ents.AttackEntity;
import net.parseltounge.components.phys_comp.Hitbox;

import java.awt.*;

public abstract class LivingEntity extends Entity {
    //Parent class to all living entities, eg. player, enemies, etc.
    //Different from WallEntity parent class, which all other passive entities fall under

    public int health;  //All living entities have health of some sort
    public boolean on_ground;  //If currently standing on ground or not
    public boolean dead; //Whether or not entity died
    public boolean invincible;  //If entity is invincible (after getting hit, temporary invincibility)

    //Methods

    public void wall_collision(WallEntity other) {  //Collided with wall 'other' (do not go through it!)
        //Allows entities to interact properly with walls and floors, ie. not go through them
        //NOTE: Some entities may go through walls, eg. ghosts, so this method does not have to be used
        //It is called at whim of child entity class, so is available if needed

        if(other instanceof TileEntity) {
            //Technique: find max distance between living entity and wall, go there.
            //Find which side of wall the living entity is on, act accordingly

            Hitbox hbox_next = new Hitbox(x_pos + dx, y_pos + dy, hitbox.width, hitbox.height);  //Hitbox in next update

            Rectangle intersection = hbox_next.intersection(other.get_hitbox());  //Intersect of two hitboxes

            //If on side of wall, then intersection is taller than it is wide
            //If on top/bottom of wall, then intersection is wider than it is tall
            //Then compare x_pos, y_pos with other.get_x() and other.get_y() to find side player is on

            if (intersection.height >= intersection.width) {  //If on left or right of wall
                if (x_pos < other.get_x() && ((TileEntity) other).left_collides) {  //On left
                    x_pos = other.get_x() - hitbox.width;  //Move appropriately
                    if (dx > 0)  //If moving into wall, stop moving in that direction
                        dx = 0;
                } else if(((TileEntity) other).right_collides) {  //On right
                    x_pos = other.get_x() + other.hitbox.width;
                    if (dx < 0)  //If moving into wall, stop moving in that direction
                        dx = 0;
                }
            } else {  //On top or bottom
                if (y_pos < other.get_y() && ((TileEntity) other).top_collides) {  //Above
                    y_pos = other.get_y() - hitbox.height;
                    on_ground = true;  //On ground, can now jump
                    if (dy > 0)  //If moving into wall, stop moving in that direction
                        dy = 0;
                } else if(((TileEntity) other).bottom_collides) {  //Below
                    y_pos = other.get_y() + other.hitbox.height;
                    if (dy < 0)  //If moving into wall, stop moving in that direction
                        dy = 0;
                }
            }

            hitbox.setLocation(x_pos, y_pos);
        }
    }

    public void set_health(int new_h) {
        if(new_h > 0)
            health = new_h;
        else
            health = 0;
    }

    public int get_health() {
        return health;
    }

    public boolean is_dead() {
        return dead;
    }

    //Abstract methods

    public abstract AttackEntity get_attack();  //Get current attack from entity, all living entities can attack
}
