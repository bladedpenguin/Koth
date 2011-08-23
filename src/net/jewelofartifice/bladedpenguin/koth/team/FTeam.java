package net.jewelofartifice.bladedpenguin.koth.team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jewelofartifice.bladedpenguin.koth.Koth;

import org.bukkit.entity.Player;

import com.massivecraft.factions.Faction;

public class FTeam implements Team{

	Faction faction;
	String name = "defaultteam";
	static TeamManager tm;
	static Koth plugin;
	private Map<Faction,FTeam> fteams = new HashMap<Faction,FTeam>();
	static void initialize(Koth p){
		plugin = p;
	}

	FTeam(Koth p, Faction f){
		plugin = p;
		faction = f;
		name = f.getTag();
		fteams.put(faction, this);
		plugin.tm.addTeam(this);
		return;
	}
	public void pay(double amount){
		plugin.em.pay(this,amount);
	}
	public Set<Player> getOnlinePlayers() {
		Set<Player> players = new HashSet<Player>();
		for (Player p : faction.getOnlinePlayers())
			players.add(p);
		return players;
	}
	public String getName() {
		return name;
	}
	public void broadcast(String message){
		faction.sendMessage(message);
	}
	
	//functions with prefix M are messaging convenience functions.
	public void MTick(String message) {
		//convenience function
		//should call the relevant messagehandler function.
		
		
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void MCapturing(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void MCapture(String message) {
		plugin.mh.Capture(message,this);
		
	}

	@Override
	public void MAdmin(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void MOccupancy(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void MOwnership(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void MPay(String message) {
		plugin.mh.Pay(message,this);
		
	}

	@Override
	public void MOwnDecay(String string) {
		// TODO Auto-generated method stub
		
	}
	
}
