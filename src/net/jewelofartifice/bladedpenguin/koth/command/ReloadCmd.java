package net.jewelofartifice.bladedpenguin.koth.command;

import com.massivecraft.mcore1.cmd.VisibilityMode;
import com.massivecraft.mcore1.cmd.req.ReqHasPerm;

import net.jewelofartifice.bladedpenguin.koth.Koth;

public class ReloadCmd extends KothCmd {
	ReloadCmd(){
		super();
		this.addAliases("reload");
		setDesc("Reload config, hilltops, and teams");
		this.setVisibilityMode(VisibilityMode.SECRET);
		//IIUC, the perms belong in individual commands, and don't need to be anywhere else 
		this.setDescPermission("koth.reload");
		this.addRequirements(new ReqHasPerm("koth.reload"));	
	}
	@Override
	public void perform(){
		Koth.k().reload();
	}
}
