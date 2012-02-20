package net.jewelofartifice.bladedpenguin.koth;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jewelofartifice.bladedpenguin.koth.Messager.reason;
import net.jewelofartifice.bladedpenguin.koth.hilltop.Hilltop;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerConfig {
	static Koth plugin= null;
	static FileConfiguration config;
//	public boolean notifyCapture = true;
//	public boolean notifyAdmin = false;
//	public boolean notifyTick = false;
//	public boolean notifyPay = false;
	Set<Hilltop> knownHilltops = new HashSet<Hilltop>();
	CommandSender user;
	List<String> log = new ArrayList<String>();
	int logMark = 0;
	private Map<Messager.reason,Boolean> shouldNotify = new HashMap<Messager.reason,Boolean>(); //yeah its silly, but I want it to match shouldLog
	private Map<Messager.reason,Boolean> shouldLog = new HashMap<Messager.reason,Boolean>(); //yeah its a silly name, but "log" was already taken
	private static Map<CommandSender,PlayerConfig> Configs= new HashMap<CommandSender,PlayerConfig>();
	private static FileConfiguration staticConfig; 
	
	
	private PlayerConfig(CommandSender sender) {
		reloadConfig();
		user = sender;
		//File f = new File (plugin.getDataFolder(),"players.yml"); //this probably needs to be ina try{}
		ConfigurationSection config = getConfigSection(sender.getName());
		
		
		shouldNotify.put(Messager.reason.CAPTURE, config.getBoolean("notifyCapture", true));
		shouldNotify.put(Messager.reason.CAPTURING, config.getBoolean("notifyCapturing", true));
		shouldNotify.put(Messager.reason.LOSS, config.getBoolean("notifyLoss", true));
		shouldNotify.put(Messager.reason.LOSING, config.getBoolean("notifyLosing", true));
		shouldNotify.put(Messager.reason.OWNERSHIP_CHANGE, config.getBoolean("notifyOwnershipChange", true));//when a hilltop that you know of by don't own changes hands
		shouldNotify.put(Messager.reason.PAY, config.getBoolean("notifyPay", true));
		shouldNotify.put(Messager.reason.PERMISSIONS_CHANGE, config.getBoolean("notifyPermissionsChange", true));//when you gain or lose permissions due to Koth
		shouldNotify.put(Messager.reason.ENTRY, config.getBoolean("notifyEntry", true));
		shouldNotify.put(Messager.reason.EXIT, config.getBoolean("notifyExit", true));
		shouldNotify.put(Messager.reason.ADMIN, config.getBoolean("notifyAdmin", false));
		shouldNotify.put(Messager.reason.OCCUPANCY, config.getBoolean("notifyOccupancy", false));
		shouldNotify.put(Messager.reason.TICK, config.getBoolean("notifyTick", false));
		
		shouldLog.put(Messager.reason.CAPTURE, config.getBoolean("logCapture", true));
		shouldLog.put(Messager.reason.CAPTURING, config.getBoolean("logCapturing", true));
		shouldLog.put(Messager.reason.LOSS, config.getBoolean("logLoss", true));
		shouldLog.put(Messager.reason.LOSING, config.getBoolean("logLosing", true));
		shouldLog.put(Messager.reason.OWNERSHIP_CHANGE, config.getBoolean( ".logOwnershipChange", true));//when a hilltop that you know of by don't own changes hands
		shouldLog.put(Messager.reason.PAY, config.getBoolean("logPay", true));
		shouldLog.put(Messager.reason.PERMISSIONS_CHANGE, config.getBoolean(".logPermissionsChange", true));//when you gain or lose permissions due to Koth
		shouldLog.put(Messager.reason.ENTRY, config.getBoolean("logEntry", true));
		shouldLog.put(Messager.reason.EXIT, config.getBoolean("logExit", true));
		shouldLog.put(Messager.reason.ADMIN, config.getBoolean("logAdmin", true));
		shouldLog.put(Messager.reason.OCCUPANCY, config.getBoolean("logOccupancy", true));
		shouldLog.put(Messager.reason.TICK, config.getBoolean("logTick", true));
		Koth.logger().info("Koth: PlayerConfig for " + user.getName() + " loaded");
		
		//Koth.logger().info("user is " + user.getName());
		//incomplete
		//load known hilltops
		logMark = config.getInt(sender.getName() + ".LogMark", 0);
		Koth.logger().info("Koth: Attempting to save PlayerConfig");
		saveConfig();
		
		//load log from separate playerLogs.yml or logs/name.txt
	}

	public static PlayerConfig get(CommandSender sender) {
		Koth.logger().fine("Koth: Attempting to get PlayerConfig for " + sender.getName());
		if (Configs.containsKey(sender)){
			if (Configs.get(sender)==null)
				Koth.logger().severe("Koth null PlayerConfig retrieved");
			Koth.logger().finest("PlayerConfig exists for " + sender.getName());
		}
		else{
			Koth.logger().finest("Creating PlayerConfig for " + sender.getName());
			Configs.put(sender, new PlayerConfig(sender));
		}
		return Configs.get(sender);
	}
	
	
	public void send(String s, Messager.reason r){
		if (shouldNotify.get(r)){
			user.sendMessage(s);
		}
		if (shouldLog.get(r)){
			log.add(s);
		}
	}
	public void send(String s, Hilltop h, Messager.reason r){
		if (!knownHilltops.contains(h))
			if (r == Messager.reason.CAPTURE || r == Messager.reason.LOSS || r == Messager.reason.ENTRY || r == Messager.reason.EXIT){
				knownHilltops.add(h);
			}else return;
		
		if (shouldNotify.get(r)){
			user.sendMessage(s);
		}
		if (shouldLog.get(r)){
			log.add(s);
		}
	}
	public void logView(){
		for (int i=0; i<10;i++){
			user.sendMessage(logMark + " : " + log.get(logMark));
			logMark++;
			if (!getConfig().contains(getName()))
				getConfig().createSection(getName());
			ConfigurationSection config = getConfig().getConfigurationSection(getName());
			config.set("logMark", logMark);
			saveConfig();
		}
	}
	public void logWrite(){
		//TODO: make this thing write to a file
	}
	public static void setPlugin(Koth koth) {
		plugin = koth;
	}
	public void setNotify(reason r, boolean b) {
		shouldNotify.put(r, b);
		return;
	}

	public Boolean getNotify(reason r) {
		return shouldNotify.get(r);
		
	}

	public String getName() {
		return user.getName();
	}
	private static FileConfiguration getConfig(){
		if(staticConfig == null){
			reloadConfig();
		}
		return staticConfig;
	}
	private static void saveConfig(){
		try{
			staticConfig.save(new File (plugin.getDataFolder(),"players.yml"));
		}catch(IOException e){
			Koth.logger().severe("Could not save PlayerConfig to " + new File (plugin.getDataFolder(),"players.yml"));
			e.printStackTrace();
		}
	}
	private static void reloadConfig(){
		staticConfig = YamlConfiguration.loadConfiguration(new File (plugin.getDataFolder(),"players.yml"));
		//InputStream defConf = plugin.getResource("players.yml");
		//if (defConf !=null){
		//	YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defConf);
		//	staticConfig.setDefaults(defaultConfig);
		//}
	}
	private static ConfigurationSection getConfigSection(String name){
		ConfigurationSection section;
		if (!getConfig().contains(name)){
			section = getConfig().createSection(name);
//			InputStream defConf = plugin.getResource("players.yml");
//			if (defConf !=null){
//				ConfigurationSection defaultConfig = YamlConfiguration.loadConfiguration(defConf).getConfigurationSection("bladedpenguin");
//				Koth.logger().info("Configuration section default found..." + defaultConfig.getName());
//				Koth.logger().info(section.getDefaultSection().getName());
//			}
			
		}
		section = getConfig().getConfigurationSection(name);
		section.addDefault("notifyCapture", true);
		section.addDefault("notifyCapturing", true);
		section.addDefault("notifyLoss", true);
		section.addDefault("notifyLosing", true);
		section.addDefault("notifyOwnershipChange", true);
		section.addDefault("notifyPermissionsChange", true);
		section.addDefault("notifyPay", true);
		section.addDefault("notifyTick", false);
		section.addDefault("notifyAdmin", false);
		return section;
	}
	
}

	
