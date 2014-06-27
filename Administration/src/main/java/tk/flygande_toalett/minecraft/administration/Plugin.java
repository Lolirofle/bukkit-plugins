package tk.flygande_toalett.minecraft.administration;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin{		
	@Override
	public void onEnable(){
		//this.getServer().getPluginManager().registerEvents(new PlayerEventListener(this),this);
	}
	
	@Override
	public void onDisable(){		
		for(Player player : this.getServer().getOnlinePlayers())
			player.setSleepingIgnored(false);
	}
	
	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		//if(command.getName().equalsIgnoreCase("sleeping"))
		return false;
	}
}
