package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Background {
    Texture img;
    public Background() {
        img = new Texture("water.gif");
    }
    public void render(SpriteBatch batch) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        batch.draw(img, 0, 0, width, height);
    }
    public void  dispose() {
        img.dispose();
    }
}
