package net.jewelofartifice.bladedpenguin.koth;

import java.util.TimerTask;

import net.jewelofartifice.bladedpenguin.koth.hilltop.Hilltop;
import net.jewelofartifice.bladedpenguin.koth.team.Team;

import org.bukkit.entity.Player;

public class KothTicker extends TimerTask {
	Koth plugin;
	KothTicker(Koth p){
		plugin = p;
	}
	@Override
	public void run() {
		Koth.logger().finer("Kothtick");
		//change this to for players on a team
		for (Team t : plugin.tm.getTeams()){ //NOTE: if a player is tricky enough to be on multiple teams, they will be counted more than once. Double agents FTW!
			plugin.mh.send(t, Messager.reason.TICK, t.getName() + " Tick!");
			for (Player p : t.getOnlinePlayers()){ //see who is sitting where, and add the to the relevant hilltop for later processing
				plugin.hm.addOccupant(p.getLocation(),t);
			}
		}
		//checks for ownership changes, pays players
		for (Hilltop h : plugin.hm.Hilltops){
			h.tick();
		}
	}
}