package net.jewelofartifice.bladedpenguin.koth;


import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jewelofartifice.bladedpenguin.koth.hilltop.Hilltop;
import net.jewelofartifice.bladedpenguin.koth.hilltop.HilltopManager;
import net.jewelofartifice.bladedpenguin.koth.team.Team;
import net.jewelofartifice.bladedpenguin.koth.team.TeamManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;



public class Koth extends JavaPlugin{
	//public static Set<Hilltop> Hilltops = new HashSet<Hilltop>(); //this is accessed almost soley by 
	
	private long tickInterval = 10000;//milliseconds.
	boolean useOP = false;
	boolean individualLogs = true;//hard on the disk space, but ensures players don't miss importatn happenings while offline
	//private long saveInterval = 3600000;//by default, it saves every hour and Whenever Hilltops and teams are added
	
	//private KothServerListener serverListener = new KothServerListener(this);
	public HilltopManager hm;
	public TeamManager tm;
	Timer timer = new Timer();
	//Vault
    public static Economy economy = null;
    public static net.milkbowl.vault.permission.Permission permission = null;
    public Messager mh;
	public static Permission admin = new Permission("koth.admin", PermissionDefault.OP);
	public static Permission info = new Permission("koth.info", PermissionDefault.OP);
	public static Permission otherinfo = new Permission("koth.otherinfo", PermissionDefault.OP);
	public static String defaultGroup = "default";
	
	
	@Override
	public void onDisable() {
		timer.cancel();
	}

	@Override
	public void onEnable() {

		
		//get the managers
		//PluginManager pm = getServer().getPluginManager();
		hm = new HilltopManager(this);
		//hm.loadAll(); //uneccessary: this is handled by reloadCOnfig() later on in this function
		tm = new TeamManager(this);
		setupEconomy();
		setupPermissions();
		mh = new Messager(this);
		//pm.registerEvent(Event.Type.PLUGIN_ENABLE, serverListener, Priority.Normal, this);
		//pm.registerEvent(Event.Type.PLUGIN_DISABLE, serverListener, Priority.Normal, this);
		timer.schedule(new KothTicker(this), 10000, tickInterval);
		
		tm.load();
		
		logger().setLevel(Level.FINEST);
		logger().info("Koth Enabled");
		
		
		//configuration
		reload();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if ((cmd.getName().compareToIgnoreCase("koth")) == 0){
			if (args.length == 0){
				sender.sendMessage("/koth reload");
				sender.sendMessage("/koth notify");
				sender.sendMessage("/koth team");
				sender.sendMessage("/koth locate <Hilltop>");
				sender.sendMessage("/hilltop <Hilltop>");
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
			}else if (args[0].compareToIgnoreCase("notify") == 0) {
				for (Messager.reason r : Messager.reason.values()){
					sender.sendMessage(r.toString() + "  notify: " + PlayerConfig.get(sender).getNotify(r));
				}
				sender.sendMessage("Per Player notification control not yet implemented");
				return true;
			}else if (args[0].compareToIgnoreCase("teams") == 0) {
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
				saveConfig();
				return true;
			
			} else if (args[0].compareToIgnoreCase("locate") == 0) {
				if (args[1] != null){
					Hilltop h;
					if (PlayerConfig.get(sender).knownHilltops.contains(h = hm.getHilltop(args[1]))){
						sender.sendMessage("Pretending to locate " + h.getName());
					}else
						sender.sendMessage("You Don't know where that is");
				}
				
				//TODO
				
				return true;
			} else if (args[0].compareToIgnoreCase("info") == 0) {
				if ((sender instanceof Player) && !sender.hasPermission(info)){
					sender.sendMessage("You require " + info.getName());
					return true;
				}
				if (args.length < 2){
					sender.sendMessage("/koth info <hilltop>");
					return true;
				}
				Hilltop h = hm.getHilltop(args[1]);
				if (h == null){
					sender.sendMessage("Sorry, " + args[1] + " is not a valid hilltop");
					return true;
				}
				if (!(sender instanceof Player) || PlayerConfig.get(sender).knownHilltops.contains(h)){
					sender.sendMessage(h.printReport());
					return true;
				}
			} else if (args[0].equalsIgnoreCase("owners"))
				if (!sender.hasPermission(info) || !(sender instanceof Player)){
					sender.sendMessage("You require " + info.getName());
					return true;
				}
				if (args.length < 2){
					sender.sendMessage("/koth owners <hilltop>");
					return true;
				}
				Hilltop h;
				if (null != (h = hm.getHilltop(args[1]))){
					if (!(sender instanceof Player) ||	(h.getOwner() != null && h.getOwner().contains((Player) sender)) || PlayerConfig.get(sender).knownHilltops.contains(h)){
						sender.sendMessage(h.printOwners());
						return true;
					}
				}
				

		} else if (((cmd.getName().compareToIgnoreCase("hilltop")) == 0) 
				|| ((cmd.getName().compareToIgnoreCase("hill")) == 0)) {
			
			if (sender instanceof Player && !sender.hasPermission(admin) && args.length < 0)
				return true;
			if (args[0].compareToIgnoreCase("list") == 0) {
				for (Hilltop h : hm.Hilltops){
					sender.sendMessage("Hilltop: " + h.getName() + " owned by " + h.getOwner());
					return true;
				}
			} else if (args[0].compareToIgnoreCase("add") == 0) {
				if (args.length <= 1){
					sender.sendMessage("/koth add <region>");
					return true;
				}
				if (args.length >= 3 && getServer().getWorld(args[2])instanceof World){
					hm.createHilltop(args[1],getServer().getWorld(args[2]));
				}else if (sender instanceof Player){
					hm.createHilltop(args[1],((Player) sender).getWorld());
				} else {
					sender.sendMessage("You gotta say which world");
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

    public void reload(){
    	logger().info("Loading configuration");
    	reloadConfig();
		tickInterval = getConfig().getInt("tickInterval", 10000);
		defaultGroup = getConfig().getString("defaultGroup","default");
		logger().fine("Koth: tickInterval Loaded: " + tickInterval);
		useOP = getConfig().getBoolean("useOP", false);
		hm.loadAll();
		tm.load();
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
		
		saveConfig();
		PlayerConfig.setPlugin(this);
		
    }
    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    private Boolean setupPermissions()
    {
        RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
}
