package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MeleeMonster {
    enum State {
        IDLE, WALKING, ATTACKING
    }

    private State currentState;
    private State previousState;
    private Animation<TextureRegion> monsterIdle;
    private Animation<TextureRegion> monsterWalking;
    private Animation<TextureRegion> monsterAttacking;
    private float stateTimer;
    private HealthBar healthBar;
    private Texture monsterSheet;
    private float x, y;
    private int maxHealth = 200;
    private int currentHealth = maxHealth;
    private boolean isDead = false;
    int frameWidth = 192;
    int frameHeight = 128;
    private float detectionRange = 500;
    private Hero hero;
    private float healthBarOffsetY = 20;
    private float attackRange = 100; // La portée à laquelle le monstre peut attaquer
    private int attackDamage = 20; // Les dégâts infligés par une attaque
    private float attackTimer = 0; // Un timer pour contrôler la fréquence des attaques
    private float attackCooldown = 1.5f; // Temps de recharge entre les attaques en secondes

    public MeleeMonster(Texture monsterSheetTexture, Texture healthBarMeleeTexture, Hero hero) {
        this.monsterSheet = monsterSheetTexture;
        this.healthBar = new HealthBar(healthBarMeleeTexture);
        healthBar = new HealthBar(healthBarMeleeTexture);
        healthBar.setSize(100, 10);
        healthBar.setOffset(0, frameHeight + healthBarOffsetY);
        this.hero = hero;
        this.x = 900;
        this.y = 20;
        initializeAnimations();
    }

    private void initializeAnimations() {
        TextureRegion[][] idleFrames = TextureRegion.split(monsterSheet, frameWidth, frameHeight);
        int numIdleFrames = 6;
        TextureRegion[] monsterIdleFrames = new TextureRegion[numIdleFrames];
        for (int i = 0; i < numIdleFrames; i++) {
            monsterIdleFrames[i] = idleFrames[0][i];
        }
        monsterIdle = new Animation<TextureRegion>(0.1f, monsterIdleFrames);

        TextureRegion[][] walkFrames = TextureRegion.split(monsterSheet, frameWidth, frameHeight);
        int numWalkFrames = 10;
        TextureRegion[] monsterwalkFrames = new TextureRegion[numWalkFrames];
        for (int i = 0; i < numWalkFrames; i++) {
            monsterwalkFrames[i] = walkFrames[1][i];
        }
        monsterWalking = new Animation<TextureRegion>(0.1f, monsterwalkFrames);
        TextureRegion[][] AttackFrames = TextureRegion.split(monsterSheet, frameWidth, frameHeight);
        int numAttackFrames = 14;
        TextureRegion[] monsterAttackFrames = new TextureRegion[numAttackFrames];
        for (int i = 0; i < numAttackFrames; i++) {
            monsterAttackFrames[i] = AttackFrames[2][i];
        }
        monsterAttacking = new Animation<TextureRegion>(0.1f, monsterAttackFrames);
        healthBar.setSize(100, 10); // Taille initiale
        healthBar.setOffset(0, frameHeight); // Position au-dessus du monstre
        currentState = State.IDLE;
        previousState = State.IDLE;
        stateTimer = 0;
    }

    private void updateHealthBar() {
        float healthPercentage = (float) currentHealth / maxHealth;
        healthBar.setSize(healthPercentage * 100, 10); // Mise à jour de la largeur de la barre

        // Changer la couleur en fonction de la santé restante
        if (healthPercentage > 0.5f) {
            healthBar.setColor(0, 1, 0, 1); // Vert
        } else if (healthPercentage > 0.2f) {
            healthBar.setColor(1, 1, 0, 1); // Jaune
        } else {
            healthBar.setColor(1, 0, 0, 1); // Rouge
        }
    }

    public void update(float deltaTime) {
        stateTimer += currentState == previousState ? deltaTime : 0;
        previousState = currentState;
        float distanceToHero = Math.abs(hero.getX() - x);
        float healthBarX = x + 50;
        float healthBarY = y + frameHeight - 120;

        // Gestion de l'attaque
        if (attackTimer > 0) {
            attackTimer -= deltaTime;
        }

        if (distanceToHero <= attackRange && !isDead) {
            if (attackTimer <= 0) {
                currentState = State.ATTACKING;
                attack();
                attackTimer = attackCooldown;
            }
        } else if (distanceToHero <= detectionRange && !isDead) {
            if (hero.getX() < x) {
                x -= 50 * deltaTime;
                currentState = State.WALKING;
            } else if (hero.getX() > x) {
                x += 50 * deltaTime;
                currentState = State.WALKING;
            }
        } else {
            currentState = State.IDLE;
        }

        healthBar.updatePosition(healthBarX, healthBarY);
        updateHealthBar();
    }



    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = getFrame();
        batch.draw(currentFrame, x, y);
        healthBar.draw(batch);
    }

    private TextureRegion getFrame() {
        TextureRegion region;
        switch (currentState) {
            case ATTACKING:
                region = monsterAttacking.getKeyFrame(stateTimer);
                break;
            case WALKING:
                region = monsterWalking.getKeyFrame(stateTimer, true);

                break;
            case IDLE:
            default:
                region = monsterIdle.getKeyFrame(stateTimer, true);
                break;
        }
        return region;
    }

    public boolean isCollidingWithHero(Hero hero) {
        float heroLeft = hero.getHitboxX();
        float heroRight = heroLeft + hero.getHitboxWidth();
        float heroTop = hero.getHitboxY() + hero.getHitboxHeight();
        float heroBottom = hero.getHitboxY();

        float monsterLeft = x;
        float monsterRight = x + frameWidth;
        float monsterTop = y + frameHeight;
        float monsterBottom = y;

        return heroLeft < monsterRight && heroRight > monsterLeft &&
                heroTop > monsterBottom && heroBottom < monsterTop;
    }

    protected void attack() {
        currentState = State.ATTACKING;
        stateTimer = 0;
        hero.decreaseHealth(1);
    }

}
