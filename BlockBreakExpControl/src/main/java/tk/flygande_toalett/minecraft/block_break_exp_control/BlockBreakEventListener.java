package tk.flygande_toalett.minecraft.block_break_exp_control;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakEventListener implements Listener{
	protected Plugin plugin;

	public BlockBreakEventListener(Plugin plugin){
		this.plugin = plugin;
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onBlockBreak(final BlockBreakEvent event){
		Block block = event.getBlock();
		switch(block.getType()){
			case WHEAT:
			case POTATOES:
			case CARROTS:
			case NETHER_WART:
			case BEETROOTS:
			//case CACTUS: //TODO: Not sure how the age parameter works for cacti and sugar canes. Seems to not work
			//case SUGAR_CANE:
				BlockData blockData = block.getBlockData();
				if(blockData instanceof Ageable){
					Ageable age = (Ageable)blockData;

					//Bukkit.getServer().getLogger().log(Level.WARNING,age.getAge() + " , " + age.getMaximumAge());
					if(age.getAge() == age.getMaximumAge()){
						int exp = plugin.config.getExp(block.getType());
						//Bukkit.getServer().getLogger().log(Level.WARNING,": " + exp);
						if(exp>=0){
							event.setExpToDrop(exp);
						}
					}
				}
				break;
			default:
				break;
		}
	}
}
