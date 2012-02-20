package net.jewelofartifice.bladedpenguin.koth;

import net.jewelofartifice.bladedpenguin.koth.hilltop.Hilltop;
import net.jewelofartifice.bladedpenguin.koth.team.Team;

import org.bukkit.entity.Player;

public class Messager {
	Koth plugin;
	private String buffer = "";
	Messager(Koth p){
		plugin = p;
		//Configuration config = plugin.getConfiguration();
	
	}
	
	public static enum reason { //every entry here needs to be added to PlayerConfig constructor, as well as command.
		OWNERSHIP_CHANGE,
		CAPTURE,
		LOSS,
		PAY,
		PERMISSIONS_CHANGE, ENTRY, EXIT, ADMIN, OCCUPANCY, TICK, CAPTURING, LOSING
	}
	
	void buffer(String message){
		buffer = message;
	}
	
	public void send(reason r, String message) {
		for (Player p : plugin.getServer().getOnlinePlayers()){
			{
				PlayerConfig.get(p).send(message, r);
			}
		}
	}
	
	public void send(Team t, reason r, String message){
		for (Player p : t.getOnlinePlayers()){
			//Koth.logger().info("PlayerConfig: " + p.getName());
			PlayerConfig pc = PlayerConfig.get(p);
			if (pc != null)
			{
				//Koth.logger().info("pc = " + pc.getName());
				pc.send(message, r);
			} else Koth.logger().info("pc null error in Messager.java");
		}
	}
	public void resend(Team t, reason r){ //dunno if I'll use resend. It's an attempt at sending a message without sending it to those who already have it. 
		for (Player p : t.getOnlinePlayers()){
			PlayerConfig.get(p).send(buffer,r); 
		}
	}
	public void resend(reason r){ 
		//TODO
	}
	public void send(Hilltop h, reason r, Team t, String message){
		if (r == reason.OWNERSHIP_CHANGE && h.getOwner() == t){ //more of the attempt to automate messaging. on hold for now
			Koth.logger().warning("n00b coder tried to send ownership change to owning team");
			return;
		}
		for (Player p : t.getOnlinePlayers()){
			 
			PlayerConfig.get(p).send(message, h, r);
		}
	}
	
	public void send(Hilltop h, reason r, String message){ //unused I think (verify before deleting) 
		//this is an attempt at automated messaging with a single command
		for (Player p : plugin.getServer().getOnlinePlayers()){
			PlayerConfig pc = PlayerConfig.get(p);
			if ((r  == reason.CAPTURE || r == reason.LOSS) && !pc.knownHilltops.contains(h))
				pc.knownHilltops.add(h);
			if (pc.knownHilltops.contains(h)){
				
			}
		}
	}
}