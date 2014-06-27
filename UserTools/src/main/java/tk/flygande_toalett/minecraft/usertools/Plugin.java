package tk.flygande_toalett.minecraft.usertools;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

public final class Plugin extends JavaPlugin{
	HashMap<Player,TpaRequests> tpa;
	
	public Plugin(){
		tpa = new HashMap<Player,TpaRequests>(); 
	}
	
	@Override
	public void onEnable(){
		//this.getServer().getPluginManager().registerEvents(new PlayerEventListener(this),this);
	}
	
	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		if(sender instanceof Player){
			final Player player = (Player)sender;
			
			if(command.getName().equalsIgnoreCase("distance")){
				BlockIterator targetBlockScanner = new BlockIterator(player,this.getServer().getViewDistance()*16);
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
			}else if(command.getName().equalsIgnoreCase("tpa")){
				if(args.length==1){
					Player targetPlayer = player.getServer().getPlayer(args[0]);
					
					//If player with specified name doesn't exist
					if(targetPlayer==null)
						player.sendMessage(ChatColor.RED + "Cannot teleport to non-existant player.");
					else{
						//Add to request list
						TpaRequests requests = tpa.get(targetPlayer);
						if(requests==null)
							requests = new TpaRequests();
						if(!requests.request(TpaRequests.Type.THERE,player))
							player.sendMessage(targetPlayer.getDisplayName() + ChatColor.RED + "have blocked teleportation requests.");
						else{
							tpa.put(targetPlayer,requests);
							
							//Send to player
							player.sendMessage(ChatColor.GOLD + "Sent teleportation request (tpa) to " + targetPlayer.getDisplayName() + ", waiting for response...");
							
							//Send to target player
							targetPlayer.sendMessage(ChatColor.GOLD + player.getDisplayName() + " has requested to teleport to you.");
							targetPlayer.sendMessage(ChatColor.GOLD + "To accept, type " + ChatColor.RED + "/tpaccept");
							targetPlayer.sendMessage(ChatColor.GOLD + "To refuse, type " + ChatColor.RED + "/tpdeny");
						}
					}
				}else
					sender.sendMessage(ChatColor.RED + command.getUsage());
				return true;
			}else if(command.getName().equalsIgnoreCase("tpahere")){
				if(args.length==1){
					Player targetPlayer = player.getServer().getPlayer(args[0]);
					
					//If player with specified name doesn't exist
					if(targetPlayer==null)
						player.sendMessage(ChatColor.RED + "Cannot teleport non-existant player.");
					else{
						//Add to request list
						TpaRequests requests = tpa.get(targetPlayer);
						if(requests==null)
							requests = new TpaRequests();
						if(!requests.request(TpaRequests.Type.HERE,player))
							player.sendMessage(targetPlayer.getDisplayName() + ChatColor.RED + "have blocked teleportation requests.");
						else{
							tpa.put(targetPlayer,requests);
							
							//Send to player
							player.sendMessage(ChatColor.GOLD + "Sent teleportation request (tpahere) to " + targetPlayer.getDisplayName() + ", waiting for response...");
							
							//Send to target player
							targetPlayer.sendMessage(ChatColor.GOLD + player.getDisplayName() + " has requested that you teleport to them.");
							targetPlayer.sendMessage(ChatColor.GOLD + "To accept, type " + ChatColor.RED + "/tpaccept");
							targetPlayer.sendMessage(ChatColor.GOLD + "To refuse, type " + ChatColor.RED + "/tpdeny");
						}
					}
				}else
					sender.sendMessage(ChatColor.RED + command.getUsage());
				return true;
			}else if(command.getName().equalsIgnoreCase("tpaccept")){
				if(args.length==0){
					//Check request list
					TpaRequests requests = tpa.get(player);
					if(requests==null)
						player.sendMessage(ChatColor.RED + "No teleportation requests");
					else{
						TpaRequests.Response response = requests.accept(player,this.getServer());
						
						if(response==null)
							player.sendMessage(ChatColor.RED + "No teleportation requests");
						else if(response.requesterPlayer==null)
							player.sendMessage(ChatColor.RED + "Cannot teleport to offline player: " + response.requesterPlayerName);
						else{
							//Send to player
							response.requesterPlayer.sendMessage(ChatColor.GOLD + "Teleportation request to " + player.getDisplayName() + " accepted");
							
							//Send to target player
							player.sendMessage(ChatColor.GOLD + "Accepted teleportation request from " + response.requesterPlayer.getDisplayName());
						}
					}
				}else
					sender.sendMessage(ChatColor.RED + command.getUsage());
				return true;
			}else if(command.getName().equalsIgnoreCase("tpdeny")){
				if(args.length==0){
					//Check request list
					TpaRequests requests = tpa.get(player);
					if(requests==null)
						player.sendMessage(ChatColor.RED + "No teleportation requests");
					else{
						TpaRequests.Response response = requests.deny(player,this.getServer());
						
						if(response==null)
							player.sendMessage(ChatColor.RED + "No teleportation requests");
						else if(response.requesterPlayer==null)
							player.sendMessage(ChatColor.RED + "Cannot teleport to offline player: " + response.requesterPlayerName);
						else{
							//Send to player
							response.requesterPlayer.sendMessage(ChatColor.GOLD + "Teleportation request to " + player.getDisplayName() + " denied");
							
							//Send to target player
							player.sendMessage(ChatColor.GOLD + "Denied teleportation request from " + response.requesterPlayer.getDisplayName());
						}
					}
				}else
					sender.sendMessage(ChatColor.RED + command.getUsage());
				return true;
			}else if(command.getName().equalsIgnoreCase("tpablock")){
				if(args.length==0){
					tpa.put(player,new TpaRequests(true));
				}else
					sender.sendMessage(ChatColor.RED + command.getUsage());
				return true;
			}else if(command.getName().equalsIgnoreCase("brightness")){
				sender.sendMessage(ChatColor.GOLD + "Brightness: " + ChatColor.RED + player.getLocation().getBlock().getLightLevel() + ChatColor.GRAY + '/' + ChatColor.RED + "15");
				return true;
			}else if(command.getName().equalsIgnoreCase("iteminfo")){
				final ItemStack item = player.getItemInHand();
				final Material itemType = item.getType();

				//Type
				sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Item info: " + item.getType().toString() + ChatColor.GRAY + " (" + ChatColor.RED + itemType.getId() + ChatColor.GRAY + ":" + ChatColor.DARK_RED + item.getData() + ")");
				
				//Stack
				sender.sendMessage(ChatColor.GOLD +"Stack: " + ChatColor.RED + item.getAmount() + ChatColor.GRAY + "/" + ChatColor.RED + itemType.getMaxStackSize());
				
				//Durability
				if(itemType.getMaxDurability()>0 || item.getDurability()>0)
					sender.sendMessage(ChatColor.GOLD + "Durability: " + ChatColor.RED + item.getDurability() + ChatColor.GRAY + "/" + ChatColor.RED + itemType.getMaxDurability());
				
				//Enchants
				Map<Enchantment,Integer> enchants = item.getEnchantments();
				if(!enchants.isEmpty()){
					StringBuilder str = new StringBuilder(ChatColor.GOLD.toString());
					str.append("Enchants: ");
					for(Entry<Enchantment,Integer> enchant : enchants.entrySet()){
						str.append(ChatColor.RESET.toString());
						str.append(enchant.getKey().getName());
						str.append(ChatColor.GRAY.toString());
						str.append('=');
						str.append(ChatColor.RED.toString());
						str.append(enchant.getValue());
						str.append(ChatColor.GRAY.toString());
						str.append('/');
						str.append(ChatColor.RED.toString());
						str.append(enchant.getKey().getMaxLevel());
						str.append(ChatColor.YELLOW.toString());
						str.append(", ");
					}
					sender.sendMessage(str.toString());
				}

				//Metadata
				if(item.hasItemMeta())
					sender.sendMessage(ChatColor.GOLD + "Metadata: " + ChatColor.DARK_RED + item.getItemMeta());

				return true;
			}else if(command.getName().equalsIgnoreCase("recipesusing")){
				if(args.length==0){					
					ItemStack itemStack = player.getItemInHand();
					
					//Title output
					StringBuilder str = new StringBuilder(ChatColor.GOLD.toString());
					str.append("Recipes using ");
					str.append(ChatColor.RED.toString());
					str.append(itemStack.getAmount());
					str.append(' ');
					str.append(ChatColor.RESET.toString());
					str.append(itemStack.getType().toString());
					
					//Process recipe output and count
					int count=0;
					StringBuilder recipeStr = new StringBuilder();
					List<Recipe> recipes = ItemUtil.getRecipesUsing(Arrays.asList(itemStack),this.getServer());
					for(Recipe recipe : recipes){
						ItemStack resultStack = recipe.getResult();
						recipeStr.append(ChatColor.RED.toString());
						recipeStr.append(resultStack.getAmount());
						recipeStr.append(ChatColor.RESET.toString());
						recipeStr.append(' ');
						recipeStr.append(resultStack.getType().toString());
						recipeStr.append(ChatColor.YELLOW.toString());
						recipeStr.append(", ");
						count++;
					}
					
					//Recipe count output
					str.append(ChatColor.GRAY.toString());
					str.append(" (");
					str.append(ChatColor.RED.toString());
					str.append(count);
					str.append(ChatColor.GRAY.toString());
					str.append(')');
					str.append(ChatColor.GOLD.toString());
					str.append(": ");
					
					//Recipe output
					str.append(recipeStr.toString());
					
					sender.sendMessage(str.toString());
					
					return true;
				}
			}else if(command.getName().equalsIgnoreCase("recipesfor")){
				if(args.length==0){
					ItemStack itemStack = player.getItemInHand();
					
					//Title output
					StringBuilder str = new StringBuilder(ChatColor.GOLD.toString());
					str.append("Recipes from ");
					str.append(ChatColor.RED.toString());
					str.append(itemStack.getAmount());
					str.append(' ');
					str.append(ChatColor.RESET.toString());
					str.append(itemStack.getType().toString());
					
					//Process recipe output and count
					int count=0;
					StringBuilder recipeStr = new StringBuilder();
					List<Recipe> recipes = this.getServer().getRecipesFor(itemStack);
					for(Recipe recipe : recipes){
						ItemStack resultStack = recipe.getResult();
						recipeStr.append(ChatColor.RED.toString());
						recipeStr.append(resultStack.getAmount());
						recipeStr.append(ChatColor.RESET.toString());
						recipeStr.append(' ');
						recipeStr.append(resultStack.getType().toString());
						recipeStr.append(ChatColor.YELLOW.toString());
						recipeStr.append(", ");
						count++;
					}
					
					//Recipe count output
					str.append(ChatColor.GRAY.toString());
					str.append(" (");
					str.append(ChatColor.RED.toString());
					str.append(count);
					str.append(ChatColor.GRAY.toString());
					str.append(')');
					str.append(ChatColor.GOLD.toString());
					str.append(": ");
					
					//Recipe output
					str.append(recipeStr.toString());
					
					sender.sendMessage(str.toString());
					
					return true;
				}
			}
		}

		if(command.getName().equalsIgnoreCase("recipesusing")){
			return true;
		}else if(command.getName().equalsIgnoreCase("recipesfor")){
			return true;
		}else if(command.getName().equalsIgnoreCase("searchitems")){
			if(args.length==1){
				StringBuilder str = new StringBuilder(ChatColor.GOLD.toString());
				str.append("Search results: ");
				
				Pattern pattern = Pattern.compile(args[0],Pattern.CASE_INSENSITIVE);
				for(Material material : Material.values())
					if(pattern.matcher(material.toString()).find()){
						str.append(ChatColor.RESET.toString());
						str.append(material.toString());
						str.append(ChatColor.YELLOW.toString());
						str.append(", ");
					}
				
				sender.sendMessage(str.toString());
			}else
				sender.sendMessage(ChatColor.RED + command.getUsage());
			
			return true;
		}
		
		return false;
	}
}
