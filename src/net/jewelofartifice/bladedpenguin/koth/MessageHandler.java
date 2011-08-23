package net.jewelofartifice.bladedpenguin.koth;

import net.jewelofartifice.bladedpenguin.koth.team.FTeam;
import net.jewelofartifice.bladedpenguin.koth.team.Team;

import org.bukkit.entity.Player;

public class MessageHandler {
	Koth plugin;
	MessageHandler(Koth p){
		plugin = p;
		//Configuration config = plugin.getConfiguration();
		
	}
	PlayerConfig.messageReason type  = PlayerConfig.messageReason.OWNERSHIP_CHANGE;
	public void Tick(FTeam t, String message){
		//format?
		for (Player p : t.getOnlinePlayers()){
			//individual format?
			if (PlayerConfig.get(p).notifyTick)
				p.sendMessage(message);
		}
		
	}
	public void OwnerShip(FTeam t, String message){
		//format?
		for (Player p : t.getOnlinePlayers()){
			//individual format?
			if (PlayerConfig.get(p).notifyTick)
				p.sendMessage(message);
		}
		
	}
	public void Pay(String message, Team t) {
		for (Player p : t.getOnlinePlayers()){
			if (PlayerConfig.get(p).notifyPay) p.sendMessage(message);
		}
	}
	public void Capture(String message, Team t) {
		for (Player p : t.getOnlinePlayers()){
			if (PlayerConfig.get(p).notifyCapture) p.sendMessage(message);	
		}
	}
}

