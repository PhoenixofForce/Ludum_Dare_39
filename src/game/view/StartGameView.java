package game.view;

import de.pof.textures.TextureHandler;
import de.pof.window.View;
import de.pof.window.Window;
import de.pof.window.listener.Controller;
import de.pof.window.listener.KeyInputListener;
import game.Game;

public class StartGameView extends View implements Controller{

	private Game game;
	private KeyInputListener ml;

	public StartGameView(Game game) {
		this.game = game;
		ml = new KeyInputListener(this);
		game.getWindow().addKeyInputListener(ml);
	}

	@Override
	public void init(Window window) {
		TextureHandler.loadImagePng("start", "start");

		new Thread(() ->{
			while(running) draw();
		}).start();
	}

	@Override
	public void draw() {
		game.getWindow().getPanel().getGraphics().drawImage(TextureHandler.getImagePng("start"), 0, 0, game.getWindow().getPanel().getWidth(), game.getWindow().getPanel().getHeight(), null);
	}

	@Override
	public void onKeyType(int i) {
		if(running) game.getWindow().updateView(new LightsOutView(game));
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
}
