package tk.flygande_toalett.minecraft.itemlore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jdt.annotation.NonNull;

import tk.flygande_toalett.minecraft.itemlore.Util.IteratorIterable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class Plugin extends JavaPlugin implements Listener{
	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(this,this);
	}

	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,String[] raw_args){
		if(command.getName().equalsIgnoreCase("lore")){
			if(raw_args.length == 0){
				sendUsage(sender);
				return true;
			}

			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "This command must be run by a player.");
				return true;
			}

			ItemStack stack = ((Player)sender).getInventory().getItemInMainHand();
			if(stack == null){
				sender.sendMessage(ChatColor.RED + "This command requires an item held in your hand.");
				return true;
			}

			//Reparse command arguments because Bukkit does not do it at the time of writing (2019-05-02)
			final ArrayList<String> args;{
				StringBuilder str = new StringBuilder();
				for(String arg : raw_args){
					str.append(arg);
					str.append(' ');
				}

				args = Util.nullorDefault(new Parser.CommandArgs(raw_args.length).parse(str),new ArrayList<String>());
			}

			if(args.get(0).equalsIgnoreCase("set")){
				if(!sender.hasPermission("itemlore.command.lore.set")){
					return false;
				}

				switch(args.size()){
					case 1:
						break;
					case 2:{
						itemStackModifyLore(stack,
							(lore -> {
								this.getServer().getConsoleSender().sendMessage("Lore of " + stack.toString() + " before change: " + Util.stringJoin(lore.iterator(),"\n").toString());
								lore.clear();
								lore.add(args.get(1));
							})
						);
						return true;
					}
					case 3:{
						int line;
						try{
							line = Integer.parseInt(args.get(1));
						}catch(NumberFormatException e){
							break;
						}
						
						itemStackModifyLore(stack,
							(lore -> {
								if(lore.size() == 0){
									lore.add(args.get(2));
								}else{
									int line2 = Math.floorMod(line,lore.size());
									this.getServer().getConsoleSender().sendMessage("Lore " + line2 + " of " + stack.toString() + " before change: " + lore.get(line2));
									lore.set(line2,args.get(2));
								}
							})
						);
						return true;
					}
					default:
						itemStackModifyLore(stack,
							(lore -> {
								lore.clear();
								for(String arg : new Util.IteratorIterable<String>(Util.iteratorSkip(args.iterator(),1))){
									lore.add(arg);
								}
							})
						);
						return true;
				}
				
				sendSetUsage(sender);
				return true;
			}else if(args.get(0).equalsIgnoreCase("add")){
				if(!sender.hasPermission("itemlore.command.lore.add")){
					return false;
				}

				switch(args.size()){
					case 2:
						itemStackModifyLore(stack,
							(lore -> {
								lore.add(args.get(1));
							})
						);
						return true;
					case 3:
						int line;
						try{
							line = Integer.parseInt(args.get(1));
						}catch(NumberFormatException e){
							break;
						}
						
						itemStackModifyLore(stack,
							(lore -> {
								if(lore.size() == 0){
									lore.add(args.get(2));
								}else{
									lore.add(Math.floorMod(line,lore.size()),args.get(2));
								}
							})
						);
						return true;
					default:
						break;
				}

				sendAddUsage(sender);
				return true;
			}else if(args.get(0).equalsIgnoreCase("remove")){
				if(!sender.hasPermission("itemlore.command.lore.remove")){
					return false;
				}

				switch(args.size()){
					case 2:
						int line;
						try{
							line = Integer.parseInt(args.get(1));
						}catch(NumberFormatException e){
							break;
						}
						
						itemStackModifyLore(stack,
							(lore -> {
								if(lore.size() > 0){
									int line2 = Math.floorMod(line,lore.size());
									this.getServer().getConsoleSender().sendMessage("Lore " + line2 + " of " + stack.toString() + " before removal: " + lore.get(line2));
									lore.remove(line2);
								}
							})
						);
						return true;
					default:
						break;
				}
				
				sendRemoveUsage(sender);
				return true;
			}else if(args.get(0).equalsIgnoreCase("clear")){
				if(!sender.hasPermission("itemlore.command.lore.clear")){
					return false;
				}

				switch(args.size()){
					case 1:
						itemStackModifyLore(stack,
							(lore -> {
								this.getServer().getConsoleSender().sendMessage("Lore of " + stack.toString() + " before set: " + Util.stringJoin(lore.iterator(),"\n").toString());
								lore.clear();
							})
						);
						return true;
					default:
						break;
				}
				
				sendClearUsage(sender);
				return true;
			}else{
				sendUsage(sender);
				return true;
			}
		}else{
			return false;
		}
	}

	private void sendClearUsage(@NonNull CommandSender sender) {
		sender.sendMessage("/lore clear");
		
	}

	private void sendRemoveUsage(@NonNull CommandSender sender) {
		sender.sendMessage("/lore remove [n] <string>");
	}

	private void sendAddUsage(@NonNull CommandSender sender) {
		sender.sendMessage("/lore add [n] <string>");
	}

	private void sendSetUsage(@NonNull CommandSender sender) {
		sender.sendMessage("/lore set [n] <string>\n/lore set <string1> <string2> ... <stringn>");
	}

	private void sendUsage(@NonNull CommandSender sender) {
		sender.sendMessage(this.getServer().getPluginCommand("lore").getUsage());
	}

	@NonNull
	ItemMeta itemStackGetOrDefaultItemMeta(@NonNull ItemStack stack){
		ItemMeta meta = stack.getItemMeta();
		if(meta == null){
			//Constructs a new item meta for the specific material
			meta = this.getServer().getItemFactory().getItemMeta(stack.getType());
			stack.setItemMeta(meta); //Note: This can return false, but should not because the type of item meta should match from the line above 
		}
		return meta;
	}

	/**
	 * Returns a new list consisting of all the lore lines on the specified item stack.
	 * @param stack
	 * @return
	 */
	@NonNull
	List<String> itemStackGetOrDefaultLore(@NonNull ItemStack stack){
		ItemMeta meta = itemStackGetOrDefaultItemMeta(stack);
		List<String> lore = meta.getLore();
		if(lore == null){
			lore = new ArrayList<String>();
			meta.setLore(lore); 
		}
		return lore;
	}

	/**
	 * Modifies the list consisting of all the lore lines on the specified item stack using a function with side-effects.
	 * @param stack
	 * @return
	 */
	void itemStackModifyLore(@NonNull ItemStack stack,Consumer<? super List<String>> f){ //TODO: May be problems if there are concurrent accesses?
		ItemMeta meta = itemStackGetOrDefaultItemMeta(stack);
		List<String> lore = meta.getLore();
		if(lore == null){
			lore = new ArrayList<String>();
			meta.setLore(lore);
		}
		
		f.accept(lore);
		meta.setLore(lore);
		stack.setItemMeta(meta);
	}
}
