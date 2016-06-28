package net.parseltounge.components.graph_comp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.net.*;

public class ImgManager {
    //Loads, stores and draws images
    //Will have one/many ImgManagers per entity, stored in entity objects for personal use
    //(eg. one will store walking sequence, one will store jump sequence, etc.)

    private ArrayList<String> Img_names;        //Array of image names
    private ArrayList<BufferedImage> Img_srcs;  //Array of actual images

    private int current_img_index;  //Index of current image. Incremented when get_next_img() is called.
    //Methods

    public ImgManager() {  //Constructor
        Img_names = new ArrayList<String>();
        Img_srcs = new ArrayList<BufferedImage>();
        current_img_index = 0;
    }

    public void load_image(String dir, String nm) {  //Load image from 'dir' and store with name 'nm'
        try {
            //Comment this out when making jar
            BufferedImage image = ImageIO.read(new File(dir));

            //Comment this out when not making jar
            //URL url = this.getClass().getClassLoader().getResource(dir);
            //BufferedImage image = ImageIO.read(url);

            //Resize image, so all images are same size
            BufferedImage output = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics g = output.getGraphics();
            g.drawImage(image, 0, 0, 100, 100, null);
            g.dispose();

            //Must add existence to arrays of names and images
            Img_names.add(nm);
            Img_srcs.add(output);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void load_image_sect(String dir, String nm, int x, int y, int w, int h) {  //Load rectangular part of image
        try {
            BufferedImage image = ImageIO.read(new File(dir));
            image = image.getSubimage(x, y, w, h);
            Img_names.add(nm);
            Img_srcs.add(image);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void load_atlas(String dir, String nm, int part_width, int part_height, int rows, int cols) {  //Load all images in a spritesheet/atlas
        try {
            BufferedImage image = ImageIO.read(new File(dir));

            for (int y = 0; y < cols*part_height; y += part_height) {
                for (int x = 0; x < rows*part_width; x += part_width) {
                    //Must make name for image
                    int im_id = x + (y * rows) + 1;  //GID of current image. 0 at left, increases left->right then up->down.
                    String name = nm + "_" + String.valueOf(im_id);  //eg. tile_1, tile_2, tile_3, ..., tile_n.
                    Img_srcs.add(image.getSubimage(x, y, part_width, part_height));  //Load in actual sub image.
                    Img_names.add(name);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public String[] get_names() {  //Get array of image names
        return (String[]) Img_names.toArray();
    }

    public BufferedImage get_img(String nm) {  //Return image by name 'nm'
        int index = Img_names.indexOf(nm); //Index of file
        return Img_srcs.get(index);
    }

    public BufferedImage get_img(int index) {  //Return image by index, useful for tilesets
        return Img_srcs.get(index);
    }

    public BufferedImage get_next_img() {  //Get next image in list of images. Used for image sequences.

        if(current_img_index >= Img_srcs.size())  //If at last image, return first one
            current_img_index = 0;

        BufferedImage result = Img_srcs.get(current_img_index);  //return image

        current_img_index++;

        return result;
    }

    public int get_height(String nm) {  //Get height of image 'nm'
        return Img_srcs.get(Img_names.indexOf(nm)).getHeight();
    }

    public int get_width(String nm) {  //Get width of image 'nm'
        return Img_srcs.get(Img_names.indexOf(nm)).getWidth();
    }

    public void set_current_img(int val) {
        current_img_index = val;
    }

}
