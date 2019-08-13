package tk.flygande_toalett.minecraft.deathinventorychest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener{
	private HashSet<Material> blockReplacable;

	public PlayerDeathListener(Plugin plugin){
		this.blockReplacable = new HashSet<Material>();
		this.blockReplacable.add(Material.AIR);
		this.blockReplacable.add(Material.WATER);
		this.blockReplacable.add(Material.LAVA);
		this.blockReplacable.add(Material.STONE);
		this.blockReplacable.add(Material.SAND);
		this.blockReplacable.add(Material.GRAVEL);
		this.blockReplacable.add(Material.CAVE_AIR);
		this.blockReplacable.add(Material.COBWEB);
		this.blockReplacable.add(Material.DIRT);
		this.blockReplacable.add(Material.GRASS);
	}

	/**
	 * Determines whether the position is valid for chest placement
	 * @param world
	 * @param pos
	 * @return Returns the block at the given position if valid, null elsewise
	 */
	private Block isValidChestPosition(World world,int x,int y,int z){
		Block block = world.getBlockAt(x,y,z);

		if(blockReplacable.contains(block.getType())){
			return block;
		}else{
			return null;
		}
	}
	private Block isValidChestPosition(Location pos){
		return isValidChestPosition(pos.getWorld(),pos.getBlockX(),pos.getBlockY(),pos.getBlockZ());
	}

	private Block isValidChestPositionAround(World world,int x,int y,int z){
		Block block = null;

		block = isValidChestPosition(world,x,y,z);
		if(block!=null){return block;}


		block = isValidChestPosition(world,x+1,y,z);
		if(block!=null){return block;}

		block = isValidChestPosition(world,x,y,z+1);
		if(block!=null){return block;}

		block = isValidChestPosition(world,x-1,y,z);
		if(block!=null){return block;}

		block = isValidChestPosition(world,x,y,z-1);
		if(block!=null){return block;}


		block = isValidChestPosition(world,x-1,y,z-1);
		if(block!=null){return block;}

		block = isValidChestPosition(world,x+1,y,z-1);
		if(block!=null){return block;}

		block = isValidChestPosition(world,x-1,y,z+1);
		if(block!=null){return block;}

		block = isValidChestPosition(world,x+1,y,z+1);
		if(block!=null){return block;}


		return block;
	}
	private Block isValidChestPositionAround(Location pos){
		return isValidChestPositionAround(pos.getWorld(),pos.getBlockX(),pos.getBlockY(),pos.getBlockZ());
	}

	/**
	 * Check if something is in the way of placing a chest, and try to resolve it by trying the blocks above/below
	 * @param world
	 * @param targetPos
	 * @return
	 */
	private Block findValidChestPosition(World world,int x,int y,int z){
		Block block;
		int minY = 0;
		int maxY = world.getMaxHeight()-1;

		if(y>maxY){//If higher up than max height, try go down
			y = maxY-1;
			for(; y>minY; y--){//TODO: Is add mutating already?
				//If the block at the position is okay to replace, then cancel the search for a position
				block = isValidChestPositionAround(world,x,y,z);
				if(block!=null){return block;}
			}
		}else{//Try go up
			if(y<minY){y = minY;}
			for(; y<maxY; y++){//TODO: Is add mutating already?
				//If the block at the position is okay to replace, then cancel the search for a position
				block = isValidChestPositionAround(world,x,y,z);
				if(block!=null){return block;}
			}
		}

		//No valid position was found if the FindBlock "loop" was not broken out of
		return null;
	}
	private Block findValidChestPosition(World world,Location originPos){
		return findValidChestPosition(originPos.getWorld(),originPos.getBlockX(),originPos.getBlockY(),originPos.getBlockZ());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent event){
		//If the entity that died is not a player, do nothing
		if(!(event.getEntity() instanceof Player)){return;}
		Player player = (Player)event.getEntity();

		//If the player does not have the permission to spawn chests upon death, do nothing
		if(!player.hasPermission("deathinventorychest.spawnchest")){return;}

		//Get the drops
		List<ItemStack> drops = event.getDrops();
		int dropsSize = drops.size();

		//If nothing is dropped (e.g. when the inventory is empty), do nothing
		if(dropsSize==0){return;}

		//Place the chests depending on how many items and chests the player dropped (from its inventory)
		//Note: This process will mutate dropsSize, and the resulting value will indicate how many items which did not fit inside the placed chests
		List<Chest> placedChests = new ArrayList<Chest>();
		{
			//Look for chests among the drops
			Iterator<ItemStack> iter = drops.iterator();
			DroppedChestSearchLoop: while(iter.hasNext()){
				ItemStack item = iter.next();

				//If the item drop is a chest
				if(item!=null && item.getType()==Material.CHEST){
					//Place chests until there is enough for storing all drops
					PlaceLoop: while(true){
						//The item drop is a stack. If the stack is empty, remove it, and stop the repeating chests placing process
						if(item.getAmount()<=0){
							iter.remove();
							break PlaceLoop;
						}

						//Hypothetically, if drops were placed inside the already placed chests, how many drops will still be drops left
						//In other words: If there already are enough placed chests, then stop looking for more chests
						if(dropsSize<=0){break DroppedChestSearchLoop;}

						//Find a valid block near the player's death location to replace with a chest, stop looking for more chests if a position is not found
						Block block = this.findValidChestPosition(player.getWorld(),player.getLocation());
						if(block==null){break DroppedChestSearchLoop;}

						//Set the chosen block to a chest, and add its state as chest to the list of chests placed
						block.setType(Material.CHEST);
						Chest chest = (Chest)block.getState();
						placedChests.add(chest);

						//Hypothetically, if the drops were placed inside the chest, dropsSize is now how many drops that are left
						dropsSize-= chest.getBlockInventory().getSize();

						//Subtract one chest from the dropped chest item stack
						item.setAmount(item.getAmount()-1);
					}
				}
			}
		}

		//If no chests were successfully placed, then do the usual
		if(placedChests.isEmpty()){
			player.sendMessage(ChatColor.WHITE + "Dropping inventory on death" + ChatColor.GRAY + " (No chests were found among the dropped items)");
			return;
		}

		//Place the item drops in the placed chests
		{
			ChestLoop: for(Chest chest : placedChests){
				//For each drop (from the player's inventory)
				Iterator<ItemStack> dropsIter = drops.iterator();
				ChestAddItemLoop: while(true){
					//If there are no more drops, then stop placing items in chests
					if(!dropsIter.hasNext()){break ChestLoop;}

					ItemStack item = dropsIter.next();
					if(item!=null){
						//If the item stack was successfully added to the chest
						if(chest.getInventory().addItem(item).isEmpty()){
							//Remove the dropped item stack
							dropsIter.remove();
						}else{
							//When not successful, then the chest is probably full, so stop adding items to this chest
							break ChestAddItemLoop;
						}
					}
				}
			}
		}

		player.sendMessage(ChatColor.WHITE + "Stored inventory in " + placedChests.size() + " chests on death" + (dropsSize>0? ChatColor.GRAY + "(" + dropsSize + " items were dropped)" : ""));
	}
}
