package net.parseltounge.game_app;

import java.awt.image.BufferStrategy;

public abstract class Session {
    //Parent class to all kinds of sessions, eg. GameSession, MenuSession, PausedSession, etc.

    Session next_session;  //Next session to be played, null unless session is ended

    public abstract Session next_session();  //Next session to be played, null unless session is ended
    public abstract void update();
    public abstract void render(BufferStrategy strategy);

}
