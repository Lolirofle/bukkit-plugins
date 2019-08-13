package tk.flygande_toalett.minecraft.usertools.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Suicide implements CommandExecutor{
	public Suicide(){}

	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
			return true;
		}
		
		if(args.length>0){
			return false;
		}

		Player player = (Player)sender;
		player.setHealth(0.0);
		return true;
	}
}
