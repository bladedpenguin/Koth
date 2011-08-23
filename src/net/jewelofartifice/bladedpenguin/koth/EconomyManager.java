package net.jewelofartifice.bladedpenguin.koth;

import net.jewelofartifice.bladedpenguin.koth.team.Team;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.iConomy.iConomy;
//import com.iConomy.system.Holdings;



public class EconomyManager {
	Koth plugin;
	iConomy iconomy;
	EconomyManager(Koth p){
		plugin = p;
		Plugin plug = plugin.getServer().getPluginManager().getPlugin("iConomy");
		if (plug == null || !(plug instanceof iConomy)) {
			Koth.logger().severe("Koth failed to integrate with iConomy");
			Koth.logger().severe("Item based payout not yet supported");
        	plugin.getServer().getPluginManager().disablePlugin(plugin);
			return;
		}
		iconomy = (iConomy) plug;
		Koth.logger().info("Koth has integrated with iConomy");
	}
	public void pay(Team t, double amount){
		if (iconomy == null){
			Koth.logger().severe("Koth could not find iConomy");
			t.MPay("You recieve " + amount);
			return;
		}
		for (Player p : t.getOnlinePlayers()){
			pay(p,amount);
		}

	}
	private void pay(Player p, double amount) {
		iConomy.getAccount(p.getName()).getHoldings().add(amount);
	}
}
