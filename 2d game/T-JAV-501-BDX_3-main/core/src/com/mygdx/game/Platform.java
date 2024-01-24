package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Platform {
    protected Sprite sprite;

    public Platform(Texture texture) {
        sprite = new Sprite(texture);
    }
    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
    }

    public void setSize(float width, float height) {
        sprite.setSize(width, height);
    }
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
    public Sprite getSprite() {
        return  new Sprite(sprite);
    }
    public float getX() {
        return sprite.getX();
    }
    public float getY() {
        return sprite.getY();
    }
    public float getWidth() {
        return sprite.getWidth();
    }
    public float getHeight() {
        return sprite.getHeight();
    }
}
