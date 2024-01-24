package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.Random;

public class Monster {
    private Sprite monster;
    private  HealthBar healthBar;
    private int  maxHealth = 150;
    private int currentHealth = maxHealth;
    private boolean isDead = false;
    private ArrayList<Projectile> projectiles;
    private Texture projectileTexture;
    private Random random;

    public Monster(Texture monsterTexture, Texture healthBarTexture, Texture projectileTexture){
        monster = new Sprite(monsterTexture);
        monster.setPosition(350, 270);
        monster.setSize(75, 100);
        healthBar = new HealthBar(healthBarTexture);
        healthBar.setSize(100, 10);
        healthBar.setOffset(0, monster.getHeight() + 10);
        this.projectileTexture = projectileTexture;
        projectiles = new ArrayList<>();
        random = new Random();
    }
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            isDead = true;
        } else {
            updateHealthBar();
        }
    }
    public void launchProjectile() {
        if (projectiles.size() < 1) { // Limite à un projectile à la fois
            float velocityX = (random.nextBoolean() ? -1 : 1) * random.nextFloat() * 200; // Vitesse aléatoire
            float velocityY = -300; // Vitesse vers le bas
            projectiles.add(new Projectile(projectileTexture, getX(), getY(), velocityX, velocityY));
        }
    }
    public void draw (SpriteBatch batch) {
        if (!isDead) {
            monster.draw(batch);
            healthBar.draw(batch, monster.getX(), monster.getY());
        }
        for (Projectile projectile : projectiles) {
            projectile.draw(batch);
        }
    }
    public void updateHealthBar() {
        float healthPercentage = (float) currentHealth / maxHealth;
        healthBar.setSize(healthPercentage * 100, 10);
        if (healthPercentage > 0.5f) {
            healthBar.setColor(0, 1, 1, 1);
        } else if (healthPercentage > 0.2f) {
            healthBar.setColor(1, 1, 0, 1);
        } else {
            healthBar.setColor(1, 0, 0, 1);
        }
    }
    public void dispose() {
            monster.getTexture().dispose();
        healthBar.dispose();
    }

    public void update(float deltaTime) {
        // Vérifier si le monstre est mort. Si oui, ne pas mettre à jour.
        if (isDead) {
            return; // Sortir de la méthode si le monstre est mort
        }

        // Mettre à jour les projectiles existants
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update(deltaTime);
            if (projectile.isOffScreen()) {
                projectiles.remove(i);
            }
        }

        if (random.nextInt(100) < 2) {
            launchProjectile();
        }

        updateHealthBar();
        healthBar.updatePosition(monster.getX(), monster.getY());
    }
    public float getX() {
        return monster.getX();
    }
    public float getY() {
        return monster.getY();
    }
    public float getWidth() {
        return monster.getWidth();
    }
    public float getHeight() {
        return monster.getHeight();
    }
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }
    public void removeProjectile(Projectile projectile) {
        projectiles.remove(projectile);
    }
    public boolean isDead() {
        return isDead;
    }
}
