package net.jewelofartifice.bladedpenguin.koth.team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jewelofartifice.bladedpenguin.koth.Koth;

import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class TeamManager {
	
	private int lastID = 0;
	private Koth plugin;
	//private Factions factions; 
	private Map<Integer,Team> Teams = new HashMap <Integer,Team>();
	public TeamManager(Koth p){
		plugin = p;
		FTeam.tm = this;
		
		Plugin pl = plugin.getServer().getPluginManager().getPlugin("Factions");
		if ((pl != null) && (pl instanceof Factions)){
			Koth.logger().info("Koth: Factions found");
		}else{
			Koth.logger().severe("Koth was unable to integrate with Factions");
		}
		
		//link against factions
	}
	public void load(){
		loadFactions();
		//load whatever other type of shit we need
	}
	public Set<Team> getTeams(){
		Set<Team> teams = new HashSet<Team>(); 
		for (Team t : Teams.values())
			teams.add(t);
		return teams;
	}
	
	public void loadFactions() {
		for (Faction f : Faction.getAll()){
			if (f.isNormal()){
				new FTeam(plugin,f);
				Koth.logger().info("Koth: Faction " + f.getTag() + " Loaded" );
			}
		}
	}
	public Team getTeam(int ID){
		return Teams.get(ID);
	}
	public void addTeam(Team t){
		lastID++;
		Teams.put(lastID, t);
	}
}

