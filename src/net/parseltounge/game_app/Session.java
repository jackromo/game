package net.parseltounge.game_app;

import java.awt.image.BufferStrategy;

/**
 * Created by Jack on 9/13/2014.
 */
public abstract class Session {
    //Parent class to all kinds of sessions, eg. GameSession, MenuSession, PausedSession, etc.

    Session next_session;  //Next session to be played, null unless session is ended

    public abstract Session next_session();  //Next session to be played, null unless session is ended
    public abstract void update();
    public abstract void render(BufferStrategy strategy);

}
