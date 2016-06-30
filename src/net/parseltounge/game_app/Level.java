package net.parseltounge.game_app;

import net.parseltounge.components.ai_comp.EnemyAI;
import net.parseltounge.components.graph_comp.ImgManager;
import net.parseltounge.entities.living_ents.EnemyEntity;
import net.parseltounge.entities.Entity;
import net.parseltounge.entities.living_ents.PlayerEntity;
import net.parseltounge.entities.wall_ents.*;

import java.awt.image.BufferedImage;
import java.util.*;
import java.awt.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import java.io.File;

public class Level {
    //Contains walls and enemies in a level.

    private ArrayList<TileEntity> walls; //Wall entities
    private ArrayList<EnemyEntity> enemies; //Enemies in level
    private PlayerEntity player_ent;  //Player entity

    private HashMap<String, EnemyAI> str_to_ai_map;

    private BufferedImage background;  //Single image background

    private ImgManager tileset;

    public Level(String map_name) {  //Constructor
        walls = new ArrayList<TileEntity>();
        enemies = new ArrayList<EnemyEntity>();
        tileset = new ImgManager();
        player_ent = new PlayerEntity(0, 0);  //default player entity, if none found in level

        init_str_to_ai_map();

        Document doc = get_level_doc(map_name);
        parse_tiles(doc);
        parse_living_ents(doc);
    }

    private void init_str_to_ai_map() {
        str_to_ai_map.put("still", EnemyAI.STILL);
        str_to_ai_map.put("walk_no_turn", EnemyAI.WALK_NO_TURN);
        str_to_ai_map.put("walk_turn", EnemyAI.WALK_TURN);
        str_to_ai_map.put("chase_still", EnemyAI.CHASE_STILL);
    }

    private Document get_level_doc(String map_name) {
        try {
            DocumentBuilderFactory db_fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = db_fact.newDocumentBuilder();
            return db.parse(new File("resources/" + map_name));
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parse_tiles(Document doc) {
        //Get all basic data about map
        NamedNodeMap doc_attrs = doc.getFirstChild().getAttributes();
        int level_width = Integer.parseInt(doc_attrs.getNamedItem("width").getNodeValue());
        int level_height = Integer.parseInt(doc_attrs.getNamedItem("height").getNodeValue());
        int tile_width = Integer.parseInt(doc_attrs.getNamedItem("tilewidth").getNodeValue());
        int tile_height = Integer.parseInt(doc_attrs.getNamedItem("tileheight").getNodeValue());

        //Retrieve and configure tiles

        //Get tileset used by level
        Node tileset_node = doc.getElementsByTagName("tileset").item(0);  //Get tileset node
        String tileset_name = tileset_node.getChildNodes().item(1).getAttributes().getNamedItem("source").getNodeValue();  //Get 'source' attribute of 'image' child node

        tileset.load_atlas("resources/" + tileset_name, "tileset", tile_width, tile_height, 38, 12);  //Load in tileset

        //Access tiles
        NodeList layers = doc.getElementsByTagName("layer");
        Node main_layer = layers.item(0);  //Get first layer
        NodeList tiles = main_layer.getChildNodes().item(1).getChildNodes();  //item(1) is the 'data' node, which holds all tiles

        //Remove nodes which are not tiles
        for(int i = 0; i < tiles.getLength(); i++) {
            Node n = tiles.item(i);
            if(!(n instanceof Element)) {
                main_layer.getChildNodes().item(1).removeChild(n);  //Remove non-element nodes
            }
        }

        int current_height = 0;
        int current_width = 0;

        //Add tiles to wall_ents array
        for(current_height = 0; current_height < level_height; current_height++) {
            for (current_width = 0; current_width < level_width; current_width++) {
                Node tile = tiles.item(current_width + (current_height * level_width));  //Retrieve tile
                NamedNodeMap tile_attrs = tile.getAttributes();
                if (tile instanceof Element) {  //Only if the node is an element, and thus a tile
                    if (!tile_attrs.getNamedItem("gid").getNodeValue().equals("0")) {  //If not empty
                        //Get tile's image (-1 because id's start at 1), then pass to wall entity
                        int gid = Integer.parseInt(tile_attrs.getNamedItem("gid").getNodeValue()) - 1;
                        BufferedImage image = tileset.get_img(gid);
                        //Tile has x, y, width, height, image, and id
                        walls.add(new TileEntity(
                                current_width * tile_width,
                                current_height * tile_height,
                                tile_width,
                                tile_height,
                                image,
                                (current_width + (current_height * level_width))
                        ));
                    }
                }
            }
        }
        for(TileEntity t: walls) {
            t.set_colliding_sides(walls, level_width, level_height);
        }
    }

    private void parse_living_ents(Document doc) {
        NodeList object_groups = doc.getElementsByTagName("objectgroup");  //Get all object groups

        Node entities_group = object_groups.item(0);  //Object group of entities, default = first group

        for(int i=0; i < object_groups.getLength(); i++) {  //Go through all object groups, make sure it is the right one
            if(object_groups.item(i).getNodeName().equals("entities"))
                entities_group = object_groups.item(i);
        }

        NodeList ent_children = entities_group.getChildNodes();  //List of all of entity object group children

        //May contain text nodes, filter out objects
        ArrayList<Node> objects = new ArrayList<Node>();  //List of objects

        for(int i = 0; i < ent_children.getLength(); i++) {
            if (ent_children.item(i) instanceof Element)
                objects.add(ent_children.item(i));  //Only add if an element
        }

        for(Node n: objects) {
            NamedNodeMap n_attrs = n.getAttributes();
            String node_name = n_attrs.getNamedItem("name").getNodeValue();

            if(node_name.equals("player")) {  //Initialize player
                int x = Integer.parseInt(n_attrs.getNamedItem("x").getNodeValue());
                int y = Integer.parseInt(n_attrs.getNamedItem("y").getNodeValue());

                player_ent = new PlayerEntity(x, y);
            }
            else if(node_name.equals("enemy")) {  //Initialize enemy
                int x = Integer.parseInt(n_attrs.getNamedItem("x").getNodeValue());
                int y = Integer.parseInt(n_attrs.getNamedItem("y").getNodeValue());
                EnemyAI chosen_ai = str_to_ai_map.get(n_attrs.getNamedItem("chosen_ai").getNodeValue());
                int x_speed = Integer.parseInt(n_attrs.getNamedItem("x_speed").getNodeValue());
                String img_path = n_attrs.getNamedItem("img_path").getNodeValue();
                enemies.add(new EnemyEntity(x, y, chosen_ai, x_speed, img_path));
            }
        }
    }

    public void draw_background(Graphics g) {  //Draw level background
        g.drawImage(background, 0, 0, Game.G_WIDTH, Game.G_HEIGHT, null);
    }

    public ArrayList<Entity> get_ents() {  //Return array of all entities
        ArrayList<Entity> results = new ArrayList<Entity>();

        results.add(player_ent);
        for(Entity e: walls) {
            results.add(e);
        }
        for(Entity e: enemies) {
            results.add(e);
        }
        return results;
    }

    public PlayerEntity get_player_ent() {  //Get player entity
        return player_ent;
    }
}
