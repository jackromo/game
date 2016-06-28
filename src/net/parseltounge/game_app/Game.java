package net.parseltounge.game_app;

import javax.swing.*;  //For Swing graphics
import java.awt.*;
import java.awt.event.*;

public class Game {
    //Main Game class, responsible for creating and managing canvas and event handlers

    JFrame frame;  //Contains all game components
    JPanel panel;  //Contains GameCanvas
    GameCanvas g_canvas;  //Actual game's image

    public static final int G_WIDTH = 800;  //Width and height of game window
    public static final int G_HEIGHT = 600;
    public static final int G_FPS = 100;  //Frames per second and Updates per second of game
    public static final int G_UPS = 50;

    public static boolean left_pressed;  //Whether certain key is pressed (updated by Game's key listener)
    public static boolean right_pressed;
    public static boolean space_pressed;
    public static boolean esc_pressed;
    public static boolean f_pressed;

    public static void main(String[] args) {
        Game g = new Game();
    }

    public Game() {  //Game constructor
        frame = new JFrame("Game Trial");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Allow user to close window with close button

        panel = new JPanel();   //Will contain game canvas
        panel.setPreferredSize(new Dimension(800, 600));  //Set size of panel

        g_canvas = new GameCanvas();  //Create actual game canvas
        g_canvas.addKeyListener(new G_KeyListener());  //Add listener for key presses

        panel.add(g_canvas);

        frame.getContentPane().add(BorderLayout.CENTER, panel);

        frame.setSize(G_WIDTH, G_HEIGHT);
        frame.setResizable(false);
        frame.setVisible(true);  //Needs to be visible before game starts so game can get buffer strategy
        frame.requestFocus();

        g_canvas.start_game();
    }

    //Event listeners

    class G_KeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {  //Checks for pressed keys and responds appropriately
            int key_code = e.getKeyCode();  //Get key pressed
            if(key_code == KeyEvent.VK_LEFT)
                Game.left_pressed = true;
            if (key_code == KeyEvent.VK_RIGHT)
                Game.right_pressed = true;
            if (key_code == KeyEvent.VK_SPACE)
                Game.space_pressed = true;
            if (key_code == KeyEvent.VK_F)
                Game.f_pressed = true;
            if (key_code == KeyEvent.VK_ESCAPE)
                Game.esc_pressed = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {  //Checks for released keys and responds appropriately
            int key_code = e.getKeyCode();  //Get key pressed
            if (key_code == KeyEvent.VK_LEFT)
                Game.left_pressed = false;
            if (key_code == KeyEvent.VK_RIGHT)
                Game.right_pressed = false;
            if (key_code == KeyEvent.VK_SPACE)
                Game.space_pressed = false;
            if (key_code == KeyEvent.VK_F)
                Game.f_pressed = false;
            if (key_code == KeyEvent.VK_ESCAPE)
                Game.esc_pressed = false;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }
    }

}
