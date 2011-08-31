package net.jewelofartifice.bladedpenguin.koth.hilltop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;

import net.jewelofartifice.bladedpenguin.koth.Koth;
import net.jewelofartifice.bladedpenguin.koth.team.Team;

public class HilltopManager {
	public static final int NONE = 0;
	public static final int WORLDGUARD = 1;
	private Koth plugin;
	public Set<Hilltop> Hilltops =  new HashSet<Hilltop>();
	public HilltopManager(Koth p){
		plugin = p;
		WGHilltop.initialize(plugin);
		//we're calling loadAll() in onEnable() because of a circular dependancy. to be clear, loadall instantiates hilltops, which relies on plugin.hm, which is a null pointer until this constructor returns. I guess we could pass this as an argument to Hilltop constructors to resolve  
		//loadAll();
		
	}
	public void loadAll() {
		Configuration config = new Configuration(new File (plugin.getDataFolder(),"hilltops.yml"));
		config.load();
		Koth.logger().info("Listing elements in hilltop.yml");
		//find all the Hilltops Hilltops, 
		Set<String> hillnames = new HashSet<String>();
		for (String name: config.getAll().keySet()){
			name = name.split("\\.")[0];
			if (!hillnames.contains(name)){
				hillnames.add(name);
				//diagnostic
				Koth.logger().info("Hillname " + name);
		// diagnostic...
		
		
	
		//find out what type each is
		
				if (config.getInt(name + ".type", 0) == WORLDGUARD){
					WGHilltop.load(name); //load it
				}else/* if (config.getInt(name + ".type", 0) == CHUNK){
					ChunkHilltop.load(name); //just an example
				}*/{
					Koth.logger().severe("Malformed type in Hilltops.yml:" + name);
				}
			}
		}
	}
	public void addOccupant(Location location, Team team) {
		//first try worldguard regions....
		Koth.logger().fine("addOccupant(" + location + ", " + team +")");
		WGHilltop.addOccupant(location, team);
		//.. then try any other types of regions that might exist.
	}
	public void addHilltop(String string, World world) {
		//assume we want a new Hilltop
		try{
			new WGHilltop(string, world);
		}catch(HilltopCreationFailException e){
			//try to make another type of Hilltop
			Koth.logger().info("Hilltop creation failed");
		}
	}
	public void addHilltop(Hilltop h){
		//Assume we already have a hilltop and just want to track it
		Hilltops.add(h);
		Koth.logger().info("Adding hilltop to Hilltops: " + Hilltops.size());
	}
	public void delHilltop(Hilltop h){
		Hilltops.remove(h);
	}
	public void saveList(){
		Configuration config = new Configuration(new File (plugin.getDataFolder(),"hilltops.yml"));
		config.load();
		Koth.logger().info("Saving Hilltop List");
		List<String> hillnames = new ArrayList<String>();
		for (Hilltop h: Hilltops){
			hillnames.add(h.getName());
			Koth.logger().info("saving to hilltop list: " + h.getName());
		}
		config.getStringList("hilltops",hillnames );
		config.save();
	}
	
}
