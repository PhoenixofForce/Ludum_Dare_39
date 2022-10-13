package game.view;

import game.Game;
import de.pof.audio.AudioHandler;
import de.pof.textures.TextureHandler;
import de.pof.window.View;
import de.pof.window.Window;
import game.entity.Camera;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class LightsOutView extends View{

	private Camera c;

	private Game game;
	private int state;

	public LightsOutView(Game g) {
		this.game = g;
		this.c = new Camera();
	}

	@Override
	public void init(Window window) {
		TextureHandler.loadImagePng("skyline1", "Skyline1");
		TextureHandler.loadImagePng("skyline2", "Skyline2");
		TextureHandler.loadImagePng("skyline3", "Skyline3");
		TextureHandler.loadImagePng("skyline4", "Skyline4");

		AudioHandler.loadMusicWav("hit", "hit");

		state = 1;

		long lastUpdate = System.currentTimeMillis();
		while(state <= 4) {
			draw();

			if(System.currentTimeMillis() - lastUpdate >= 1000) {
				c.addScreenshake(25.0f);
				state++;
				lastUpdate = System.currentTimeMillis();
			}
		}

		game.getWindow().updateView(new GameView(game));
	}

	 @Override
	 public void stop() {
		running = false;
		AudioHandler.unloadMusicWav("hit");
	 }

	@Override
	public void draw() {
		BufferedImage draw = TextureHandler.getImagePng("skyline" + state);
		JPanel panel = game.getWindow().getPanel();
		c.update();
		panel.getGraphics().drawImage(draw, (int) c.x, (int )c.y, panel.getWidth(), panel.getHeight(), null);
	}
}
