package tk.flygande_toalett.minecraft.usertools.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tk.flygande_toalett.minecraft.usertools.Plugin;
import tk.flygande_toalett.minecraft.usertools.TpaRequests;
import tk.flygande_toalett.minecraft.usertools.TpaRequests.Response;

public class TpDeny implements CommandExecutor{
	protected Plugin plugin;
	
	public TpDeny(Plugin plugin){
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
			Response response = requests.deny(player);
			if(response == null){
				player.sendMessage(ChatColor.RED + "There are no teleportation requests");
				return true;
			}

			Player requester = response.request.requester;
			Player target = response.target;

			requester.sendMessage(ChatColor.GOLD + "Teleportation request to " + target.getDisplayName() + " denied");
			target.sendMessage(ChatColor.GOLD + "Denied teleportation request from " + requester.getDisplayName());
			
			return true;
		}

		return false;
	}
}
