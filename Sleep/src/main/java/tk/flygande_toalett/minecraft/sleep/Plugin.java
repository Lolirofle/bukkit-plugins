package tk.flygande_toalett.minecraft.sleep;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin{
	public Config config;
	protected HashMap<World,List<String>> sleeping;
	
	public Plugin(){
		sleeping = new HashMap<World,List<String>>();
	}
	
	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(new PlayerEventListener(this),this);
		config = new Config(this.getConfig());
	}
	
	@Override
	public void onDisable(){		
		for(Player player : this.getServer().getOnlinePlayers())
			player.setSleepingIgnored(false);
		
		config.finish();
		this.saveConfig();
	}
	
	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		if(command.getName().equalsIgnoreCase("sleeping")){
			if(sender instanceof Player){
				final World world = ((Player)sender).getWorld();

				int count = 0;
				StringBuilder builder = new StringBuilder();

				List<String> sleepingInWorld = sleeping.get(world);
				if(sleepingInWorld!=null){
					for(String playerName : sleepingInWorld){
						builder.append(playerName);
						builder.append(",");
						count++;
					}
				}
				
				sender.sendMessage(ChatColor.DARK_PURPLE + "Sleeping in " + world.getName() + " (" + count + "/" + getPlayersSleepRequired(world).size() + "): " + builder.toString());
				
				return true;
			}
		}else if(command.getName().equalsIgnoreCase("notsleeping")){
			if(sender instanceof Player){
				final World world = ((Player)sender).getWorld();

				StringBuilder builder = new StringBuilder();

				for(Player player : getPlayersNotSleeping(world)){
					builder.append(player.getDisplayName());
					builder.append(",");
				}
				
				sender.sendMessage(ChatColor.DARK_PURPLE + "Not sleeping in " + world.getName() + ": " + builder.toString());
				
				return true;
			}
		}
		return false;
	}
	
	List<Player> getPlayersSleepRequired(World world){
		LinkedList<Player> list = new LinkedList<Player>();
		for(Player player : world.getPlayers())
			if(!player.isSleepingIgnored())
				list.add(player);
		return list;
	}
	
	List<Player> getPlayersNotSleeping(World world){
		LinkedList<Player> list = new LinkedList<Player>();
		for(Player player : world.getPlayers())
			if(!player.isSleepingIgnored() && !player.isSleeping())
				list.add(player);
		return list;
	}
}
