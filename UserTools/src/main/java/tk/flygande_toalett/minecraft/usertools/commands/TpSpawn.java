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

public class TpSpawn implements CommandExecutor{
	protected Plugin plugin;
	
	public TpSpawn(Plugin plugin){
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

			player.teleport(player.getBedSpawnLocation());
			
			return true;
		}else if(args.length == 1 && sender.hasPermission("usertools.command.tpspawn.others")){			
			List<Entity> entities = Bukkit.selectEntities(sender,args[0]);
			for(Entity entity : entities){
				if(!(entity instanceof HumanEntity)){
					sender.sendMessage(ChatColor.RED + "Unable to teleport non-player " + entity + " to its spawn location.");
					continue;
				}
				HumanEntity player = (HumanEntity)entity;

				if(player.teleport(player.getBedSpawnLocation())){
					sender.sendMessage(ChatColor.GOLD + "Teleported entity " + player.toString() + " to its spawnpoint.");
				}else{
					sender.sendMessage(ChatColor.GOLD + "Unable to teleport entity " + player.toString() + " to its spawnpoint.");
				}
			}

			return true;
		}

		return false;
	}
}
