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

public class TpaHere implements CommandExecutor{
	protected Plugin plugin;
	
	public TpaHere(Plugin plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
			return true;
		}
		Player requester = (Player)sender;
		
		if(args.length!=1){
			return false;
		}		

		List<Entity> targets = Bukkit.selectEntities(sender,args[0]);
		if(targets.isEmpty()){
			requester.sendMessage(ChatColor.RED + "Cannot teleport non-existing players.");
			return true;
		}

		for(Entity target : targets){
			if(target instanceof Player){
				Player player = (Player)target;

				if(!player.isOnline()){
					requester.sendMessage(ChatColor.RED + "Cannot teleport offline player " + player.getDisplayName());
				}else{
					this.request(requester,player);
				}
			}else{
				requester.sendMessage(ChatColor.RED + "Cannot request a non-player to teleport: " + target.toString());
			}
		}

		return true;
	}

	protected void request(Player requester,Player target){		
		//Add to request list
		TpaRequests requests = this.plugin.tpaRequests(target);

		if(!requests.request(TpaRequests.Type.HERE,requester)){
			requester.sendMessage(target.getDisplayName() + ChatColor.RED + "have blocked teleportation requests.");
		}else{
			//Send to player
			requester.sendMessage(ChatColor.GOLD + "Sent teleportation request (tpahere) to " + target.getDisplayName() + ", waiting for response...");
			
			//Send to target player
			target.sendMessage(ChatColor.GOLD + requester.getDisplayName() + " has requested that you teleport to them.");
			target.sendMessage(ChatColor.GOLD + "To accept, type " + ChatColor.RED + "/tpaccept");
			target.sendMessage(ChatColor.GOLD + "To refuse, type " + ChatColor.RED + "/tpdeny");
		}
	}
}
