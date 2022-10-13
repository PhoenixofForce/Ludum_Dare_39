package game.map;

import de.pof.gamelib.math.Vec2d;
import game.entity.Player;
import game.view.GameView;
import game.view.VictoryView;

import java.util.HashMap;
import java.util.Map;

public class WarpAction implements TileAction{

	private GameView gv;
	private String world;
	private Vec2d warpPoint;

	private Map<String, Object> needs;

	public WarpAction(GameView gv, String world, Vec2d warpPoint){
		this.world = world;
		this.warpPoint = warpPoint;
		this.gv = gv;

		needs = new HashMap<>();
	}

	public WarpAction(GameView gv,String world, int x, int y){
		this.world = world;
		this.gv = gv;
		this.warpPoint = new Vec2d(x, y);

		needs = new HashMap<>();
	}

	@Override
	public void onStep(Player p) {
		if(world.trim().equalsIgnoreCase("victory")) {
			gv.getGame().getWindow().updateView(new VictoryView(gv.getGame()));
			return;
		}
		gv.setMap(new GameMap(gv, gv.getItems(), world));
		p.setPosition((int) warpPoint.x, (int) warpPoint.y);
	}

	@Override
	public void addNeed(String key, Object val) {
		needs.put(key, val);
	}

	public Map<String, Object> getNeeds() {
		return needs;
	}
}
