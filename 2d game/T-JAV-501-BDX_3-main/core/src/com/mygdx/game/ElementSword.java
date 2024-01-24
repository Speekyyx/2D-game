package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class ElementSword extends ApplicationAdapter {
	private MovingPlatform movingPlatform;
	private SpriteBatch batch;
	private Background background;
	private ArrayList<Platform> platforms;
	private Texture platformTexture;
	private Texture blocTexture;
	private Texture platformfinal;
	private Hero hero;
	private Texture heroTexture;
	private Texture healthBarTexture;
	private Texture monsterTexture;
	private Texture healthBarMonsterTexture;
	private Texture healthBarMeleeTexture;
	private ArrayList<Monster> monsters; // Correction ici
	private boolean debugMode = false;
	private ShapeRenderer shapeRenderer;
	private static final float COLLISION_THRESHOLD = 175;
	private BitmapFont font;
	private boolean gameOver = false;
	private Texture projectileTexture;
	private coin coin;
	private boolean coinCollected = false;
	private MeleeMonster meleeMonster;
	Music zeldaSong;
	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(3);
		shapeRenderer = new ShapeRenderer();
		background = new Background();
		monsterTexture = new Texture("monster.png");
		healthBarTexture = new Texture("healthBar.png");
		healthBarMonsterTexture = new Texture("EnemyBar.png");
		healthBarMeleeTexture = new Texture("HealthBarMelee.png");
		projectileTexture = new Texture("projectile.png");
		zeldaSong = Gdx.audio.newMusic(Gdx.files.internal("zelda_song.mp3"));
		coin = new coin(1170, 470,50, 50);
		hero = new Hero(healthBarTexture);
		monsters = new ArrayList<>();
		monsters.add(new Monster(monsterTexture, healthBarMonsterTexture, projectileTexture));
		Texture meleeMonsterTexture = new Texture("FrostGuardian.png");
		meleeMonster = new MeleeMonster(meleeMonsterTexture, healthBarMonsterTexture, hero);

		platformTexture = new Texture("platform.png");
		platformfinal = new Texture("platformfinal.png");
		blocTexture = new Texture("bloc.png");
		platforms = new ArrayList<>();
		Platform newPlatform = new Platform(platformfinal);
		newPlatform.setPosition(1100, 350);
		newPlatform.setSize(200, 25);
		platforms.add(newPlatform);
		Platform platform3 = new Platform(blocTexture);
		platform3.setPosition(650, 20);
		platform3.setSize(100, 100); // Correction ici
		platforms.add(platform3);

		Platform platform1 = new Platform(platformTexture);
		platform1.setPosition(305, 250);
		platform1.setSize(200, 25); // Correction ici
		platforms.add(platform1);

		movingPlatform = new MovingPlatform(platformTexture, 900, 300, 140, 25, 1000, 100);
	}


	private  void handleInput(float deltaTime) {
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			hero.moveLeft(deltaTime);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			System.out.println("Right");
			hero.moveRight(deltaTime);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			System.out.println("Jump");
			hero.jump(deltaTime);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
			System.out.println("D");
			toggleDebugMode();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.A)){
			System.out.println("Attacking key pressed");
			hero.attack(monsters);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			System.out.println("Enter");
		}
	}
	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		ScreenUtils.clear(1, 0, 0, 1);

		batch.begin();
		background.render(batch);
		coin.update(deltaTime);
		coin.draw(batch);
		if (!coinCollected) {
			coin.update(deltaTime);
			coin.draw(batch);
			if (checkHeroCoinCollision(hero, coin)) {
				coinCollected = true;
			}
		}
		zeldaSong.play();
		zeldaSong.setVolume(0.15f);
		handleInput(deltaTime);
		if (gameOver) {
			font.draw(batch, "Game Over",525, 400);
		} else {
			hero.update(deltaTime);
			hero.draw(batch, shapeRenderer, debugMode);

			for (Platform platform : platforms) {
				platform.render(batch);
			}

			for (Monster monster : monsters) {
				if (!monster.isDead()) {
					monster.update(deltaTime);
					monster.draw(batch);

					Projectile hitProjectile = hero.isHitByProjectile(monster.getProjectiles());
					if (hitProjectile != null) {
						hero.decreaseHealth(hitProjectile.getDamage());
						monster.removeProjectile(hitProjectile); // Supprimer le projectile touché
						handleHeroProjectileCollision(hero, hitProjectile);
					}
				}
			}

			movingPlatform.update(deltaTime);
			movingPlatform.render(batch);

			meleeMonster.update(deltaTime);
			meleeMonster.draw(batch);
			checkCollisions();
		}
		if (coinCollected) {
			font.draw(batch, "You win", 525, 400);
		}
		batch.end();

		if (debugMode) {
			drawDebug();
		}
	}

	private void drawDebug() {
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(1, 0, 0, 0.5f);
		for (Platform platform : platforms) {
			shapeRenderer.rect(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight());
		}
		shapeRenderer.end();
	}
	private void checkCollisions() {
		boolean isHeroOnPlatform = false;
		for (Platform platform : platforms) {
			if (isColliding(hero, platform)) {
				handleHeroPlatformCollision(hero, platform);
				isHeroOnPlatform = true;

			} else if (isCollidingHorizontally(hero, platform)) {
				handleHorizontalCollision(hero, platform);
			}
		}
		if (isColliding(hero, movingPlatform)) {
			handleHeroPlatformCollision(hero, movingPlatform);
			isHeroOnPlatform = true;
		} else if (isCollidingHorizontally(hero, movingPlatform)) {
			handleHorizontalCollision(hero, movingPlatform);
		}
		if (!isHeroOnPlatform && hero.getY() >20) { // GROUND_LEVEL est la position Y du sol
			hero.setOnGround(false);
		} else {
			hero.setOnGround(isHeroOnPlatform || hero.getY() <=20);
		}
		for (Monster monster : monsters) {
			if (isCollidingWithMonster(hero, monster)) {
				handleHeroMonsterCollision(hero, monster);
			}
		}
		if (meleeMonster.isCollidingWithHero(hero)) {
			handleHeroMeleeMonsterCollision(hero, meleeMonster);
			meleeMonster.attack();
		}
	}
	private void handleHeroMeleeMonsterCollision(Hero hero, MeleeMonster meleeMonster) {
		meleeMonster.attack();
		hero.decreaseHealth(0);
		if (hero.getCurrentHealth() <= 0) {
			gameOver = true;
		}
	}
	private void handleHorizontalCollision(Hero hero, Platform platform) {
		hero.setVelocityX(0);
	}
	private boolean isCollidingHorizontally(Hero hero, Platform platform) {
		float heroRight = hero.getHitboxX() + hero.getHitboxWidth();
		float heroLeft = hero.getHitboxX();
		float heroTop = hero.getHitboxY() + hero.getHitboxHeight();
		float heroBottom = hero.getHitboxY();

		float platformLeft = platform.getX();
		float platformRight = platform.getX() + platform.getWidth();
		float platformTop = platform.getY() + platform.getHeight();
		float platformBottom = platform.getY();


		if (heroRight > platformLeft && heroRight < platformRight &&
				heroBottom < platformTop && heroTop > platformBottom) {
			System.out.println("Collision avec le côté gauche de la plateforme");
			return true;
		}

		// Vérifier la collision avec le côté droit de la plateforme
		if (heroLeft < platformRight && heroLeft > platformLeft &&
				heroBottom < platformTop && heroTop > platformBottom) {
			System.out.println("Collision avec le côté droit de la plateforme");
			return true;
		}

		return false;
	}
	private boolean isAbovePlatform(Hero hero, Platform platform) {
		float heroLeft = hero.getHitboxX();
		float heroRight = heroLeft + hero.getHitboxWidth();

		float platformLeft = platform.getX();
		float platformRight = platformLeft + platform.getWidth();

		return heroRight > platformLeft && heroLeft < platformRight;
	}

	private boolean isCollidingWithMonster(Hero hero, Monster monster) {
		float heroLeftMonster = hero.getHitboxX();
		float heroRightMonster = heroLeftMonster + hero.getHitboxWidth();
		float heroTopMonster = hero.getHitboxY() + hero.getHitboxHeight();
		float heroBottomMonster= hero.getHitboxY();

		float monsterLeft = monster.getX();
		float monsterRight = monsterLeft + monster.getWidth();
		float monsterTop = monster.getY() + monster.getHeight();
		float monsterBottom = monster.getY();

		return heroLeftMonster < monsterRight && heroRightMonster > monsterLeft &&
				heroTopMonster> monsterBottom && heroBottomMonster < monsterTop;
	}
	private boolean isColliding(Hero hero, Platform platform) {
		float heroLeft = hero.getHitboxX();
		float heroRight = heroLeft + hero.getHitboxWidth();
		float heroTop = hero.getHitboxY() + hero.getHitboxHeight();
		float heroBottom = hero.getHitboxY();
		float platformLeft = platform.getX();
		float platformRight = platformLeft + platform.getWidth();
		float platformTop = platform.getY() + platform.getHeight();
		float platformBottom = platform.getY();


		float tolerance = 5f; // Une petite tolérance pour éviter les tremblements
		return heroLeft < platformRight && heroRight > platformLeft &&
				heroTop > platformBottom && heroBottom < platformTop + tolerance;
	}
	private void handleHeroMonsterCollision(Hero hero, Monster monster) {
		hero.decreaseHealth(0);
		if (hero.getCurrentHealth() <= 0) {
			gameOver = true;
		}
	}
	private void handleHeroProjectileCollision(Hero hero, Projectile projectile) {
		hero.decreaseHealth(1);
		if (hero.getCurrentHealth() <= 0) {
			gameOver = true;
		}
	}
	private void handleHeroPlatformCollision(Hero hero, Platform platform) {
		hero.setY(platform.getY() + platform.getHeight());
		hero.setOnGround(true);
		hero.setVelocityY(0);
	}
	private boolean checkHeroCoinCollision(Hero hero, coin coin) {
		float heroLeft = hero.getHitboxX();
		float heroRight = hero.getHitboxX() + hero.getHitboxWidth();
		float heroTop = hero.getHitboxY() + hero.getHitboxHeight();
		float heroBottom = hero.getHitboxY();

		float coinLeft = coin.getX();
		float coinRight = coin.getX() + coin.getWidth();
		float coinTop = coin.getY() + coin.getHeight();
		float coinBottom = coin.getY();

		return heroRight > coinLeft && heroLeft < coinRight && heroTop > coinBottom && heroBottom < coinTop;
	}
	@Override
	public void dispose() {
		if (batch != null) batch.dispose();
		if (background != null) background.dispose();
		if (platformTexture != null) platformTexture.dispose();
		if (blocTexture != null) blocTexture.dispose();
		if (hero != null) hero.dispose();
		for (Monster monster : monsters) {
			monster.dispose();
		}
		if (heroTexture != null) heroTexture.dispose();
		if (healthBarTexture != null) healthBarTexture.dispose();
		if (monsterTexture != null) monsterTexture.dispose();
		if (healthBarMonsterTexture != null) healthBarMonsterTexture.dispose();
		if(shapeRenderer != null) shapeRenderer.dispose();
		if (font != null) font.dispose();
		if (projectileTexture != null) projectileTexture.dispose();
		if (coin != null) coin.dispose();
	}
	public void toggleDebugMode() {
		debugMode = !debugMode;
	}
}
