package tk.flygande_toalett.minecraft.usertools.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.flygande_toalett.minecraft.usertools.Plugin;
import tk.flygande_toalett.minecraft.usertools.TpaRequests;
import tk.flygande_toalett.minecraft.usertools.TpaRequests.Response;

public class TpAccept implements CommandExecutor{
	protected Plugin plugin;
	
	public TpAccept(Plugin plugin){
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
		
		if(args.length==0){
			Response response = requests.accept(player);
			if(response == null){
				player.sendMessage(ChatColor.RED + "There are no teleportation requests");
				return true;
			}

			Player requester = response.request.requester;
			Player target = response.target;
			
			if(response.status){
				requester.sendMessage(ChatColor.GOLD + "Teleportation request to " + target.getDisplayName() + " accepted");
				target.sendMessage(ChatColor.GOLD + "Accepted teleportation request from " + requester.getDisplayName());
			}else{
				requester.sendMessage(ChatColor.RED + "Teleportation to " + target.getDisplayName() + " is not possible");
				target.sendMessage(ChatColor.RED + "Teleporting " + requester.getDisplayName() + " is not possible");				
			}

			return true;
		}

		return false;
	}
}
