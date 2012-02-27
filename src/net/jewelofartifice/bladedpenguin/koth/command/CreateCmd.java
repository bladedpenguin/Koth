package net.jewelofartifice.bladedpenguin.koth.command;

import org.bukkit.World;

import net.jewelofartifice.bladedpenguin.koth.Koth;
import net.jewelofartifice.bladedpenguin.koth.hilltop.Hilltop;

import com.massivecraft.mcore1.cmd.VisibilityMode;
import com.massivecraft.mcore1.cmd.req.ReqHasPerm;

public class CreateCmd extends KothCmd{
	CreateCmd(){
		super();
		this.addAliases("create");
		this.addAliases("add");
		requiredArgs.add("hillname");
		optionalArgs.put("type", "worldguard"); //worldguard region or "FChunk" or whatever
		optionalArgs.put("world", "");
		setDesc("Create a hilltop");
		this.setVisibilityMode(VisibilityMode.SECRET);
		
		
		//IIUC, the perms belong in individual commands, and don't need to be anywhere else 
		this.setDescPermission("koth.create");
		this.addRequirements(new ReqHasPerm("koth.create"));	
	}
	@Override
	public void perform()
	{
		World w = Koth.k().getServer().getWorlds().get(0); //if nothing else comes, up, use the default world.
		World w2 = Koth.k().getServer().getWorld(arg(2)); 
		if (w2!=null){
			w = w2;
		}else if (me != null){
			w = me.getWorld();
		}
		Hilltop h = Koth.k().hm.getHilltop(this.arg(0));
		if (h != null){
			msg("A Hilltop with that name already exists");
		}
		if (arg(1).equalsIgnoreCase("WorldGuard")){
			Koth.k().hm.createHilltop(arg(0),w);
		} else if (arg(1).equalsIgnoreCase("FChunk")){
			msg("Frack You!");
		}
	}

}
