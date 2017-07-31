package tk.flygande_toalett.minecraft.deathinventorychest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener{
	private Plugin plugin;
	private HashSet<Material> blockUnreplacable;

	public PlayerDeathListener(Plugin plugin){
		this.plugin = plugin;
		this.blockUnreplacable = new HashSet<Material>();
	}
	
	public PlayerDeathListener(Plugin plugin,HashSet<Material> blockUnreplacable){
		this.plugin = plugin;
		this.blockUnreplacable = blockUnreplacable;
	}

	/**
	 * Determines whether the position is valid for chest placement 
	 * @param world
	 * @param pos
	 * @return Returns the block at the given position if valid, null elsewise
	 */
	private Block isValidChestPosition(World world,Location pos){
		Block block = world.getBlockAt(pos.getBlockX(),pos.getBlockY(),pos.getBlockZ());
		//TODO: Check if surrounding area is okay (If there's another chest interfering, ignoring accompaniedChestPos)
		//If the block at the position is okay to replace
		if(blockUnreplacable.contains(block.getType())){
			return null;
		}
		return block;
	}

	/**
	 * Determines whether the position is valid for placement of a second chest 
	 * @param world
	 * @param pos
	 * @param firstChestPos
	 * @return Returns the block at the given position if valid, null elsewise
	 */
	private Block isValidChestPosition(World world,Location pos,Location firstChestPos){
		return null;
	}
	
	private Block findValidChestPosition(World world,Location targetPos){
		Block block;
		int minY=0;
		int maxY=world.getMaxHeight()-1;

		if(targetPos.getBlockY()>maxY){//If higher up than max height, try go down
			targetPos.setY(maxY);
			for(; targetPos.getBlockY()>minY; targetPos=targetPos.add(0,-1,0)){//TODO: Is add mutating already?
				//If the block at the position is okay to replace, then cancel the search for a position
				block = isValidChestPosition(world,targetPos);
				if(block!=null){return block;}
			}
		}else{//Try go up
			if(targetPos.getBlockY()<minY){targetPos.setY(minY);}
			for(; targetPos.getBlockY()<maxY; targetPos=targetPos.add(0,1,0)){//TODO: Is add mutating already?
				//If the block at the position is okay to replace, then cancel the search for a position
				block = isValidChestPosition(world,targetPos);
				if(block!=null){return block;}
			}
		}
		//No valid position was found if the FindBlock "loop" was not broken out of
		return null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent event){
		//If the entity that died is a player
		if(event.getEntity() instanceof Player){
			Player player = (Player)event.getEntity();

			if(player.hasPermission("deathinventorychest.spawnchest")){
				List<ItemStack> drops = event.getDrops();

				//If nothing is dropped (from the inventory), then nothing needs to be done
				if(drops.size()==0){
					return;
				}

				//Check if the player has chest(s) in its inventory
				int chestsInInventory = 0;
				for(ItemStack item : drops){
					if(item!=null && item.getType()==Material.CHEST){
						chestsInInventory+= item.getAmount();
					}
				}
				if(chestsInInventory==0){
					player.sendMessage(ChatColor.WHITE + "Dropping inventory on death + ChatColor.GRAY + (No chests was found in inventory)");
					return;
				}

				//Check if something is in the way at the player's death location, and try to resolve it by trying the blocks above/below
				//TODO: Check for a valid location for the second chest (above/below or next to)
				Iterator<ItemStack> dropsIter = event.getDrops().listIterator();
				int slot = 0;
				do{if(chestsInInventory==0){break;}else{chestsInInventory-= 1;}
					Block block = this.findValidChestPosition(player.getWorld(),player.getLocation());
					if(block==null){return;}
	
					//Set the chosen block to a chest
					block.setType(Material.CHEST);
	
					//Set the chest data
					BlockState state = block.getState();
					Chest chest = (Chest)state.getData();
					
					int maxSlot = chest.getInventory().getSize();
					
					//For each drop from the player's inventory
					while(dropsIter.hasNext()){
						ItemStack item = dropsIter.next();
						if(item==null)continue;

						//Take the chest(s)
						if(item.getType()==Material.CHEST){
							if (item.getAmount() >= removeChestCount) {
								item.setAmount(item.getAmount() - removeChestCount);
								removeChestCount = 0;
							} else {
								removeChestCount -= item.getAmount();
								item.setAmount(0);
							}
							if (item.getAmount() == 0) {
								iter.remove();
								continue;
							}
						}
			
						// Add items to chest if not full.
						if (slot < maxSlot) {
							if (slot >= sChest.getInventory().getSize()) {
								if (lChest == null) continue;
								lChest.getInventory().setItem(slot % sChest.getInventory().getSize(), item);
							} else {
								sChest.getInventory().setItem(slot, item);
							}
							iter.remove();
							slot++;
						} else if (removeChestCount == 0) break;
					}
				}while(drops.size() > maxSlot);
				//if(player.hasPermission("deathinventorychest.consumechest"))
			}
		}
	}
}
