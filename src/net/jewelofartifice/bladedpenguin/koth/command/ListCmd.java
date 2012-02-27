package net.jewelofartifice.bladedpenguin.koth.command;

import net.jewelofartifice.bladedpenguin.koth.Koth;
import net.jewelofartifice.bladedpenguin.koth.hilltop.Hilltop;

import com.massivecraft.mcore1.cmd.VisibilityMode;
import com.massivecraft.mcore1.cmd.req.ReqHasPerm;

public class ListCmd extends KothCmd{
	ListCmd(){
		super();
		this.addAliases("list");
		optionalArgs.put("query", "");
		setDesc("List available Hilltops");
		this.setVisibilityMode(VisibilityMode.SECRET);
		
		
		//IIUC, the perms belong in individual commands, and don't need to be anywhere else 
		this.setDescPermission("koth.list");
		this.addRequirements(new ReqHasPerm("koth.list"));
	}
	@Override
	public void perform(){
		for (Hilltop h : Koth.k().hm.getHilltops()){
			this.msg(h.getName() + " : " + h.getOwner().getName());
		}
	}
}
