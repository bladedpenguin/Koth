package net.jewelofartifice.bladedpenguin.koth.team;

import java.util.Set;

import org.bukkit.entity.Player;

public interface Team {
	int getID();
	void pay(double amount);
	public String getName();
	void MTick		(String message);
	void MCapturing	(String message);
	void MCapture	(String message);
	void MAdmin		(String message);
	void MOccupancy	(String message);
	void MOwnership	(String message);
	void MPay		(String message);
	Set<Player> getOnlinePlayers();
	void MOwnDecay(String string);
}
