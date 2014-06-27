package tk.flygande_toalett.minecraft.loginregister;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;

public class PlayerEventsHandler implements Listener{
	protected Plugin plugin;
	
	public PlayerEventsHandler(Plugin plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event){
		if(event.getPlayer().hasPermission("loginregister.enable") && (event.getPlayer().hasPermission("loginregister.requireRegistration") || plugin.database.hasLogin(event.getPlayer().getName()))){
			plugin.playersNotLoggedIn.add(event.getPlayer().getName());
			event.getPlayer().sendMessage(ChatColor.RED + "Registered user. Please login using /login <password>");
		}
	}
	
	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event){
		plugin.playersNotLoggedIn.remove(event.getPlayer().getName());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName())){
			if(!event.getMessage().startsWith("/register ") && !event.getMessage().startsWith("/login ")){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "Please login using /login <password>");
			}
		}
	}
	
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerBedEnter(final PlayerBedEnterEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerBucketFill(final PlayerBucketFillEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerDropItem(final PlayerDropItemEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerEditBook(final PlayerEditBookEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerFish(final PlayerFishEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	/*@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerGameModeChange(final PlayerGameModeChangeEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}*/
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteractEntity(final PlayerInteractEntityEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteract(final PlayerInteractEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerItemConsume(final PlayerItemConsumeEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerItemHeld(final PlayerItemHeldEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerMove(final PlayerMoveEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerPortal(final PlayerPortalEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	/*@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerShearEntity(final PlayerShearEntityEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerTeleport(final PlayerTeleportEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerToggleFlight(final PlayerToggleFlightEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerToggleSneak(final PlayerToggleSneakEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerToggleSprint(final PlayerToggleSprintEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}*/
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerUnleashEntity(final PlayerUnleashEntityEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerVelocity(final PlayerVelocityEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	/*@EventHandler(priority=EventPriority.HIGHEST)
	public void onEnchantItem(final EnchantItemEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getEnchanter().getName()))
			event.setCancelled(true);
	}*/
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryClick(final InventoryClickEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getWhoClicked().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryDrag(final InventoryDragEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getWhoClicked().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInventoryOpen(final InventoryOpenEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	/*@EventHandler(priority=EventPriority.HIGHEST)
	public void onPrepareItemEnchant(final PrepareItemEnchantEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getEnchanter().getName()))
			event.setCancelled(true);
	}*/
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockDamage(final BlockDamageEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPlace(final BlockPlaceEvent event){
		if(plugin.playersNotLoggedIn.contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityDamage(final EntityDamageEvent event){
		if(event.getEntityType()==EntityType.PLAYER && plugin.playersNotLoggedIn.contains(((Player)event.getEntity()).getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onFoodLevelChange(final FoodLevelChangeEvent event){
		if(event.getEntityType()==EntityType.PLAYER && plugin.playersNotLoggedIn.contains(((Player)event.getEntity()).getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityTarget(final EntityTargetEvent event){
		final Entity target = event.getTarget();
		if(target!=null && target.getType()==EntityType.PLAYER && plugin.playersNotLoggedIn.contains(((Player)target).getName()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEntityRegainHealth(final EntityRegainHealthEvent event){
		if(event.getEntityType()==EntityType.PLAYER && plugin.playersNotLoggedIn.contains(((Player)event.getEntity()).getName()))
			event.setCancelled(true);
	}
}
