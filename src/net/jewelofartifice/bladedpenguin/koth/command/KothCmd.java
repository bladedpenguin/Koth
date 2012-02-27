package net.jewelofartifice.bladedpenguin.koth.command;

import net.jewelofartifice.bladedpenguin.koth.Koth;

import com.massivecraft.mcore1.MPlugin;
import com.massivecraft.mcore1.cmd.MCommand;

public class KothCmd extends MCommand{
	Koth plugin;
	
	KothCmd(){
		super();
		plugin = Koth.k();
	}
	@Override
	public MPlugin p() { //fuck this I think; it doesn't do shit. if at a later point it is foudn to do shit, I guess I'll refactor
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void perform() {
		// TODO Auto-generated method stub

	}

}
