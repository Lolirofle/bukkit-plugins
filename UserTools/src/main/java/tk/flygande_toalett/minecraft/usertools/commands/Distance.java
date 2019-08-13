package tk.flygande_toalett.minecraft.usertools.commands;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;

public class Distance implements CommandExecutor{
	public static final int CHUNK_SIZE = 16;
	
	protected Plugin plugin;
	
	public Distance(Plugin plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
			return true;
		}
		Player player = (Player)sender;

		BlockIterator targetBlockScanner = new BlockIterator(player,this.plugin.getServer().getViewDistance()*CHUNK_SIZE);
		while(targetBlockScanner.hasNext()){
			final Block targetBlock = targetBlockScanner.next();
			if(!targetBlock.getType().equals(Material.AIR)){
				final Location targetBlockPos = targetBlock.getLocation();
				final double distance = player.getLocation().distance(targetBlockPos);
				sender.sendMessage(ChatColor.GOLD + "Distance: " + ChatColor.RED + (new DecimalFormat("#.##")).format(distance) + ChatColor.GRAY + " (" + targetBlock.getType().toString() + " at ("+targetBlockPos.getBlockX()+","+targetBlockPos.getBlockY()+","+targetBlockPos.getBlockZ()+"))");
				
				return true;
			}
		}
			
		sender.sendMessage(ChatColor.GOLD + "Distance: " + ChatColor.RED + " ??? " + ChatColor.GRAY + " (Too far away)");
		return true;
	}
}
