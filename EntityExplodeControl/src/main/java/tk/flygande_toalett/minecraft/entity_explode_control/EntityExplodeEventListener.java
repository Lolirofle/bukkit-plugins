package tk.flygande_toalett.minecraft.entity_explode_control;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeEventListener implements Listener{
	protected Plugin plugin;

	public EntityExplodeEventListener(Plugin plugin){
		this.plugin = plugin;
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onEntityExplode(final EntityExplodeEvent event){
		event.setYield(1.0f);

		EntityType entityType = event.getEntityType(); 
		if(entityType == EntityType.CREEPER
		|| entityType == EntityType.ENDER_DRAGON
		){
			Iterator<Block> i = event.blockList().iterator();
			while(i.hasNext()){
				Block block = i.next();
				Material blockType = block.getType(); 
				if(blockType == Material.CHEST
				|| blockType == Material.ENDER_CHEST
				){
					i.remove();
				}
			}
		}
	}
}
