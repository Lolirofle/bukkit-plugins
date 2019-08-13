package tk.flygande_toalett.minecraft.usertools;

import java.io.File;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jdt.annotation.NonNull;

import tk.flygande_toalett.minecraft.usertools.commands.*;

public final class Plugin extends JavaPlugin implements Listener{
	protected HashMap<Player,TpaRequests> tpa = new HashMap<Player,TpaRequests>();
	
	public Plugin(){}
	
	@Override
	public void onEnable(){
		if(!new File(this.getDataFolder(),"config.yml").exists()){
			this.getLogger().warning("Configuration file does not exist, it will be created.");
			this.saveDefaultConfig();
		}
		this.reloadConfig();

		this.getServer().getPluginManager().registerEvents(this,this);

		this.getCommand("distance").setExecutor(new Distance(this));
		this.getCommand("tpa").setExecutor(new Tpa(this));
		this.getCommand("tpahere").setExecutor(new TpaHere(this));
		this.getCommand("tpaccept").setExecutor(new TpAccept(this));
		this.getCommand("tpdeny").setExecutor(new TpDeny(this));
		this.getCommand("tpspawn").setExecutor(new TpSpawn(this));
		this.getCommand("tpworldspawn").setExecutor(new TpWorldSpawn(this));
		this.getCommand("resetspawn").setExecutor(new ResetSpawn(this));
		this.getCommand("suicide").setExecutor(new Suicide());
	}

	/*
	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		if(sender instanceof Player){
			final Player player = (Player)sender;

			if(command.getName().equalsIgnoreCase("brightness")){
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
	*/
	
	@NonNull public TpaRequests tpaRequests(Player player){
		TpaRequests requests = this.tpa.get(player);
		if(requests == null){
			requests = new TpaRequests();
			this.tpa.put(player,requests);
		}
		return requests;
	}
}
