package net.parseltounge.entities.living_ents; /**
 * Created by Jack on 9/6/2014.
 */

import net.parseltounge.components.phys_comp.Hitbox;
import net.parseltounge.entities.Entity;
import net.parseltounge.entities.wall_ents.WallEntity;
import net.parseltounge.entities.attack_ents.AttackEntity;
import net.parseltounge.components.graph_comp.ImgManager;
import net.parseltounge.game_app.Game;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

public class PlayerEntity extends LivingEntity {
    //Player's entity in game.
    //current_img, x_pos, y_pos, dx, dy, and hitbox in Entity

    private ImgManager im_man;  //Current Image manager (see ImgManager class), stores + loads currently used images
    private ImgManager walking_im_man;  //Manages images for walking
    private ImgManager jumping_im_man;  //Manages images for jumping
    private ImgManager punching_im_man;  //Manages images for jumping

    private String state;  //State of player, eg. 'standing', 'walking', 'punching', etc.

    private AttackEntity punch; //Punch attack

    AffineTransformOp tf_hor_flip;  //Transform op to flip player image horizontally

    int punch_start;  //Which update punch started at, keep track of how many updates punch has existed for
    int punch_len;  //Number of updates a punch lasts for
    int hurt_start;  //Which update being hurt started at
    int hurt_len;  //Length hurt for

    int walk_img_len = 10;  //Length an image is displayed for before next one while walking
    int walk_start = 0;  //When walk sequence started

    private boolean left_facing;  //Whether or not player faces left
    private boolean right_facing; //Whether or not player faces right

    private int hbox_offset_top;  //Offset of top of hitbox from top of current image
    private int hbox_offset_left; //Offset of left side of hitbox from left side of current image

    //Methods

    public PlayerEntity(int x, int y) {  //Constructor

        //Initialize all state variables
        health = 200;
        x_pos = x;
        y_pos = y;
        dx = 0;
        dy = 0;

        state = "standing";  //Currently standing

        punch_len = 10;  //Punch lasts 10 updates
        walk_img_len = 10;
        hurt_len = 28;
        on_ground = false;
        left_facing = true;  //Start off facing left
        right_facing = false;

        im_man = new ImgManager();  //Will later on simply be a reference to currently used image manager

        //Initialize all image managers
        walking_im_man = new ImgManager();
        jumping_im_man = new ImgManager();
        punching_im_man = new ImgManager();

        punch = new AttackEntity(this, 20);  //punch comes from player and deals 20 damage
        punch.get_hitbox().setBounds(x_pos, y_pos, 30, 30);  //Set size of attack

        //Uncomment these two lines when walking and jumping atlases are made
        walking_im_man.load_atlas("resources/player_walk.png", "walking", 102, 148, 6, 1);
        //jumping_im_man.load_atlas("asdf", "jumping", 32, 32, 5, 1);
        //punching_im_man.load_atlas("asdf", "punching", 32, 32, 5, 1);

        im_man.load_image("resources/player_img.png", "player_img");  //Load sprite image
        current_img = im_man.get_img("player_img");

        //Create transform op to flip player image when turning around
        AffineTransform tf = AffineTransform.getScaleInstance(-1, 1);
        tf.translate(-current_img.getWidth(), 0);
        tf_hor_flip = new AffineTransformOp(tf, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        hbox_offset_top = 15;
        hbox_offset_left = 15;
        hitbox = new Hitbox(x_pos,y_pos,70,85);  //Initialize hitbox
    }

    public int get_health() {
        return health;
    }

    public AttackEntity get_attack() {  //Get current attack.
        if(state.equals("punching") && update_counter - punch_start >= punch_len) {
            punch.update();  //Make sure punch is updated to most current state
            return punch;
        }
        else
            return null;
    }

    //State initialisation functions when DFA state changes

    void stand_init() {
        state = "standing";
        current_img = im_man.get_img("player_img");
        dx = 0;
    }

    void punch_init() {
        state = "punching";
        punching_im_man.set_current_img(0);
        punch_start = update_counter;
        dx = 0;
    }

    void jump_init() {
        state = "jumping";
        jumping_im_man.set_current_img(0);
        dy = -15;
    }

    void walk_init() {
        state = "walking";
        walking_im_man.set_current_img(0);
        walk_start = update_counter;

        current_img = walking_im_man.get_next_img();
        if(left_facing) {
            dx = -5;
            current_img = tf_hor_flip.filter(current_img, null);  //Make sure facing right way
        } else
            dx = 5;
    }

    void hurt_init() {
        state = "hurt";
        hurt_start = update_counter;
        invincible = true;
        if(left_facing) {
            dx = 3;
        } else
            dx = -3;
        dy = -5;
    }

    public void draw(Graphics g) {  //Draw entity to graphics
        g.drawImage(current_img, x_pos - hbox_offset_left, y_pos - hbox_offset_top, 100, 100, null);
        g.setColor(Color.black);
        g.drawRect(x_pos, y_pos, hitbox.width, hitbox.height);
    }

    public void update() {  //Update state of entity
        //Increase update counter
        update_counter++;

        //Update state of player AI DFA
        switch(state) {
            case "standing":  //If standing, can change to walking, jumping or punching
                if((Game.left_pressed || Game.right_pressed) && !(Game.left_pressed && Game.right_pressed)) {
                    //If only one of left or right pressed right; if both, stay still
                    walk_init();
                } else if(Game.space_pressed && on_ground) {
                    jump_init();
                } else if(Game.f_pressed) {
                    punch_init();
                }
                break;
            case "walking":  //If walking, can change to jumping, punching or standing
                if(Game.space_pressed && on_ground) {  //Jump
                    jump_init();
                } else if(!on_ground) {  //If falls off cliff, jumping but without upwards velocity
                    jump_init();
                    dy = 0;
                } else if(Game.f_pressed) {  //Punch
                    punch_init();
                } else if(Game.left_pressed && Game.right_pressed) {  //If both left and right pressed, stand
                    stand_init();
                } else if(Game.left_pressed && !left_facing) {  //Update to face left
                    left_facing = true;
                    right_facing = false;
                    dx = -5;
                    current_img = tf_hor_flip.filter(current_img, null);
                } else if(Game.right_pressed && !right_facing) {  //Update to face right
                    right_facing = true;
                    left_facing = false;
                    dx = 5;
                    current_img = tf_hor_flip.filter(current_img, null);
                } else if(!(Game.right_pressed || Game.left_pressed)) {  //If neither walk button pressed, stand
                    stand_init();
                }
                break;
            case "jumping":  //If jumping, can change to standing
                if(on_ground) {
                    stand_init();
                } else if(Game.left_pressed) {
                    dx = -5;
                } else if(Game.right_pressed) {
                    dx = 5;
                } else {
                    dx = 0;
                }
                break;
            case "punching":  //If punching, can change to standing
                if((update_counter - punch_start) > punch_len) {
                    stand_init();
                }
                break;
            case "hurt":  //If hurt, can change to standing
                if(on_ground) {
                    stand_init();
                }
                break;
            default:  //Raise error if state is not comprehensible
                break;
        }
        //end DFA update


        //Change position of player via speed
        x_pos += dx;
        y_pos += dy;

        //Simulate gravity acceleration
        if(dy < 15)
            dy += 1;

        hitbox.setLocation(x_pos, y_pos);  //Update hitbox location

        //Set location of punch
        //NB: Punch is updated by session when present, not by player
        if(left_facing)
            punch.set_x(x_pos - punch.get_hitbox().width);  //If facing left, punch is on left of player
        else
            punch.set_x(x_pos + hitbox.width);  //If facing right, punch is on right of player

        punch.set_y(y_pos + hitbox.height/2);


        //Update image


        //Update current_img according to DFA state
        if(state.equals("hurt")) {
            current_img = im_man.get_img("player_img");  //Standing for now
            if(right_facing)
                current_img = tf_hor_flip.filter(current_img, null);  //Make sure facing right way
        }
        else if(state.equals("walking")) {  //If walking, update image
            if((update_counter - walk_start) % walk_img_len == 0) {  //Update to next image after a set time
                current_img = walking_im_man.get_next_img();
                if (left_facing)
                    current_img = tf_hor_flip.filter(current_img, null);  //Make sure facing right way
            }
        }
        else if(state.equals("standing")) {  //If standing, update image
            current_img = im_man.get_img("player_img");
            if(right_facing)
                current_img = tf_hor_flip.filter(current_img, null);  //Make sure facing right way
        }
        else if(state.equals("jumping")) {  //If jumping, update image
            current_img = im_man.get_img("player_img");  //Standing for now
            if(right_facing)
                current_img = tf_hor_flip.filter(current_img, null);  //Make sure facing right way
        }
        else if(state.equals("punching")) {  //If punching, update image
            current_img = im_man.get_img("player_img");  //Standing for now
            if(right_facing)
                current_img = tf_hor_flip.filter(current_img, null);  //Make sure facing right way
        }


        //Flash invisible to visible while in this state to show user that player is invincible, in this state when hurt
        //Separate from other images because can be invincible while walking, jumping, etc.
        if(invincible) {
            if ((update_counter - hurt_start) >= hurt_len)
                invincible = false;
            if ((update_counter - hurt_start) % 4 == 0) {
                //Change between invisible and visible every 4 updates, flashing
                if ((update_counter - hurt_start) % 8 == 0) {  //Visible every 8 updates
                    current_img = im_man.get_img("player_img");  //Make visible
                    if(right_facing)  //Make sure image is facing right way
                        current_img = tf_hor_flip.filter(current_img, null);
                }
                else {  //Invisible for every 4 updates not covered above (ie. every other 4 updates)
                    current_img = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);  //Reset image to transparent rectangle
                    Graphics2D g = (Graphics2D) current_img.getGraphics();
                    g.setComposite(AlphaComposite.Clear);  //Draw transparent rectangle over it
                    g.fillRect(x_pos, y_pos, 100, 100);
                    g.dispose();
                }
            }
        }


        //Flip image according to direction of movement
        if(dx < 0 && !left_facing) {
            left_facing = true;  //Only flip once
            right_facing = false;
            current_img = tf_hor_flip.filter(current_img, null); //Flip image to left
        }
        else if(dx > 0 && !right_facing) {
            right_facing = true;
            left_facing = false;
            current_img = tf_hor_flip.filter(current_img, null); //Flip back
        }

        if(health <= 0)  //If health too low, died
            dead = true;

        on_ground = false;  //If no collision, this won't be updated to 'true' during game update, so in air
    }

    public void collision(Entity other) {  //Collided with 'other', called by game loop, take action

        if(other instanceof WallEntity) {  //If collided with wall
                wall_collision((WallEntity) other);  //Method defined in LivingEntity
        }
        else if(other instanceof EnemyEntity) {
            if(!state.equals("hurt") && !invincible) {
                hurt_init();
                health -= 10;  //For now, lose 10 health per enemy collision
                hurt_start = update_counter;  //Start being invincible
            }
        }
        else if(other instanceof AttackEntity) {
            AttackEntity oth = (AttackEntity) other;
            if(oth.get_source() instanceof EnemyEntity) {  //Only hurt if from enemy, cannot get hurt by own attacks
                //hit by enemy
                if(!state.equals("hurt") && !invincible) {
                    hurt_init();
                    health -= oth.get_damage();
                    hurt_start = update_counter;  //Start being invincible
                }
            }
        }

    }

}