package tk.flygande_toalett.minecraft.usertools.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import tk.flygande_toalett.minecraft.usertools.Plugin;
import tk.flygande_toalett.minecraft.usertools.TpaRequests;

public class TpaBlock implements CommandExecutor{
	protected Plugin plugin;
	
	public TpaBlock(Plugin plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
			return true;
		}
		Player player = (Player)sender;
		TpaRequests requests = plugin.tpaRequests(player);
		
		if(args.length == 0){
			requests.block_all = !requests.block_all;
			if(requests.block_all){
				player.sendMessage(ChatColor.GOLD + "/tpa (teleport ask) requests are now blocked.");
			}else{
				player.sendMessage(ChatColor.GOLD + "/tpa (teleport ask) requests are now unblocked.");
			}
			return true;
		}else{
			for(String arg : args){
				List<Entity> targets = Bukkit.selectEntities(sender,arg);
				for(Entity target : targets){
					if(target instanceof Player){
						if(requests.block_list.contains((Player)target)){
							requests.block_list.remove((Player)target);
						}else{
							requests.block_list.add((Player)target);
						}
					}else{
						sender.sendMessage(ChatColor.RED + "Cannot block a non-player: " + target.toString());
					}
				}
			}
			return true;
		}
	}
}
