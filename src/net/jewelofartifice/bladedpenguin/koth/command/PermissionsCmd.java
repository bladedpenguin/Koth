package net.jewelofartifice.bladedpenguin.koth.command;

import net.jewelofartifice.bladedpenguin.koth.Koth;
import net.jewelofartifice.bladedpenguin.koth.hilltop.Hilltop;
import com.massivecraft.mcore1.cmd.VisibilityMode;
import com.massivecraft.mcore1.cmd.req.ReqHasPerm;

public class PermissionsCmd extends KothCmd {

	PermissionsCmd(){
		super();
		this.addAliases("permissions");
		this.addAliases("permission");
		this.addAliases("perms");
		this.addAliases("perm");
		requiredArgs.add("hilltop");
		//optionalArgs.put("query", "");
		setDesc("List permissions for a hilltop");
		this.setVisibilityMode(VisibilityMode.SECRET);
		
		
		//IIUC, the perms belong in individual commands, and don't need to be anywhere else 
		this.setDescPermission("koth.permission");
		this.addRequirements(new ReqHasPerm("koth.permission"));
	}
	@Override
	public void perform(){
		Hilltop h = Koth.k().hm.getHilltop(arg(0));
		if (h == null){
			msg("That's not a real Hilltop!");
			return;
		}
		msg("Group: " + h.getGroup());
		//I don't think permissions will be handed out individually
	}
}
