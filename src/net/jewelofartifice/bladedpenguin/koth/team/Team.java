package net.jewelofartifice.bladedpenguin.koth.team;

import java.util.Set;

import org.bukkit.entity.Player;

public interface Team {
	//String getID();
	void pay(double amount);
	public String getName();
	//public static get(String name);
	void MTick		(String message);
	void MCapturing	(String message);
	void MCapture	(String message);
	void MAdmin		(String message);
	void MOccupancy	(String message);
	void MOwnership	(String message);
	void MPay		(String message);
	Set<Player> getOnlinePlayers();
	void MOwnDecay(String string);
	@Override
	public boolean equals(Object o);
	@Override
	public int hashCode();
}
