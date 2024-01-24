package com.mygdx.game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class coin {
    private Texture coinSheet;
    private Animation<TextureRegion> coinAnimation;
    private float stateTime;
    private float x, y;
    private float width, height;

    private static final int FRAME_COLS =1/* nombre de colonnes */;
    private static final int FRAME_ROWS =1/* nombre de lignes */;


    public coin(float x, float y, float width, float height) {
        this.coinSheet = new Texture("piece.png");

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        stateTime = 0f;


        TextureRegion[][] coinFrames = TextureRegion.split(coinSheet, 16,16);
        int numIdleFrames = 15;
        TextureRegion[] CoinidleFrames = new TextureRegion[numIdleFrames];
        for (int i = 0; i < numIdleFrames; i++) {
            CoinidleFrames[i] = coinFrames[0][i];
        }
        coinAnimation = new Animation<TextureRegion>(0.1f, CoinidleFrames);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = coinAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, width, height);
    }

    public void dispose() {
        coinSheet.dispose();
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
}
