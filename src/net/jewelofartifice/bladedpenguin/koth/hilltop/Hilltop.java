package net.jewelofartifice.bladedpenguin.koth.hilltop;

import net.jewelofartifice.bladedpenguin.koth.team.Team;

import org.bukkit.entity.Player;

public interface Hilltop {
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

}
