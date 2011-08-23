package net.jewelofartifice.bladedpenguin.koth;



import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jewelofartifice.bladedpenguin.koth.hilltop.Hilltop;
import net.jewelofartifice.bladedpenguin.koth.hilltop.HilltopManager;
import net.jewelofartifice.bladedpenguin.koth.team.Team;
import net.jewelofartifice.bladedpenguin.koth.team.TeamManager;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;



public class Koth extends JavaPlugin{
	public static Set<Hilltop> Hilltops = new HashSet<Hilltop>(); //this is accessed almost soley by 
	
	private long tickInterval = 10000;//milliseconds.
	//private long saveInterval = 3600000;//by default, it saves every hour and Whenever Hilltops and teams are added
	
	private KothServerListener serverListener = new KothServerListener(this);
	public HilltopManager hm;
	public TeamManager tm;
	Timer timer = new Timer();
	public EconomyManager em;
	public MessageHandler mh;

	@Override
	public void onDisable() {
		timer.cancel();
	}

	@Override
	public void onEnable() {
		
		//configuration
		reloadConfig();
		
		//get the managers
		PluginManager pm = getServer().getPluginManager();
		hm = new HilltopManager(this);
		tm = new TeamManager(this);
		em = new EconomyManager(this);
		mh = new MessageHandler(this);
		pm.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Priority.Normal, this);
		timer.schedule(new KothTicker(this), 10000, tickInterval);
		tm.loadFactions();
		
		tm.load();
		
		logger().setLevel(Level.FINEST);
		logger().info("Koth Enabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if ((cmd.getName().compareToIgnoreCase("koth")) == 0){
			if (args.length == 0){
				sender.sendMessage("/koth help");
				return  true;
			}
			else{
				logger().info("args0 = " + args[0]);}
			if (args[0] == "reload"){
				reloadConfig();
				tm.load();
				sender.sendMessage("Reloaded Koth");
				logger().info("Reloaded Koth");
				return true;
			} else if (args[0].compareToIgnoreCase("teams") == 0) {
				for (Team t : tm.getTeams()){
					sender.sendMessage("Team: " + t.getName() + "" );
				}
				return true;
			} else if (args[0].compareToIgnoreCase("factionload") == 0) {
				sender.sendMessage("Koth: Loading Factions as Teams");
				tm.loadFactions();
				return true;
			} else if (args[0].compareToIgnoreCase("save") == 0) {
				sender.sendMessage("Koth: Saving Configuration");
				logger().info("Koth: Saving Configuration");
				getConfiguration().save();
				return true;
			}

		} else if (((cmd.getName().compareToIgnoreCase("hilltop")) == 0) 
				|| ((cmd.getName().compareToIgnoreCase("hill")) == 0)) {
			if (args[0].compareToIgnoreCase("add") == 0) {
				if (args.length <= 1){
					sender.sendMessage("/koth add <region>");
					return true;
				}
				if (args.length >= 3 && getServer().getWorld(args[2])instanceof World){
					hm.addHilltop(args[1],getServer().getWorld(args[2]));
				}else if (sender instanceof Player){
					hm.addHilltop(args[1],((Player) sender).getWorld());
				} else {
					sender.sendMessage("Sorry, hilltop addition relies on player for now");
				}
				return true;
			}else if (0 == args[0].compareToIgnoreCase("list")){
				String list = "Hilltops: ";
				for (Hilltop h : hm.Hilltops){
					list += h.getName();
					list += ", ";
				}
				sender.sendMessage(list);
			}
		}
		return false;
	}
    public static Logger logger() {
        return Logger.getLogger("Minecraft");
    }

    void reloadConfig(){
    	logger().info("Loading configuration");
    	Configuration config = getConfiguration();
		config.load();
		tickInterval = config.getInt("tickInterval", 10000);
		// I need to read the Permissions source to see how they load an arbitrary  number of groups
		//also need to look up you yaml does HashSet<whatever> and for that matter, arbitrary objects.
		/*ArrayList <HashMap> hilltops = (ArrayList) config.getProperty("hilltops");
		if (hilltops != null){
			Hilltop currentHilltop;
			for (Map hilltop :  hilltops){
				if (hilltop.get("type") == Hilltop.type.WORLDGUARD){
					currentHilltop = new Hilltop((String) hilltop.get("region"), getServer().getWorld((String) hilltop.get("world")) );
					currentHilltop.setName((String) hilltop.get("name"));
					
				} else {
					logger().info("Koth: Malformed Hilltop type");
				}
				
			}
		}*/
		
		config.save();
		PlayerConfig.setPlugin(this);
		
    }
}
