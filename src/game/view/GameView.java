package game.view;

import de.pof.textures.TextureHandler;
import de.pof.window.View;
import de.pof.window.Window;
import de.pof.window.listener.Controller;
import de.pof.window.listener.KeyInputListener;
import game.Game;
import game.entity.Camera;
import game.map.GameMap;
import game.entity.Player;

import java.util.Map;
import java.util.HashMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class GameView extends View implements Controller {

	private Game game;

	private GameMap map;
	private Player player;
	private Camera cam;

	private long lastUpdate;
	private long lastEnergyUp;

	private KeyInputListener keys;

	private Map<String, Integer> items;

	public GameView(Game game) {
		this.game = game;

		keys = new KeyInputListener(this);
		game.getWindow().addKeyInputListener(keys);

		items = new HashMap<>();
		items.put("key", 0);
		items.put("energy", 100);
		map = new GameMap(this, items, "Level01");
		player = new Player(360, 256);
		cam = new Camera();
	}

	@Override
	public void init(Window window) {
		TextureHandler.loadImagePng("kachel", "kachel");
		TextureHandler.loadImagePng("table", "table");
		TextureHandler.loadImagePng("plant", "blume");
		TextureHandler.loadImagePng("herd", "herd");
		TextureHandler.loadImagePng("energy", "energy");
		TextureHandler.loadImagePng("key", "key");

		TextureHandler.loadImagePngSpriteSheet("sink", "sink");
		TextureHandler.loadImagePngSpriteSheet("door", "door");
		TextureHandler.loadImagePngSpriteSheet("overlay", "overlay");
		TextureHandler.loadImagePngSpriteSheet("comode", "comode");
		TextureHandler.loadImagePngSpriteSheet("couche", "couche");
		TextureHandler.loadImagePngSpriteSheet("tv", "tv");
		TextureHandler.loadImagePngSpriteSheet("wc", "wc");
		TextureHandler.loadImagePngSpriteSheet("bed", "bed");

		new Thread(() -> {
			lastUpdate = System.currentTimeMillis();
			lastEnergyUp = System.currentTimeMillis();
			while(running) {
				long delta = System.currentTimeMillis() - lastUpdate;
				lastUpdate = System.currentTimeMillis();

				if(System.currentTimeMillis()-lastEnergyUp >= 1000) {
					lastEnergyUp = System.currentTimeMillis();
					items.put("energy", Math.max(items.get("energy")-1, 0));
				}

				float mx = 0, my = 0;
				if (keys.isPressed(KeyEvent.VK_D)) mx += 1;
				if (keys.isPressed(KeyEvent.VK_A)) mx -= 1;
				if (keys.isPressed(KeyEvent.VK_W)) my -= 1;
				if (keys.isPressed(KeyEvent.VK_S)) my += 1;

				player.updateWalkingDirection(mx, my);
				player.update(map, delta);

				cam.setPosition((96 * Game.SIZE -player.getX() - player.getDirection().box.x * Game.SIZE - player.getDirection().box.width/2 * Game.SIZE),54 * Game.SIZE -player.getY() - player.getDirection().box.y * Game.SIZE - player.getDirection().box.height/2 * Game.SIZE);
				cam.update();
				draw();
			}
		}).start();
	}

	@Override
	public void draw() {
		JPanel panel = game.getWindow().getPanel();
		BufferedImage buffer = new BufferedImage(map.width() * 32 * Game.SIZE/2, map.height() * 32 * Game.SIZE/2, BufferedImage.TYPE_INT_ARGB);
		BufferedImage buffer2 = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics g = buffer.getGraphics();
		Graphics g2 = buffer2.getGraphics();

		g.setColor(new Color(0x23213d));
		g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

		map.draw(g);
		g.drawImage(player.getSprite(), (int) Math.floor(player.getX()), (int) Math.floor(player.getY()), 32 * Game.SIZE, 32 * Game.SIZE, null);

		g2.drawImage(buffer, (int)cam.x, (int)cam.y, null);
		if(items.get("energy") > 0) g2.drawImage(TextureHandler.getImagePng("overlay_" + player.getDirection().toString().toLowerCase()), 0, 0, panel.getWidth(), panel.getHeight(), null);
		else {g2.setColor(new Color(0x23213d)); g2.fillRect(0, 0, buffer2.getWidth(), buffer2.getHeight());}
		g2.drawImage(TextureHandler.getImagePng("key"), 5, 5, null);
		g2.drawImage(TextureHandler.getImagePng("energy"), 5, 32 + 10, null);
		g2.setColor(Color.white);
		g2.drawString(items.get("energy") + "", 10 + 32, 10 + 64);
		g2.drawString(items.get("key") + "", 10 + 32, 5 + 32);

		panel.getGraphics().drawImage(buffer2, 0, 0, null);
	}

	public void setMap(GameMap map) {
		this.map = map;
	}

	@Override
	public void onKeyType(int i) {
		if(KeyEvent.VK_E == i) {
			map.playerInteract(player.getX(), player.getY());
		}
	}

	public void errorShake() {
		cam.addScreenshake(7.5f);
	}

	@Override
	public void onMouseWheel(double v) {

	}

	@Override
	public void onMouseDrag(int i, int i1) {

	}

	@Override
	public void onMouseClick(int i, int i1) {

	}

	public Map<String, Integer> getItems() {
		return items;
	}

	public Game getGame() {
		return game;
	}
}