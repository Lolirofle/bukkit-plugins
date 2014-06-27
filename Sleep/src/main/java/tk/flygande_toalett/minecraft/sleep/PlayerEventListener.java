package tk.flygande_toalett.minecraft.sleep;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerEventListener implements Listener{
	protected Plugin plugin;
	
	public PlayerEventListener(Plugin plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onPlayerBedEnterNotify(final PlayerBedEnterEvent event){
		final World world = event.getPlayer().getWorld();
		
		//Add to list of sleeping players
		List<String> sleepingInWorld = plugin.sleeping.get(world);
		if(sleepingInWorld==null)
			sleepingInWorld = new ArrayList<String>();
		if(!event.getPlayer().isSleepingIgnored()){
			sleepingInWorld.add(event.getPlayer().getName());
			plugin.sleeping.put(world,sleepingInWorld);
		}

		//Notify
		if(event.getPlayer().hasPermission("sleep.notifydown")){
			String message = event.getPlayer().getDisplayName() + ChatColor.DARK_PURPLE + " went to sleep (" + sleepingInWorld.size() + "/" + plugin.getPlayersSleepRequired(world).size() + ").";
			for(Player player : world.getPlayers())
				player.sendMessage(message);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onPlayerBedLeaveNotify(final PlayerBedLeaveEvent event){
		final World world = event.getPlayer().getWorld();

		//Remove from list of sleeping players, if existing
		List<String> sleepingInWorld = plugin.sleeping.get(world);
		int count = 0;
		if(sleepingInWorld!=null){
			sleepingInWorld.remove(event.getPlayer().getName());
			plugin.sleeping.put(world,sleepingInWorld);
			count = sleepingInWorld.size();
		}
		
		//Notify
		if(event.getPlayer().hasPermission("sleep.notifyup")){
			String message = event.getPlayer().getDisplayName() + ChatColor.DARK_PURPLE + " woke up (" + count + "/" + plugin.getPlayersSleepRequired(world).size() + ").";
			for(Player player : world.getPlayers())
				player.sendMessage(message);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onPlayerBedEnter(final PlayerBedEnterEvent event){
		//Check if player is allowed to sleep
		if(!event.getPlayer().hasPermission("sleep.allowed")){
			event.setCancelled(true);
			return;
		}
		
		//Update ignore sleep for all players in current world (TODO: Expensive calls?)
		for(Player player : event.getPlayer().getWorld().getPlayers())
			player.setSleepingIgnored(player.hasPermission("sleep.ignored") || (player.hasPermission("sleep.depthCheck") && player.getEyeLocation().getY()<plugin.config.getDepth(event.getPlayer().getWorld().getName()) && event.getPlayer().getLocation().getBlock().getLightFromSky()==0));
	}
}
