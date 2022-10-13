package game;

import de.pof.window.Window;
import game.view.StartGameView;

import java.awt.*;

public class Game {

	public static final int SIZE = 4;

	private Window w;

	public Game() {
		w = new Window();
		w.setTitle("Lights Out");
		w.setResizable(false);
		w.setMinimumSize(new Dimension(192, 108));
		w.setSize(new Dimension(192 * SIZE + w.getInsets().left + w.getInsets().right, 108 * SIZE + w.getInsets().top +  + w.getInsets().bottom));

		w.updateView(new StartGameView(this));
	}

	public Window getWindow() {
		return w;
	}
}
