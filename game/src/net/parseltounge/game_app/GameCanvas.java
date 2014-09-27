package net.parseltounge.game_app; /**
 * Created by Jack on 9/6/2014.
 */

import java.awt.*;
import java.awt.image.*;  //For BufferedImage
import java.awt.event.*;  //For event listeners
import java.util.*; //For arraylist

public class GameCanvas extends Canvas{
    //Game canvas to draw to. Contains info about graphics, main game loop and current session.

    private Thread update_thread;  //Thread for updating game state
    private Thread render_thread;  //Thread for rendering and painting game
    private final Object session_lock = new Object();  //Object for synchronising access to session

    private volatile boolean running;  //Termination of game loop, ie. close window

    private int frame_time;  //How long a frame should take (milliseconds)
    private int update_time;  //How long an update should take (milliseconds)

    private BufferStrategy strategy;  //Buffer strategy for canvas

    private volatile Session session;  //Currently playing session

    //Methods

    public GameCanvas() {  //Constructor
        frame_time = 1000 / Game.G_FPS;  //Period in milliseconds = (1/Frequency) * 1000
        update_time = 1000 / Game.G_UPS;

        update_thread = new Thread(new UpdateRunnable());
        render_thread = new Thread(new RenderRunnable());
        this.setBounds(0, 0, Game.G_WIDTH, Game.G_HEIGHT);

        session = new GameSession();
    }

    public void start_game() {  //Start game loop (run anim_thread)
        //Buffer strategy cannot get strategy of non-visible component, so needs to be done after jframe is visible
        this.createBufferStrategy(2);  //Double buffer strategy
        strategy = this.getBufferStrategy();

        //if(anim_thread == null)
        //    anim_thread = new Thread(this);
        if(!running) {
            running = true;
            update_thread.start();
            render_thread.start();
        }
    }

    class UpdateRunnable implements Runnable {
        //Update loop, runs in separate thread.

        public void run() {
            long before_time = System.currentTimeMillis();  //When next iteration starts, updated each iteration
            long after_time = 0;  //After update complete

            while(running) {
                synchronized (session_lock) {
                    session.update();
                    if (session.next_session() != null) {
                        session = session.next_session();
                    }
                }

                after_time = System.currentTimeMillis();

                long sleep_time = update_time - (after_time - before_time);  //Sleep for remainder of period time

                if(sleep_time < 0) {
                    sleep_time = 5;  //Sleep for 5ms anyway
                }

                try {
                    Thread.sleep(sleep_time);
                } catch (Exception e) { e.printStackTrace(); }


                before_time = System.currentTimeMillis();
            }

            System.exit(0);  //Stop everything after game stops running, will have termination logic later (eg. save)
        }
    }

    class RenderRunnable implements Runnable {
        //Render-paint loop, runs in separate thread.

        public void run() {
            long before_time = System.currentTimeMillis();  //When next iteration starts, updated each iteration
            long after_time = 0;  //After update complete

            while(running) {
                synchronized (session_lock) {
                    session.render(strategy);
                }
                paint_game();

                after_time = System.currentTimeMillis();

                long sleep_time = frame_time - (after_time - before_time);  //Sleep for remainder of period time

                if(sleep_time < 0) {
                    sleep_time = 5;  //Sleep for 5ms anyway
                }

                try {
                    Thread.sleep(sleep_time);
                } catch (Exception e) { e.printStackTrace(); }

                before_time = System.currentTimeMillis();
            }
        }
    }

    private void paint_game() {  //Paint rendered frame to panel (active paint, therefore not paintComponent())
        try {
            strategy.show();  //Show drawn graphics to canvas
        } catch (Exception e) { e.printStackTrace(); }
    }

}
