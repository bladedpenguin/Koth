package net.jewelofartifice.bladedpenguin.koth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jewelofartifice.bladedpenguin.koth.hilltop.Hilltop;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class PlayerConfig { //the ones that are false are intended to be super spammy
	boolean notifyTick = false;
	boolean notifyOwnership = false;
	boolean notifyOwned = true;
	boolean notifyCapture = true;
	boolean notifyPay = true;
	boolean notifyOwnDecay = false;
	Set<Hilltop> watching = new HashSet<Hilltop>(); //a list of the hilltops that the player cares about
	public static enum messageReason{
		TICK, OWNERSHIP_CHANGE, OWNERSHIP
		
	}
	private static Map<Player,PlayerConfig> instances = new HashMap<Player,PlayerConfig>();
	private static Koth plugin; 
	public PlayerConfig(Player p) {
		Configuration config = plugin.getConfiguration();
		config.load();
		config.getBoolean("Players." + p.getName() + ".notifyPay", true);
		config.getBoolean("Players." + p.getName() + ".notifyTick", false);
		config.getBoolean("Players." + p.getName() + ".notifyCapture", true);
		config.getBoolean("Players." + p.getName() + ".notifyOwnership", true);
	}
	public static PlayerConfig get(Player p) {
		PlayerConfig c = instances.get(p);
		if (c == null){
			c = new PlayerConfig(p);
			instances.put(p, c);
			//load settings I guess
		}
		return c;
	}
	public static void setPlugin(Koth plugin) {
		PlayerConfig.plugin = plugin;
	}
}
