package net.parseltounge.entities.wall_ents;

import net.parseltounge.components.graph_comp.ImgManager;
import net.parseltounge.components.phys_comp.Hitbox;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Jack on 9/14/2014.
 */
public class TileEntity extends WallEntity {

    public int id;  //Signifies which tile position this tile takes up in map grid

    public TileEntity(int x, int y, int width, int height, BufferedImage image, int id_val) {
        x_pos = x;
        y_pos = y;
        dx = 0;
        dy = 0;
        id = id_val;

        //These define if one can collide with to of tile, with bottom, etc.
        //Used for groups of tiles to form collective walls, tiles which have top but no bottom, etc.
        top_collides = true;
        left_collides = true;
        right_collides = true;
        bottom_collides = true;

        hitbox = new Hitbox(x, y, width, height);
        im_man = new ImgManager();

        current_img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = current_img.getGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
    }

    public void set_colliding_sides(ArrayList<TileEntity> tiles, int map_width, int map_height) {

        for(TileEntity t: tiles) {
            if(t.id == id-map_width) //If there's a tile directly above, no collision on top
                top_collides = false;
            if(t.id == id+map_width) //If has tile directly below, no collision on bottom
                bottom_collides = false;
            if(t.id == id-1) //If has tile directly to left, no collision on left
                left_collides = false;
            if(t.id == id+1) //If has tile directly to right, no collision on right
                right_collides = false;
        }

        //Make sure collision detection is effective on outer edges of map
        if(id % map_width == 0)
            left_collides = true;
        else if((id+1) % map_width == 0)
            right_collides = true;
        else if(id < map_width)
            top_collides = true;
        else if(id > (map_height-1)*map_width)
            bottom_collides = true;
    }

}
