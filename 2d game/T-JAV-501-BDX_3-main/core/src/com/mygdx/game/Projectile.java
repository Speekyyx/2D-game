package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Projectile {
    private Sprite sprite;
    private float velocityX, velocityY;
    private int damage = 10;
    public Projectile(Texture texture, float x, float y, float velocityX, float velocityY) {
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.setSize(50, 20);
    }

    public void update(float deltaTime) {
        sprite.translate(velocityX * deltaTime, velocityY * deltaTime);
    }

    public void draw(SpriteBatch batch) {
        if (sprite != null) {
            sprite.draw(batch);
        }
    }

    public boolean isOffScreen() {
        return sprite.getX() < 0 || sprite.getX() > Gdx.graphics.getWidth()
                || sprite.getY() < 0 || sprite.getY() > Gdx.graphics.getHeight();
    }
    public int setSize(float width, float height) {
        sprite.setSize(width, height);
        return 0;
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
    public int getDamage() {
        return damage;
    }
}
