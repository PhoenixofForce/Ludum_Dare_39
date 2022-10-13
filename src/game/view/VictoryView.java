package game.view;

import de.pof.textures.TextureHandler;
import de.pof.window.View;
import de.pof.window.Window;
import game.Game;

import javax.swing.*;

public class VictoryView extends View {

	private Game game;

	public VictoryView(Game game) {
		this.game = game;
	}

	@Override
	public void init(Window window) {
		TextureHandler.loadImagePng("victory", "victory");
		new Thread(()->{
			while (running) draw();
		}).start();
	}

	@Override
	public void draw() {
		JPanel panel =game.getWindow().getPanel();
		panel.getGraphics().drawImage(TextureHandler.getImagePng("victory"), 0, 0, panel.getWidth(), panel.getHeight(), null);
	}
}
