package net.parseltounge.game_app;

import java.awt.image.BufferStrategy;

public class PausedSession extends Session{
    //Session of being paused in game.

    private boolean paused;  //Whether or not session is paused.

    public PausedSession() {
        paused = true;  //Is paused during existence of paused_session
    }

    public Session next_session() {
        if(!paused) {
            paused = true;
            return next_session;
        } else
            return null;
    }

    public void update() {

    }

    public void render(BufferStrategy strategy) {

    }
}
