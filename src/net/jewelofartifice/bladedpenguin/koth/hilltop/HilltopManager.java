package net.jewelofartifice.bladedpenguin.koth.hilltop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import net.jewelofartifice.bladedpenguin.koth.Koth;
import net.jewelofartifice.bladedpenguin.koth.team.Team;

public class HilltopManager {
	public static final int NONE = 0;
	public static final int WORLDGUARD = 1;
	private Koth plugin;
	public Set<Hilltop> Hilltops =  new HashSet<Hilltop>(); //this should be private and a map<String name, Hilltop>
	public HilltopManager(Koth p){
		plugin = p;
		WGHilltop.initialize(plugin);
		//we're calling loadAll() in onEnable() because of a circular dependancy. to be clear, loadall instantiates hilltops, which relies on plugin.hm, which is a null pointer until this constructor returns. I guess we could pass this as an argument to Hilltop constructors to resolve  
		//loadAll();
		
	}
	public void loadAll() {
		//Configuration config = new Configuration(new File (plugin.getDataFolder(),"hilltops.yml"));
		//config.load();
		plugin.reloadConfig();
		 
		//TODO figure this shit out
		//purge the secondary induction buffer.
		Hilltops.clear();
		
		Koth.logger().info("Listing elements in hilltop.yml");
		//find all the Hilltops Hilltops, 
		Set<String> hillnames = new HashSet<String>();
		for (String name: plugin.getConfig().getKeys(true)){
			name = name.split("\\.")[1];
			if (!hillnames.contains(name)){
				hillnames.add(name);
				//diagnostic
				Koth.logger().info("Hill found:  " + name);
		// diagnostic...
		
		
	
		//find out what type each is
		
				if (plugin.getConfig().getInt("Hilltops." + name + ".type", 0) == WORLDGUARD){
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
	public void createHilltop(String string, World world) {
		//assume we want a new Hilltop
		//the interface between this and Cmd needs to be defined
		//do we use overloading?
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
		//Configuration config = new Configuration(new File (plugin.getDataFolder(),"hilltops.yml"));
		//config.load();
		plugin.reloadConfig();
		ConfigurationSection config = plugin.getConfig();
		Koth.logger().info("Saving Hilltop List");
		List<String> hillnames = new ArrayList<String>();
		for (Hilltop h: Hilltops){
			hillnames.add(h.getName());
			Koth.logger().info("saving to hilltop list: " + h.getName());
		}
		config.set("Hilltops",hillnames );
		plugin.saveConfig();
	}
//	@Override
//	public Set<Team> getOwners(){
//		return Owner
//	}
	public Hilltop getHilltop(String name) { //maybe we need to use a map<name,hilltop>, or this function needs ot make multiple passes, at descending levels of precision
		Koth.logger().finest("getHilltop() seeking: " + name);
		for (Hilltop h : Hilltops){
			Koth.logger().finest("getHilltop() Hilltop detected: " + h.getName());
			if (h.getName().equals(name)){
				Koth.logger().finest("getHilltop() returning " + h.getName());
				return h;
			}
		}
		return null;
	}
	public Set<Hilltop> getHilltops() {
		// TODO Auto-generated method stub
		return Hilltops;
	}
	
}
