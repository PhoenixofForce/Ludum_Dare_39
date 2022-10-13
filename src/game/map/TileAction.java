package game.map;

import game.entity.Player;
import java.util.Map;

public interface TileAction {

	void onStep(Player p);
	void addNeed(String key, Object val);
	Map<String, Object> getNeeds();
}