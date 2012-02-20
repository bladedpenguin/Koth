package net.jewelofartifice.bladedpenguin.koth.hilltop;

import java.util.ArrayList;
import net.jewelofartifice.bladedpenguin.koth.team.Team;

import org.bukkit.entity.Player;

public interface Hilltop {
	//unless otherwise mentioned, commented methods are intended to be part of the nterface, but can't be because of limitations of java.
	
	boolean isOccupiedBy(Player occupant);
	void tick();
	void save();
	//static void load(String name);
	//static void addOccupant(Location loc, Team t);
	//static final Koth Plugin;
	//
	String getName();
	//do I needa a setname?
	void addOccupant(Team t);
	public void configure();
	Team getOwner();
	String printReport(); 
	String printOwners();
	String getGroup(); //the permissions group that is added/removed on capture
	ArrayList<String> Permissions = new ArrayList<String>();
}
