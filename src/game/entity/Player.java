package game.entity;

import de.pof.textures.Animation;
import de.pof.textures.TextureHandler;
import game.Direction;
import game.Game;
import game.map.GameMap;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {

	public static final float acceleration = 0.001f;
	public static final float reibung = 0.9f;
	public static final float stopping = 0.9f;
	public static final long TIME = 10;

	private float x, y;
	private float vx, vy;
	private float mx, my;

	private Animation animation;
	private Direction direction;
	private boolean walking;

	public Player(float x, float y) {

		this.x = x;
		this.y = y;

		TextureHandler.loadImagePngSpriteSheet("player", "Player");
		direction = Direction.DD;
		walking = false;
		animation = new Animation("player_idle_" + direction.toString().toLowerCase()) {
			@Override
			public void onSpriteChange() {}
		};
	}

	private long lastTime = 0;
	private long add = 0;
	public void update(GameMap m, long time) {
		lastTime += time;

		add += time;
		while(add > 75) {
			add -= 75;
			animation.next();
		}

		while (lastTime > TIME) {
			vx += mx * acceleration;
			vy += my * acceleration;
			vx *= reibung;
			vy *= reibung;
			if (mx == 0 && my == 0) {
				vx *= stopping;
				vy *= stopping;

				walking = false;
				animation.setAnimation("player_idle_" + direction.toString().toLowerCase());
			} else {

				     if(mx > 0 && my == 0 && (direction != Direction.RR || !walking)) {animation.setAnimation("player_move_rr"); direction = Direction.RR;}
				else if(mx < 0 && my == 0 && (direction != Direction.LL|| !walking)) {animation.setAnimation("player_move_ll"); direction = Direction.LL;}
				else if(mx == 0 && my < 0 && (direction != Direction.UU|| !walking)) {animation.setAnimation("player_move_uu"); direction = Direction.UU;}
				else if(mx == 0 && my > 0 && (direction != Direction.DD|| !walking)) {animation.setAnimation("player_move_dd"); direction = Direction.DD;}

				else if(mx > 0 && my > 0 && (direction != Direction.DR|| !walking)) {animation.setAnimation("player_move_dr"); direction = Direction.DR;}
				else if(mx < 0 && my < 0 && (direction != Direction.UL|| !walking)) {animation.setAnimation("player_move_ul"); direction = Direction.UL;}
				else if(mx > 0 && my < 0 && (direction != Direction.UR|| !walking)) {animation.setAnimation("player_move_ur"); direction = Direction.UR;}
				else if(mx < 0 && my > 0 && (direction != Direction.DL|| !walking)) {animation.setAnimation("player_move_dl"); direction = Direction.DL;}

				walking = true;
			}

			lastTime -= TIME;

			if(collides(m, x + vx * 90, y + vy * 90)) {
				vy = 0;
				vx = 0;
			}

			x += vx * 90;
			y += vy * 90;

			if(mx != 0 || my != 0) m.playerStep(this, x, y);
		}
	}

	private boolean collides(GameMap map, float x, float y) {
		Rectangle player = new Rectangle((int) Math.floor(x) + direction.box.x * Game.SIZE, (int) Math.floor(y) + direction.box.y * Game.SIZE, direction.box.width * Game.SIZE, direction.box.height * Game.SIZE);
		for(Rectangle r: map.getBoxes()) {
			if(r.intersects(player)) {
				return true;
			}
 		}

 		return false;
	}

	public BufferedImage getSprite() {
		return animation.getCurrentSprite();
	}

	public void updateWalkingDirection(float mx, float my) {
		this.mx = mx;
		this.my = my;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Direction getDirection() {
		return direction;
	}
}
