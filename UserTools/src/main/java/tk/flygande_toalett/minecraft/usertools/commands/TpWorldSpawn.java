package tk.flygande_toalett.minecraft.usertools.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import tk.flygande_toalett.minecraft.usertools.Plugin;

public class TpWorldSpawn implements CommandExecutor{
	protected Plugin plugin;
	
	public TpWorldSpawn(Plugin plugin){
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
			World world = player.getWorld();

			player.teleport(world.getSpawnLocation());
			
			return true;
		}else if(args.length == 1 && sender.hasPermission("usertools.command.tpspawn.worlds")){
			World world = plugin.getServer().getWorld(args[0]);
			if(world == null){
				sender.sendMessage(ChatColor.RED + "World " + args[0] + " does not exist.");
				return true;
			}
			
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
				return true;
			}
			Player player = (Player)sender;

			player.teleport(world.getSpawnLocation());

			return true;
		}else if(args.length == 2 && sender.hasPermission("usertools.command.tpspawn.others")){
			World world = plugin.getServer().getWorld(args[0]);
			if(world == null){
				sender.sendMessage(ChatColor.RED + "World " + args[0] + " does not exist.");
				return true;
			}
			
			List<Entity> entities = Bukkit.selectEntities(sender,args[1]);
			for(Entity entity : entities){
				if(entity.teleport(world.getSpawnLocation())){
					sender.sendMessage(ChatColor.GOLD + "Teleported entity " + entity.toString() + " to spawn.");
				}else{
					sender.sendMessage(ChatColor.GOLD + "Unable to teleport entity " + entity.toString() + " to spawn.");
				}
			}

			return true;
		}

		return false;
	}
}
