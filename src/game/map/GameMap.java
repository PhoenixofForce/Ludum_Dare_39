package game.map;

import de.pof.gamelib.math.Vec2d;
import de.pof.textures.TextureHandler;
import game.Game;
import game.entity.Player;
import game.view.GameView;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;


public class GameMap {

	private GameView view;

	private Map<Vec2d, String> interactables;
	private Map<Vec2d, TileAction> actionTiles;
	private Map<String, Integer> items;
	private Map<String, Object> values;

	private int[][] map;

	public GameMap(GameView gameView, Map<String, Integer> items, String mapName) {

		this.view = gameView;

		interactables = new HashMap<>();
		actionTiles = new HashMap<>();
		values = new HashMap<>();
		this.items = items;

		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResource("res/maps/"+mapName + ".map").openStream()));

			String line = r.readLine();
			while(!line.startsWith("[map")) {
				if(line.startsWith("[create_val")){
					String[] args = line.split(";");
					if(args[1].trim().equals("bool")) values.put("bool_" + args[2].trim(), args[3].trim().equals("true"));
					else if(args[1].trim().equals("int")) values.put("int_" + args[2].trim(), Integer.parseInt(args[3].trim()));
					else if(args[1].trim().equals("item")) values.put(args[2].trim(), Integer.parseInt(args[3].trim()));
				}

				else if(line.startsWith("[create_interactable")) {
					String[] args = line.split(";");
					Vec2d pos = new Vec2d(Double.parseDouble(args[1]), Double.parseDouble(args[2]));
					String todo = "";
					for(int i = 3; i < args.length; i++) todo += args[i];
					interactables.put(pos, todo);
				}

				line = r.readLine();
			}

			int width = Integer.parseInt(line.split(";")[1].trim());
			int height = Integer.parseInt(line.split(";")[2].trim());

			map =  new int[width][height];
			actionTiles = new HashMap<>();

			for(int y = 0; y < height; y++){
				line = r.readLine();

				for(int x = 0; x < width; x++){
					String tile = line.split(", ")[x];

					if(tile.contains("[")){
						int tileID = Integer.parseInt(tile.split(Pattern.quote("["))[0]);

						for(int j = 1; j < tile.split(Pattern.quote("[")).length; j++){
							String tileInformation = tile.split(Pattern.quote("["))[j];
							String[] filteredInformation = tileInformation.split(Pattern.quote("{"));
							String[] actionInfo = filteredInformation[0].split("; ");

							String actionDetails = "";
							for(int k = 1; k < tileInformation.split(Pattern.quote("{")).length; k++)
								actionDetails += "{" + tileInformation.split(Pattern.quote("{"))[k];

							if(actionInfo[0].equals("warp")){

								WarpAction wa = new WarpAction(gameView, actionInfo[1], Integer.parseInt(actionInfo[2]), Integer.parseInt(actionInfo[3]));

								if(actionDetails != ""){
									for(String s: actionDetails.split(Pattern.quote("{"))){
										String[] detailParts = s.split("; ");
										if(detailParts[0].equals("need")){

											String key = "";
											for(String val: values.keySet()) if(val.endsWith(detailParts[1].trim())) key = val;

											if(key.startsWith("bool")) wa.addNeed(key, detailParts[2].equals("true"));
											else if(key.startsWith("int")) wa.addNeed(key, Integer.parseInt(detailParts[2]));
											else wa.addNeed(key, Integer.parseInt(detailParts[2]));
										}
									}
								}

								if(actionTiles.containsKey(new Vec2d(x, y)));
								else{
									actionTiles.put(new Vec2d(x, y), wa);
								}
							}
						}

						map[x][y] = tileID;
					}

					else{
						map[x][y] = Integer.parseInt(tile);
					}
				}
			}

			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void draw(Graphics g) {
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[0].length; y++) {
				int tile = map[x][y];

				if(tile == 0) continue;

				g.drawImage(TextureHandler.getImagePng("kachel"), x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				//SINKS
					 if(tile == 2) g.drawImage(TextureHandler.getImagePng("sink_empty_r"),x * 32 * Game.SIZE/2 + 16 * Game.SIZE/2, y * 32 * Game.SIZE/2, 16 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				else if(tile == 3) g.drawImage(TextureHandler.getImagePng("sink_full_r"),x * 32 * Game.SIZE/2 + 16 * Game.SIZE/2, y * 32 * Game.SIZE/2, 16 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				else if(tile == 4) g.drawImage(TextureHandler.getImagePng("sink_empty_l"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 16 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				else if(tile == 5) g.drawImage(TextureHandler.getImagePng("sink_full_l"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 16 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				else if(tile == 6) g.drawImage(TextureHandler.getImagePng("sink_empty_d"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2 + 16 * Game.SIZE/2, 32 * Game.SIZE/2, 16 * Game.SIZE/2, null);
				else if(tile == 7) g.drawImage(TextureHandler.getImagePng("sink_full_d"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2 + 16 * Game.SIZE/2, 32 * Game.SIZE/2, 16 * Game.SIZE/2, null);

				else if(tile == 8) g.drawImage(TextureHandler.getImagePng("sink_empty_u"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 16 * Game.SIZE/2, null);
				else if(tile == 9) g.drawImage(TextureHandler.getImagePng("sink_full_u"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 16 * Game.SIZE/2, null);

				//SINGLES
				else if(tile == 10) g.drawImage(TextureHandler.getImagePng("table"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				else if(tile == 15) g.drawImage(TextureHandler.getImagePng("plant"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				else if(tile == 38) g.drawImage(TextureHandler.getImagePng("herd"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				//DOORS
				 else if(tile == 11) g.drawImage(TextureHandler.getImagePng((boolean) values.get("bool_door" + x  + ""+ y)? "door_ro": "door_h"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 12) g.drawImage(TextureHandler.getImagePng((boolean) values.get("bool_door" + x  + ""+ y)? "door_lo": "door_h"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 13) g.drawImage(TextureHandler.getImagePng((boolean) values.get("bool_door" + x  + ""+ y)? "door_do": "door_v"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 14) g.drawImage(TextureHandler.getImagePng((boolean) values.get("bool_door" + x  + ""+ y)? "door_uo": "door_v"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				  //COMODES
				 else if(tile == 16) g.drawImage(TextureHandler.getImagePng("comode_l"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 17) g.drawImage(TextureHandler.getImagePng("comode_u"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 18) g.drawImage(TextureHandler.getImagePng("comode_r"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 19) g.drawImage(TextureHandler.getImagePng("comode_d"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				 //COUCHES
				 else if(tile == 20) g.drawImage(TextureHandler.getImagePng("couche_left"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 21) g.drawImage(TextureHandler.getImagePng("couche_left_t"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 22) g.drawImage(TextureHandler.getImagePng("couche_left_m"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 23) g.drawImage(TextureHandler.getImagePng("couche_left_b"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				 else if(tile == 24) g.drawImage(TextureHandler.getImagePng("couche_right"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 25) g.drawImage(TextureHandler.getImagePng("couche_right_t"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 26) g.drawImage(TextureHandler.getImagePng("couche_right_m"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 27) g.drawImage(TextureHandler.getImagePng("couche_right_b"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				 else if(tile == 28) g.drawImage(TextureHandler.getImagePng("couche_top"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 29) g.drawImage(TextureHandler.getImagePng("couche_top_l"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 30) g.drawImage(TextureHandler.getImagePng("couche_top_m"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 31) g.drawImage(TextureHandler.getImagePng("couche_top_r"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				 else if(tile == 32) g.drawImage(TextureHandler.getImagePng("couche_bottom"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 33) g.drawImage(TextureHandler.getImagePng("couche_bottom_l"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 34) g.drawImage(TextureHandler.getImagePng("couche_bottom_m"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 35) g.drawImage(TextureHandler.getImagePng("couche_bottom_r"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				 //TV
				 else if(tile == 36) g.drawImage(TextureHandler.getImagePng("tv_right_t"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 37) g.drawImage(TextureHandler.getImagePng("tv_right_b"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				 //BED
				 else if(tile == 39) g.drawImage(TextureHandler.getImagePng("bed_h_t"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 40) g.drawImage(TextureHandler.getImagePng("bed_h_b"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);

				 //WC
				 else if(tile == 41) g.drawImage(TextureHandler.getImagePng("wc_l"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 42) g.drawImage(TextureHandler.getImagePng("wc_r"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 43) g.drawImage(TextureHandler.getImagePng("wc_u"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
				 else if(tile == 44) g.drawImage(TextureHandler.getImagePng("wc_d"),x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2, null);
			}
		}
	}

	public List<Rectangle> getBoxes() {
		List<Rectangle> boxes = new ArrayList<>();

		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[0].length; y++) {
				int tile = map[x][y];

				Rectangle r = null;
				if(tile == 0 || tile == 15 || tile == 0 || (tile <= 37 && tile >= 16) || (tile <= 44 && tile >= 41)) r = new Rectangle(x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2);

				else if(tile == 2) r = new Rectangle(x * 32 * Game.SIZE/2 + 16 * Game.SIZE/2, y * 32 * Game.SIZE/2, 16 * Game.SIZE/2, 32 * Game.SIZE/2);
				else if(tile == 3) r = new Rectangle(x * 32 * Game.SIZE/2 + 16 * Game.SIZE/2, y * 32 * Game.SIZE/2, 16 * Game.SIZE/2, 32 * Game.SIZE/2);

				else if(tile == 4) r = new Rectangle(x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 16 * Game.SIZE/2, 32 * Game.SIZE/2);
				else if(tile == 5) r = new Rectangle(x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 16 * Game.SIZE/2, 32 * Game.SIZE/2);

				else if(tile == 6) r = new Rectangle(x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2 + 16 * Game.SIZE/2, 32 * Game.SIZE/2, 16 * Game.SIZE/2);
				else if(tile == 7) r = new Rectangle(x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2 + 16 * Game.SIZE/2, 32 * Game.SIZE/2, 16 * Game.SIZE/2);

				else if(tile == 8) r = new Rectangle(x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 16 * Game.SIZE/2);
				else if(tile == 9) r = new Rectangle(x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 16 * Game.SIZE/2);

				else if((tile == 11 || tile == 12 || tile == 14|| tile == 13) && !(boolean)(values.get("bool_door" + x + "" + y))) r = new Rectangle(x * 32 * Game.SIZE/2, y * 32 * Game.SIZE/2, 32 * Game.SIZE/2, 32 * Game.SIZE/2);
				if(r != null) boxes.add(r);
			}
		}

		return boxes;
	}

	public void playerStep(Player p, float x, float y) {
		for(Vec2d v: actionTiles.keySet()) {
			if(v.clone().multi(Game.SIZE/2 * 32).distanceTo(new Vec2d(x, y)) <= 32 * Game.SIZE/2) {
				Map<String, Object>  ab = actionTiles.get(v).getNeeds();
				if(ab.size() > 0) {
					for(String s: ab.keySet()) {
						System.out.println("________________" + s);
						if(values.containsKey(s) && ((s.startsWith("bool") && (boolean)values.get(s) != (boolean)ab.get(s)) || (s.startsWith("int") && (int)values.get(s) != (int)ab.get(s))) || (items.containsKey(s) && items.get(s) != (int)ab.get(s))) {
							//view.errorShake();
							return;
						}
					}
				}

				actionTiles.get(v).onStep(p);
			}
		}
	}

	public void playerInteract(float x, float y) {

		for(Vec2d v: interactables.keySet()) {
			double distance = v.clone().multi(32 * Game.SIZE/2).distanceTo(new Vec2d(x, y));
			double range = 32 * Game.SIZE;
			if(distance <= range) {
				String todo = interactables.get(v);
				String[] parts = todo.split(Pattern.quote("["));

				boolean canInteract = true;
				for(String s: parts) {
					String[] cont = s.trim().split(" ");
					if(cont.length > 0 && cont[0].trim().startsWith("need")) {

						boolean need = false;

						String key = "";
						for(String val: values.keySet()) if(val.endsWith(cont[1].trim())) key = val;
						if(key.startsWith("bool")) {
							boolean a = cont[2].trim().equals("true");

							if(a == (boolean) values.get(key)) need = true;
						} else if (key.startsWith("int")) {
							int a = Integer.parseInt(cont[2]);
							if (a <= (int) values.get(key)) need = true;
						} else {
							int a = Integer.parseInt(cont[2]);
							if (a <= items.get(key)) need = true;
						}
						if(!need) {
							canInteract = false;
							break;
						}
					}
				}

				if(canInteract) {
					for(String s: parts) {
						String[] cont = s.trim().split(" ");
						if(cont.length > 0 && cont[0].trim().startsWith("set_val")) {

							String key = "";
							for(String val: values.keySet()) if(val.endsWith(cont[1].trim())) key = val;
							if(key.startsWith("bool")) values.put(key, cont[2].trim().equals("true"));
							else if(key.startsWith("int")) values.put(key, Integer.parseInt(cont[2]));
							else items.put(cont[1], Integer.parseInt(cont[2]));

							System.out.println(key + " " + values.get(key));
						}else if(cont[0].trim().startsWith("add")) {

							String key = "int_" + cont[1].trim();
							if(values.containsKey(key)) {
								int old = (int)values.get(key);
								values.put(key, old + Integer.parseInt(cont[2]));
							} else {
								int old = items.get(cont[1].trim());
								items.put(cont[1].trim(), old + Integer.parseInt(cont[2]));
							}
							System.out.println(key + " " + values.get(key));
						}
					}
				}else {
					view.errorShake();
				}
				return;
			}
		}

		view.errorShake();
	}

	public int width() {
		return map.length;
	}

	public int height() {
		return map[0].length;
	}

}
