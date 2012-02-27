package net.jewelofartifice.bladedpenguin.koth.command;

import net.jewelofartifice.bladedpenguin.koth.Koth;
import net.jewelofartifice.bladedpenguin.koth.team.Team;

import com.massivecraft.mcore1.cmd.VisibilityMode;
import com.massivecraft.mcore1.cmd.req.ReqHasPerm;

public class TeamsCmd extends KothCmd {
	TeamsCmd(){
		super();
		this.addAliases("teams");
		this.addAliases("team");
		this.addAliases("t");
		//requiredArgs.add("hilltop");
		optionalArgs.put("team", ""); //if present, gives that teams details
		setDesc("List teams");
		this.setVisibilityMode(VisibilityMode.VISIBLE);
		
		
		//IIUC, the perms belong in individual commands, and don't need to be anywhere else 
		this.setDescPermission("koth.team");
		this.addRequirements(new ReqHasPerm("koth.team"));
	}
	public void perform(){
		Team t = Koth.k().tm.getTeam(arg(0));
		if (t == null){
			msg("Koth Teams: ");
			for (Team team : Koth.k().tm.getTeams()){
				msg(team.getName());
			}
		} else {
			msg("Yes, there is a team called " + t.getName() + " and they own: ");
			for(String s : t.getGroups()){
				msg(s);
			}
		}
	}
}
