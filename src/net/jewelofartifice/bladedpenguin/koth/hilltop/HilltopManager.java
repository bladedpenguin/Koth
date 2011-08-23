package net.jewelofartifice.bladedpenguin.koth.hilltop;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;

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
		loadAll();
	}
	private void loadAll() {
		// TODO write this shit
		
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
	
}
