package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HealthBar {
    private Sprite sprite;
    private float xOffSet;
    private float yOffSet;

    public HealthBar(Texture texture) {
        sprite = new Sprite(texture);
        xOffSet = -sprite.getWidth() / 2;
        yOffSet = 50;
    }
    public void setColor(float r, float g, float b, float a) {
        sprite.setColor(r, g, b, a);
    }
    public void setSize(float width, float height) {
        sprite.setSize(width, height);
    }
    public void setOffset(float xOffSet, float yOffSet) {
        this.xOffSet = xOffSet;
        this.yOffSet = yOffSet;
    }
    public void updatePosition(float heroX, float HeroY) {
        sprite.setPosition(heroX + xOffSet, HeroY + yOffSet);
    }

    public void draw(SpriteBatch batch, float x, float v) {

        sprite.draw(batch);
    }
    public void dispose() {
        sprite.getTexture().dispose();
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}

