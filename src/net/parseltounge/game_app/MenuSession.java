package net.parseltounge.game_app;

import java.awt.image.BufferStrategy;

public class MenuSession extends Session {
    //Main menu session. Game starts up with this.

    public MenuSession() {
    }

    public Session next_session() {
        return next_session;
    }

    public void update() {
        next_session = new GameSession();  //Currently goes straight to game session
    }

    public void render(BufferStrategy strategy) {

    }
}
