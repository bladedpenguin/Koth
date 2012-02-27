package net.jewelofartifice.bladedpenguin.koth.team;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.entity.Player;

public interface Team {
	//String getID();
	// public static load(); ???
	void update();
	void pay(double amount);
	public String getName();
	//public static get(String name);
	Set<Player> getOnlinePlayers();
	Set<Player> getPlayers();
	void addGroup(String group);
	void removeGroup(String group);
	@Override
	public boolean equals(Object o);
	@Override
	public int hashCode();
	boolean contains(Player sender);
	ArrayList<String> Groups = new ArrayList<String>();
	public ArrayList<String> getGroups();
}
