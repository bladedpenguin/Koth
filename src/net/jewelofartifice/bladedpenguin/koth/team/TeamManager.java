package net.jewelofartifice.bladedpenguin.koth.team;

import java.util.HashMap;

//not to self: think about Map Team 
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jewelofartifice.bladedpenguin.koth.Koth;

import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class TeamManager {
	
	private Koth plugin;
	//private Factions factions; 
	private Map<String,Team> Teams = new HashMap <String,Team>();
	public TeamManager(Koth p){
		plugin = p;
		
		FTeam.initialize(p, this);
		
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
		if (!plugin.getServer().getPluginManager().isPluginEnabled("Factions")){
			Koth.logger().severe("Koth: Factions not enabled....");
			return;
		}
		for (Faction f : Factions.i.get()){
			if (f.isNormal()){
				new FTeam(plugin,f);
				Koth.logger().info("Koth: Faction " + f.getTag() + " Loaded" );
			}
		}
	}
	public Team getTeam(String string){
		//determine what kind of team
		//call the static get() for the particular type
		return Teams.get(string);
	}
	public void addTeam(Team t){
		String name = t.getName();
		
		while (Teams.containsKey(name)){
			name = name + '0';
		}
		Teams.put(t.getName(), t);
	}
}

