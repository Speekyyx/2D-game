package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

public class Hero {
    public boolean isOnGround() {
        return isOnGround;
    }
    enum State {
        IDLE, WALKING, JUMPING,ATTACKING
    }
    private State currentState;
    private State previousState;
    private Animation<TextureRegion> heroIdle;
    private Animation<TextureRegion> heroWalking;
    private Animation<TextureRegion> heroJumping;
    private Animation<TextureRegion> heroAttacking;
    private float stateTimer;
    private  HealthBar healthBar;
    private int maxHealth = 100;
    private int currentHealth = maxHealth;
    private float speed = 200;
    private float jumpSpeed = 450;
    private float gravity = -15;
    public float velocityY = 0;
    public float velocityX = 0;
    public boolean isOnGround = true;
    float lastXPosition;
    private float x,y;
    public float herowidth;
    public float heroheight;
    private Texture heroSheet;
    private float hitboxWidth, hitboxHeight;
    private boolean isBlocked = false;
    int frameWidth = 288;
    int frameHeight = 128;
    private float attackRange = 200;
    private int attackDamage = 50;

    public Hero(Texture healthBarTexture){
        healthBar = new HealthBar(healthBarTexture);
        healthBar.setSize(100, 10);
        healthBar.setOffset(55, heroheight + 10);
        heroSheet = new Texture("hero_sheet.png");
        this.herowidth=500;
        this.heroheight=350;
        this.x = 0;
        this.y = 20;
        this.hitboxWidth = frameWidth * 0.12f; // Ajuster à 80% de la largeur du cadre
        this.hitboxHeight = frameHeight * 0.8f; // Ajuster à 80% de la hauteur du cadre
        initializeAnimations();
    }
        public  void initializeAnimations() {
        TextureRegion[][] idleFrames= TextureRegion.split(heroSheet, frameWidth, frameHeight);
        int numIdleFrames = 8;
        TextureRegion[] heroIdleFrames = new TextureRegion[numIdleFrames];
        for (int i = 0; i < numIdleFrames; i++) {
            heroIdleFrames[i] = idleFrames[0][i];
        }
        heroIdle = new Animation<TextureRegion>(0.1f, heroIdleFrames);
        TextureRegion[][] walkFrames = TextureRegion.split(heroSheet, frameWidth, frameHeight);
        int numWalkFrames = 8;
        TextureRegion[] herowalkFrames = new TextureRegion[numWalkFrames];
            for (int i = 0; i < numWalkFrames; i++) {
                herowalkFrames[i] =walkFrames [2][i];
            }
            heroWalking = new Animation<TextureRegion>(0.1f, herowalkFrames);
            TextureRegion[][] jumpFrames = TextureRegion.split(heroSheet, frameWidth, frameHeight);
            int numJumpFrames = 3;
            TextureRegion[] heroJumpFrames = new TextureRegion[numJumpFrames];
            for (int i = 0; i < numJumpFrames; i++) {
                heroJumpFrames[i] = jumpFrames[4][i];
            }
            heroJumping = new Animation<TextureRegion>(0.1f, heroJumpFrames);
            TextureRegion[][] attackFrames = TextureRegion.split(heroSheet, frameWidth, frameHeight);
            int numAttackFrames = 8;
            TextureRegion[] heroAttackFrames = new TextureRegion[numAttackFrames];
            for (int i = 0; i < numAttackFrames; i++) {
                heroAttackFrames[i] = attackFrames[5][i];
            }
            heroAttacking = new Animation<TextureRegion>(0.2f, heroAttackFrames);
            currentState = State.IDLE;
            previousState = State.IDLE;
            stateTimer = 0;
            lastXPosition = x;
    }
    public void moveLeft(float deltaTime) {
            x-=speed*deltaTime;
        if (isOnGround) {
            currentState = State.WALKING;
        }
        lastXPosition = x;
    }
    public void moveRight(float deltaTime) {
        x+=speed*deltaTime;
        if (isOnGround) {
            currentState = State.WALKING;
        }
        lastXPosition = x;
    }
    public void jump(float deltaTime) {
        if (isOnGround) {
            setVelocityY(jumpSpeed);
            setOnGround(false);
        }
    }
    public void decreaseHealth(int amount) {
        currentHealth -= amount;
        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }
    public void increaseHealth(int amount) {
        currentHealth += amount;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }
    public void attack(ArrayList<Monster> monsters) {
        if(currentState != State.ATTACKING) {
            for (Monster monster : monsters) {
                if (isMonsterInRange(monster)) {
                    currentState = State.ATTACKING;
                    stateTimer = 0;
                    System.out.println("Attack method called");

                    monster.takeDamage(attackDamage);
                }
            }
        }
    }
    private boolean isMonsterInRange(Monster monster) {
        float distanceX = getHitboxX() - monster.getX();
        float distanceY = getHitboxY() - monster.getY();
        float distanceToMonster = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

        boolean inRange = distanceToMonster <= attackRange;

        // Afficher la distance pour le débogage
        System.out.println("Distance to Monster: " + distanceToMonster + ", In Range: " + inRange);

        return inRange;
    }
    public Projectile isHitByProjectile(ArrayList<Projectile> projectiles) {
        for (Projectile projectile : projectiles) {
            float heroLeft = getHitboxX();
            float heroRight = heroLeft + getHitboxWidth();
            float heroTop = getHitboxY() + getHitboxHeight();
            float heroBottom = getHitboxY();

            float projectileLeft = projectile.getX();
            float projectileRight = projectileLeft + projectile.getWidth();
            float projectileTop = projectile.getY() + projectile.getHeight();
            float projectileBottom = projectile.getY();

            if (heroLeft < projectileRight && heroRight > projectileLeft &&
                    heroTop > projectileBottom && heroBottom < projectileTop) {
                return projectile;
            }
        }
        return null;
    }
    public void updateHealthBar() {
        float healthPercentage = (float) currentHealth / maxHealth;
        healthBar.setSize(healthPercentage * 100, 10);
        if (healthPercentage > 0.5f) {
            healthBar.setColor(0, 1, 0, 1);
        } else if (healthPercentage > 0.2f) {
            healthBar.setColor(1, 1, 0, 1);
        } else {
            healthBar.setColor(1, 0, 0, 1);
        }
    }
    private void updateState() {
        boolean stateChanged = currentState != previousState;
        previousState = currentState;

        if (currentState == State.ATTACKING && heroAttacking.isAnimationFinished(stateTimer)) {
            currentState = State.IDLE; // ou un autre état approprié
        } else {
            if (!isOnGround) {
                currentState = State.JUMPING;
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                currentState = State.WALKING;
            } else if (currentState != State.ATTACKING) { // Ajout de cette condition
                currentState = State.IDLE;
            }
        }

        if (stateChanged) {
            stateTimer = 0;
        }
    }
    private TextureRegion getFrame() {
        TextureRegion region;
        switch (currentState) {
            case ATTACKING:
                region = heroAttacking.getKeyFrame(stateTimer);
                break;
            case JUMPING:
                region = heroJumping.getKeyFrame(stateTimer, false);
                break;
            case WALKING:
                region = heroWalking.getKeyFrame(stateTimer, true);
                break;
            case IDLE:
            default:
                region = heroIdle.getKeyFrame(stateTimer, true);
                break;
        }
        stateTimer += currentState == previousState ? Gdx.graphics.getDeltaTime() : 0;
        return region;
    }
    public void draw (SpriteBatch batch, ShapeRenderer shapeRenderer, boolean debugMode) {
        updateState();
        TextureRegion currentFrame = getFrame();
        batch.draw(currentFrame, x, y, herowidth, heroheight);
        healthBar.draw(batch,x,y + heroheight);
        if (debugMode) {
            batch.end();

            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 0, 0, 0.5f); // Rouge semi-transparent
            shapeRenderer.rect(getHitboxX(), getHitboxY(), getHitboxWidth(), getHitboxHeight());
            shapeRenderer.end();

            batch.begin();
        }

        }
    public void dispose() {
        heroSheet.dispose();
        healthBar.dispose();
    }
    public void update(float deltaTime) {
        TextureRegion currentFrame = getFrame();
        if (!isOnGround) {
            velocityY += gravity;
        }

        if (currentState == State.ATTACKING || currentState == previousState) {
            stateTimer += deltaTime;
        }
        y += velocityY * deltaTime;
        if (y <= 20) {
            y = 20;
            isOnGround = true;
            velocityY = 0;
        }

        healthBar.updatePosition(x + currentFrame.getRegionWidth() / 2, y + currentFrame.getRegionHeight());
        updateHealthBar();
    }
    public float getX() {return x;}
    public float getY() {return y;}
    public void setY(float y) {this.y = y;}
    public void setX(float x) {this.x = x;}
    public void setVelocityY(float velocityY) {this.velocityY = velocityY;}
    public void setOnGround(boolean isOnGround) {this.isOnGround = isOnGround;}
    public void setHitboxSize(float width, float height) {
        this.hitboxWidth = width;
        this.hitboxHeight = height;
    }
    public float getHitboxWidth() {return hitboxWidth;}
    public float getHitboxHeight() {return hitboxHeight;}
    public float getHitboxX() {return x + (herowidth - hitboxWidth) / 2;}
    public float getHitboxY() {return y;}
    public float getVelocityY() {return velocityY;}
    public float getGravity() {return gravity;}

    public float getVelocityX() {
        return velocityX;
    }
    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }
    public int getCurrentHealth() {return currentHealth;}
}
