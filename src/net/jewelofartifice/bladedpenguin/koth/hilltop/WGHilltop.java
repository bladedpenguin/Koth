package net.jewelofartifice.bladedpenguin.koth.hilltop;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.jewelofartifice.bladedpenguin.koth.Koth;
import net.jewelofartifice.bladedpenguin.koth.team.Team;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WGHilltop implements Hilltop{
	Team owner = null;
	String name="defaulthilltop";
	ProtectedRegion region;
	Date expires = new Date();
	Date payday  = new Date();
	long payInterval = 30000; //in milliseconds
	double decayRate = 0.01;
	double squatRate = 0.1; //rate at which ownership is taken. This is heavily affected by Koth.tickInterval
	double payout = 1.0;
	World world = null;
	public Map<Team , Integer > occupants = new HashMap<Team , Integer >(); //how many of each team we have here
	public Map<Team, Float> owners = new HashMap<Team, Float>();
	private static boolean initialized = false;
	public static Koth plugin;
	public static WorldGuardPlugin worldGuard;
	static Map <ProtectedRegion,Hilltop> regions = new HashMap<ProtectedRegion,Hilltop>();
	public static void initialize(Koth p){
		if (initialized) return;
		plugin = p;
		PluginManager pm = plugin.getServer().getPluginManager();
		Plugin pl;
		pl = pm.getPlugin("WorldGuard");
	    if (pl == null || !(pl instanceof WorldGuardPlugin)) {
	        Koth.logger().severe("Koth was unable to integrate with ");
	        plugin.getServer().getPluginManager().disablePlugin(plugin);
	    } else {
	    	worldGuard = (WorldGuardPlugin) pl;
	    	Koth.logger().info("Koth integrated with WorldGuard");
	    }
	    initialized = true;
	}
	
	public static enum type {
		WORLDGUARD
		
	}
	
	public static void  addOccupant(Location loc, Team t){
		RegionManager rm = worldGuard.getRegionManager(loc.getWorld());
		for (ProtectedRegion r : rm.getApplicableRegions(toVector(loc))) {
			if (regions.containsKey(r)){
				regions.get(r).addOccupant(t);
				Koth.logger().fine("Someone is standing in " + regions.get(r).getName());
			}
		}
	}
	
	WGHilltop(String n, World w) throws HilltopCreationFailException{ //when I use something besides worldguard, there will be more constructors.
		//region = r;				// it takes a Player because eventually region notfound will create a radiused location centered hilltop.
		if (!initialized){
			Koth.logger().severe("Koth: Tried to add WGHilltop without initialization!");
			Plugin p = Bukkit.getServer().getPluginManager().getPlugin("Koth");
			initialize((Koth) p);
			
		}
		Koth.logger().info("Attempting to add region: " + n);
		//sender.sendMessage("Attempting to add region: " + n);
		name = n;
		world = w;
		ProtectedRegion r = worldGuard.getRegionManager(w).getRegion(name);
		if (r != null){
			this.region = r;
			regions.put(r, this);
			plugin.hm.addHilltop(this);
			Koth.logger().info("Region added: " + r.getId());
		} else {
			throw new HilltopCreationFailException();
		}
		Koth.logger().warning("Koth: About to try serializing Hilltops");
		configure();
		//try to get faction owner obviously this needs to be updates to Team.load(String prefix)
		/*int faction = 0;
		Configuration config = plugin.getConfiguration();
		faction = config.getInt("hilltops." + getName() + ".owner",0);
		if (faction != 0)
			owner = new Team(plugin, Faction.get(faction));
		
		
		//load the details for this
		//oops we're not doing this this way. sorry
		//hopefully we can save and load Hilltops as complete objects
		
		payInterval = config.getInt("hilltops." + getName() + ".owner", 30000);
		decayRate = config.getDouble("hilltops." + getName() + ".expireRate", 0.01);
		squatRate = config.getDouble("hilltops." + getName() + ".squatRate", 0.1);
		payout = config.getDouble("hilltops." + getName() + ".payout" , 1.0);*/
	}
	
	public void save(){ // I need thread safety here. I know it. I need to lock up the config before I open it
		File hilltopFile =  new File(plugin.getDataFolder(),"hilltops.yml");
		Configuration config = new Configuration(hilltopFile);
		config.load();
		config.setProperty(getName() + ".type", HilltopManager.WORLDGUARD);
		config.setProperty(getName() + ".world", world.getName());
		config.setProperty(getName() + ".region", region.getId());
		
		config.save();
		
		/*HashMap newHill = new HashMap();
		newHill.put("type", type.WORLDGUARD);
		newHill.put("region", region.getId());
		newHill.put("name", name);
		newHill.put("payInterval", payInterval);
		newHill.put("decayRate", decayRate);
		newHill.put("squatRate", squatRate);
		newHill.put("payout", payout);*/
	}
	
	//Only called for regions that exist in the config file.
	public static void load(String name){ 	//only called for prexisting regions
		Koth.logger().info("Loading Hilltop: " + name);
		Date start = new Date();
		File hilltopFile =  new File(plugin.getDataFolder(),"hilltops.yml");
		Configuration config = new Configuration(hilltopFile);
		config.load();
		if (config.getInt(name + ".type", HilltopManager.NONE) != HilltopManager.WORLDGUARD){
			Koth.logger().warning("Attempted to load non WG Hilltop " + name + " as a WGHilltop. Fail.");
			return;
		}
		World w = plugin.getServer().getWorld(config.getString(name + ".world", ""));
		if (w instanceof World && w != null){
			try {
				new WGHilltop(name,w);
			} catch (HilltopCreationFailException e) {
				Koth.logger().warning("Koth: Region not found.");
				e.printStackTrace();
			}
		}
	
		
		config.getString(name + ".region", name);
		Koth.logger().info("Loading took: " + (new Date().getTime() - start.getTime()) + "ms");
	}
	
	//called in the constructor to get
	public void configure(){ 
		Date start = new Date();
		File hilltopFile =  new File(plugin.getDataFolder(),"hilltops.yml");
		Configuration config = new Configuration(hilltopFile);
		config.load();
		
		//THis doesn't read: only writes if it hasn't yet been written. //maybe later I fixes that. 
		config.getInt(getName() + ".type", HilltopManager.WORLDGUARD);
		config.getString(getName() + ".world", world.getName());
		config.getString(getName() + ".region", region.getId());
		
		//actual readable options.
		payInterval = config.getInt(getName() + ".payInterval", 30000);
		decayRate = config.getDouble(getName() + ".expireRate", 0.01);
		squatRate = config.getDouble(getName() + ".squatRate", 0.1);
		payout = config.getDouble(getName() + ".payout" , 1.0);
		
		plugin.hm.saveList();
		//if (owner != null || (config.getString(config.getString(getName() + ".owner",null)) != null)){ 
		//	owner = plugin.tm.getTeam(config.getString(getName() + ".owner", owner.getName()));
		//}
		config.save();
		
		//need to get owners?
		//for owner in owners
		//owners.put(owner.getName(),config.getDouble(getName() + ".owners." + owner.getName(), 0.0))
		
		Koth.logger().info("Configure() took " + (new Date().getTime() - start.getTime()) + "ms");
	}
	
	public void capture(Team t){
		Configuration config = plugin.getConfiguration();
		Date now = new Date();
		owner = t;
		payday = new Date(now.getTime() + payInterval);
		t.MCapture("You have captured " + getName());
		config.setProperty("hilltops." + getName() + ".owner", owner.getID()); //at some point this needs to be moved into Team.save(String prefix) 
	}
	
	public void expire(){
		owner = null;
	}
	public void tick() {
		//notify and log
		Koth.logger().finer("Koth tick " + getName() );
		for (Entry<Team, Integer> e : occupants.entrySet()){
			Koth.logger().info("Koth: " + e.getValue() + " of " + e.getKey().getName() + " are in " + getName());
			e.getKey().MOccupancy("Koth: " + e.getValue() + " of " + e.getKey().getName() + " are in " + getName());
		} 
		
		//detect and assign ownership
		
		double total = 0;
		for (Team t : occupants.keySet()){ //count the ppl in the field
			total += occupants.get(t);
		}
		total = 1.0/total; //save time on divisions later
		double ownershipIncrease = 0.0; //so we know how much ownership to take from those not present
		for (Team t : occupants.keySet()){ //find their percent occupancy
			if (!owners.containsKey(t))
				owners.put(t, (float) 0.0);
			double po = occupants.get(t)*total; //calculate percent occupancy
			double od =  ((po - owners.get(t))*squatRate); //get occupancy differential, and slow it to the appropriate tick rate 
			owners.put(t,(float) (owners.get(t) + od)); //add the ownership differential
			Koth.logger().fine("t.getName od: " + od);
			ownershipIncrease += od; //document the ownership differential, to keep total ownership at 1.000
			t.MCapture(t.getName() +  " has captured " + (owners.get(t)*100) + "% of " + getName());
			Koth.logger().info(t.getName() +  " has captured " + (owners.get(t)*100) + "% of " + getName());
			if (owners.get(t)> 0.5 && owner != t){
				capture(t);		//if someone has 51%, give them ownership, unless they already have it.
			}
			
		}
		//determine who owns land and didn't participate today. then take ownership away to balance ownershipIncrease, and keep total ownership at 1.0000
		total = 0;
		for(Team t : owners.keySet()){
			if (!occupants.containsKey(t)){
				owners.put(t,(float) (owners.get(t) - owners.get(t)*ownershipIncrease) );
			}
			//Take ownership anyway  (because we hate you)
			owners.put(t, (float) (owners.get(t) * (1 - decayRate)));
			Koth.logger().info(t.getName() + " : " + owners.get(t));
			total += owners.get(t);
			t.MOwnDecay("You have " + owners.get(t) + " pwnership of " + getName());
		}
		if ( owner != null && owners.get(owner) < 0.5){
			expire();
		}

		
		
		Koth.logger().info("Total: " + total);
		
		//pay if appropriate
		Date now = new Date ();
		if (owner != null && now.getTime() >= payday.getTime()){
			owner.pay(payout);
			owner.MPay("You've been paid " + payout + " for ownership of " + getName());
			payday.setTime(payday.getTime()+payInterval); //pretty sure getTime is long miliseconds. otherwise I'm in trouble.
			Koth.logger().info("paid: " + (int) now.getTime()/1000 + " and payday is: " + (int) payday.getTime()/1000);
		}
		clearOccupants();
	}
	public void addOccupant(Team team){
		Koth.logger().info(team.getName() + " added to " + getName());
		if (occupants.containsKey(team))
			occupants.put(team,occupants.get(team) + 1);
		else
			occupants.put(team, 1);
	}
	void clearOccupants(){
		occupants.clear();
	}
	public String getName(){
		return name;
	}
	public void setName(String n){
		name = n;
	}

	@Override
	public boolean isOccupiedBy(Player occupant) {
		// TODO Auto-generated method stub
		return false;
	}

}

