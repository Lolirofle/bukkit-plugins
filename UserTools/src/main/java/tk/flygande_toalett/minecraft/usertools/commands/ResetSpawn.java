package tk.flygande_toalett.minecraft.usertools.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import tk.flygande_toalett.minecraft.usertools.Plugin;

public class ResetSpawn implements CommandExecutor{
	protected Plugin plugin;
	
	public ResetSpawn(Plugin plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		if(args.length == 0){
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
				return true;
			}
			Player player = (Player)sender;
			player.setBedSpawnLocation(null);
			sender.sendMessage(ChatColor.GOLD + "Reset spawnpoint for " + player.getDisplayName() + ".");
			
			return true;
		}else if(args.length == 1 && sender.hasPermission("usertools.command.resetspawn.others")){			
			List<Entity> entities = Bukkit.selectEntities(sender,args[0]);
			for(Entity entity : entities){
				if(!(entity instanceof HumanEntity)){
					sender.sendMessage(ChatColor.RED + "Unable to reset spawnpoint on non-human entity " + entity + ".");
					continue;
				}
				HumanEntity player = (HumanEntity)entity;
				player.setBedSpawnLocation(null);
				sender.sendMessage(ChatColor.GOLD + "Reset spawnpoint for " + entity + ".");
			}

			return true;
		}

		return false;
	}
}
