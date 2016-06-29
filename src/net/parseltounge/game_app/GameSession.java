package net.parseltounge.game_app;

import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.Array;
import java.util.*;

import net.parseltounge.components.phys_comp.Hitbox;
import net.parseltounge.entities.*;  //Import all entities
import net.parseltounge.entities.attack_ents.*;
import net.parseltounge.entities.living_ents.EnemyEntity;
import net.parseltounge.entities.living_ents.LivingEntity;
import net.parseltounge.entities.living_ents.PlayerEntity;
import org.w3c.dom.css.Rect;

public class GameSession extends Session {
    //Current session of playing game.

    private PlayerEntity player_ent;  //Entity controlled by player
    private volatile ArrayList<Entity> ent_array;  //list of all entities
    private volatile ArrayList<Entity> active_ents;  //All entities which will update

    private Level level;  //Current level played

    private int camera_x;  //Location of top-left corner of camera
    private int camera_y;

    private boolean game_over;  //If game is over, checked by main game loop
    private boolean paused;

    //Methods

    public GameSession() {  //Constructor
        ent_array = new ArrayList<Entity>();
        active_ents = new ArrayList<Entity>();

        level = new Level("demo_map.tmx");  //Level to be played in game
        player_ent = level.get_player_ent();

        for(Entity e : level.get_ents()) {  //Get all entities stored in level for current use
            ent_array.add(e);
            if(e instanceof LivingEntity) {  //Will eventually check for any entity which is active, eg. block which changes color
                active_ents.add(e);
            }
        }
    }

    public Session next_session() {
        if(game_over) {
            return next_session;
        } else if(paused) {
            paused = false;
            return next_session;
        } else
            return null;
    }

    public void update() {
        handle_pause();
        if(!paused) {
            handle_collisions();
            update_ents();
            handle_collisions();
            handle_attacks();
            remove_dead_ents();
            update_camera();
            handle_player_death();
        }
    }

    public void render(BufferStrategy strategy) {  //Render next frame
        Graphics g = strategy.getDrawGraphics();  //Get graphics context to render on

        g.setColor(Color.white);
        g.fillRect(0,0,Game.G_WIDTH,Game.G_HEIGHT);  //Erase old image on graphics to draw new things to

        g.setColor(Color.black);

        for(Entity e : ent_array) {  //Draw player entity to graphics
            e.set_x(e.get_x() - camera_x);  //Shift entity to left if camera is to the right, and vice versa
            e.set_y(e.get_y() - camera_y);  //Shift entity down if camera is up, and vice versa

            e.draw(g);  //Draw entity in new temporary position

            e.set_x(e.get_x() + camera_x);  //Set entity back to original position
            e.set_y(e.get_y() + camera_y);
        }

        //Draw health meter
        g.setColor(Color.red);
        g.fillRect(Game.G_WIDTH - 200, 0, player_ent.get_health(), 50);

        g.dispose();
    }

    private void handle_pause() {
        if(Game.esc_pressed) {
            Game.esc_pressed = false;  //Prevent rapid swap between pause and game
            paused = true;  //Invert value of 'paused'
            //next_session = new PausedSession();  //Will enter paused session once PausedSession exists
        }
    }

    private void handle_collisions() {
        // Check for collisions that will happen next update
        for(Entity e : active_ents) {  //Only active entities can collide
            Hitbox hbox_temp = new Hitbox(e.hitbox.x + e.get_dx(), e.hitbox.y + e.get_dy(), e.hitbox.width, e.hitbox.height);
            for(Entity other : ent_array) {
                if(other.hitbox.intersects(hbox_temp) && e != other) {
                    e.collision(other);
                }
            }
        }
    }

    private void update_ents() {
        for(Entity e : active_ents) {
            e.update(ent_array);
        }
    }

    private void handle_attacks() {
        //Request new attack entities from existing entities
        //Append to attacks list first to avoid ConcurrentModificationException
        ArrayList<AttackEntity> attacks = new ArrayList<AttackEntity>();
        for(Entity e: active_ents) {
            if(e instanceof LivingEntity && ((LivingEntity) e).get_attack() != null) {
                if(!active_ents.contains(((LivingEntity) e).get_attack()))  //If ent_array does not already have attack
                    attacks.add(((LivingEntity) e).get_attack());  //Get attack from entity and add to attack array
            }
        }
        active_ents.addAll(attacks);  //Attacks are active entities
        ent_array.addAll(attacks);  //Add all attacks to current entities array

        //Remove any old attacks
        //NB: Must add to external arrays first, then remove, to avoid ConcurrentModificationException
        ArrayList<AttackEntity> old_attacks = new ArrayList<AttackEntity>();
        for(Entity e: active_ents) {
            if(e instanceof AttackEntity && !attacks.contains(e)) {
                old_attacks.add((AttackEntity) e);
            }
        }
        ent_array.removeAll(old_attacks);
        active_ents.removeAll(old_attacks);
    }

    private void remove_dead_ents() {
        ArrayList<LivingEntity> dead_ents = new ArrayList<LivingEntity>();
        for(Entity e: active_ents) {
            if(e instanceof LivingEntity && ((LivingEntity) e).is_dead()) { //If a dead LivingEntity
                dead_ents.add((LivingEntity) e);
            }
        }
        ent_array.removeAll(dead_ents);
        active_ents.removeAll(dead_ents);
    }

    private void update_camera() {
        //If too far right, camera is set so rightmost side of player is 50 pixels right of middle of screen
        //If too far left, camera is set so left side of player is 50 pixels left of screen middle
        if(player_ent.get_x() + player_ent.get_hitbox().width - camera_x > Game.G_WIDTH/2 + 50)  //If too far to right
            camera_x = player_ent.get_x() + player_ent.get_hitbox().width - Game.G_WIDTH/2 - 50;
        else if(player_ent.get_x() - camera_x < Game.G_WIDTH/2 - 50)  //If too far to left
            camera_x = player_ent.get_x() - Game.G_WIDTH/2 + 50;

        //If too far down, camera is set so bottom of player is 100 pixels below middle of screen
        //If too far up, camera is set so top of player is 100 pixels above middle of screen
        if(player_ent.get_y() + player_ent.get_hitbox().height - camera_y > Game.G_HEIGHT/2 + 100)  //If too far down
            camera_y = player_ent.get_y() + player_ent.get_hitbox().height - Game.G_HEIGHT/2 - 100;
        else if(player_ent.get_y() - camera_y < Game.G_HEIGHT/2 - 100)  //If too far up
            camera_y = player_ent.get_y() - Game.G_HEIGHT/2 + 100;
    }

    private void handle_player_death() {
        if(!active_ents.contains(player_ent)) {  //Player does not exist, must have died, end game
            game_over = true;
            next_session = new GameSession();  //Create new game session (restart)
            //Will later give a GameOverSession or something similar
        }
    }
}
