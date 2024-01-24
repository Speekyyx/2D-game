package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;


public class MovingPlatform extends Platform{
    private float startX, endX;
    private float velocity;
    private boolean movingRight;

    public MovingPlatform(Texture texture, float x,float y,float width, float height, float endX,float velocity) {
        super(texture);
        this.setPosition(x, y);
        this.setSize(width, height);
        this.startX = x;
        this.endX = endX;
        this.velocity = velocity;
        this.movingRight = true;
    }
    public void update(float deltaTime) {
        if (movingRight) {
            this.sprite.translateX((velocity * deltaTime));
            if (this.sprite.getX() >= endX) {
                movingRight = false;
            }
        } else {
            this.sprite.translateX(-(velocity * deltaTime));
            if (this.sprite.getX() <= startX) {
                movingRight = true;
            }
        }
    }
}

