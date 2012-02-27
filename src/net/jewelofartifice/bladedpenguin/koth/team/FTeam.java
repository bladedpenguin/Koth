package net.jewelofartifice.bladedpenguin.koth.team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jewelofartifice.bladedpenguin.koth.Koth;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;

public class FTeam implements Team,Listener{

	Faction faction;
	String name = "defaultteam";
	static TeamManager tm;
	static Koth plugin;
	ArrayList <Player> players;
	private Map<Faction,FTeam> fteams = new HashMap<Faction,FTeam>();
	ArrayList <String> groups = new ArrayList<String>();
	static void initialize(Koth p,TeamManager TM){
		plugin = p;
		tm = TM;
	}
	static void load(){
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
	FTeam(Koth p, Faction f){
		plugin = p;
		faction = f;
		name = f.getTag();
		fteams.put(faction, this);
		tm.addTeam(this);
		groups.add(Koth.defaultGroup);
		
			
		return;
	}
	public void pay(double amount){
		for (Player p : getOnlinePlayers()){
			Koth.logger().info("Koth: " + p.getName() + " was paid " + amount);
			Koth.economy.depositPlayer(p.getName(), amount);
		}
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
	public boolean equals(Object o){
		if (o instanceof FTeam){
			if (((FTeam) o).getID() ==  this.getID()){
				return true;
			}
		} return false;
	}
	public int hashCode(){
		return getID().hashCode();
	}

	public void MTick(String message) {
		//convenience function
		//should call the relevant messagehandler function.
		
		
	}

	public String getID() {
		return faction.getId();
	}

	@Override
	public boolean contains(Player p) {
		if (faction.getFPlayers().contains(FPlayers.i.get(p)))
			return true;
		return false;
	}

	@Override
	public void update() {
		if (!Arrays.asList(Koth.permission.getGroups()).contains(getID())){
			for (Player p : getPlayers())
			Koth.permission.playerAddGroup(p, getID());
		}
		
	}

	public Set<Player> getPlayers() {
		Set <Player> P = new HashSet<Player>();
		
		for (FPlayer fp: faction.getFPlayers()){
			P.add(fp.getPlayer());
		}
		
		return P;
	}

	@Override
	public void addGroup(String group) {
		if (groups.contains(group)){
			Koth.logger().severe("Team " + getName() + "is getting permission group " + group + " more than once. Hilltop groups must be used only for a single Hilltop" );
		}
		for (Player p : getPlayers()){
			if (Koth.permission.playerInGroup(p,group)){
				Koth.logger().severe("Player " + p.getName() + " is getting permission group " + group + " from another source!");
			}
			Koth.permission.playerAddGroup(p,group);
		}
	}

	@Override
	public void removeGroup(String group) {
		if (!groups.contains(group)){
			Koth.logger().severe("Team " + getName() + " has already had permissions group " + group + " removed.");
			return;
		} 
		for(Player p : getPlayers()){
			Koth.permission.playerRemoveGroup(p,group);
		}
	}
	@EventHandler
	void playerJoin(FPlayerJoinEvent event){
		Player player = event.getFPlayer().getPlayer();
		for (String g : groups){
			if (Koth.permission.playerInGroup(player,g)){
				Koth.logger().severe("Player " + player.getName() + " already has group " + g);
			}
			Koth.permission.playerAddGroup(player, g);
		}
	}
	@EventHandler
	void playerJoin(FPlayerLeaveEvent event){
		Player player = event.getFPlayer().getPlayer();
		for (String g : groups){
			Koth.permission.playerRemoveGroup(player,g);
		}
	}
	@Override
	public ArrayList<String> getGroups() {
		// TODO Auto-generated method stub
		return groups;
	}
}
