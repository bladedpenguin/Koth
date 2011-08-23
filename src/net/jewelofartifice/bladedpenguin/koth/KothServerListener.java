package net.jewelofartifice.bladedpenguin.koth;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.iConomy.iConomy;

public class KothServerListener extends ServerListener{
	Koth plugin;
	KothServerListener (Koth p){
		plugin = p;
	}
	@Override
    public void onPluginDisable(PluginDisableEvent event) {
        if (plugin.em.iconomy != null) {
            if (event.getPlugin().getDescription().getName().equals("iConomy")) {
                plugin.em.iconomy = null;
                System.out.println("KOTH un-hooked from iConomy.");
            }
        }
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        if (plugin.em.iconomy == null) {
            Plugin em = plugin.getServer().getPluginManager().getPlugin("iConomy");

            if (em != null) {
                if (em.isEnabled() && em.getClass().getName().equals("com.iConomy.iConomy")) {
                    plugin.em.iconomy = (iConomy) em;
                    System.out.println("KOTH hooked into iConomy.");
                }
            }
        }
    }
}
