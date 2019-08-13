package tk.flygande_toalett.minecraft.individualpvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jdt.annotation.NonNull;

import java.io.File;
import java.util.HashMap;

public final class Plugin extends JavaPlugin implements Listener{
	@NonNull public Config config = new Config();
	//public HashMap<Player,Boolean> pvpStatus = new HashMap<Player,Boolean>();
	@NonNull private HashMap<Player,Long> lastHurtTimestamp = new HashMap<Player,Long>();
	@NonNull private HashMap<Player,Long> lastToggleTimestamp = new HashMap<Player,Long>();
	@NonNull public final NamespacedKey pvpKey = new NamespacedKey(this,"pvp");

	@Override
	public void onEnable(){
		if(!new File(this.getDataFolder(),"config.yml").exists()){
			this.getLogger().warning("Configuration file does not exist, it will be created.");
			this.saveDefaultConfig();
		}
		this.reloadConfig();
		config = new Config(this.getConfig());

		this.getServer().getPluginManager().registerEvents(this,this);
	}

	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		if(command.getName().equalsIgnoreCase("pvp")){
			if(!sender.hasPermission("individualpvp.command.pvp")){
				return false;
			}

			if(sender instanceof Player){
				if(sender.hasPermission("individualpvp.forcenopvp")){
					sender.sendMessage(ChatColor.RED + "PVP is forced to be " + ChatColor.GRAY + "disabled" + ChatColor.RED + ".");

					return true;
				}

				if(sender.hasPermission("individualpvp.forcepvp")){
					sender.sendMessage(ChatColor.RED + "PVP is forced to be " + ChatColor.RED + "enabled" + ChatColor.RED + ".");

					return true;
				}

				if(args.length == 0){
					if(performPvpSetDelayCheck((Player)sender)){
						this.sendSetPvpMessage((Player)sender,this.togglePvp((Player)sender));
						recordTogglePvpSetDelay((Player)sender);
					}
					return true;
				}

				if(args.length == 1){
					PvpArg1 arg = PvpArg1.parse(args[0]);
					if(arg == null){
						sendPvpOtherCommandInstructions(sender);
						return true;
					}

					switch(arg){
						case Enable:
							if(performPvpSetDelayCheck((Player)sender)){
								this.setPvp((Player)sender,true);
								this.sendSetPvpMessage((Player)sender,true);
								recordTogglePvpSetDelay((Player)sender);
							}
							break;
						case Disable:
							if(performPvpSetDelayCheck((Player)sender)){
								this.setPvp((Player)sender,false);
								this.sendSetPvpMessage((Player)sender,false);
								recordTogglePvpSetDelay((Player)sender);
							}
							break;
						case Get:
							this.sendGetPvpMessage(sender,(Player)sender);
							break;
						case Toggle:
							if(performPvpSetDelayCheck((Player)sender)){
								this.sendSetPvpMessage((Player)sender,this.togglePvp((Player)sender));
								recordTogglePvpSetDelay((Player)sender);
							}
							break;
						default:
							break;
					}
					return true;
				}
				
				sendPvpCommandInstructions(sender);
				return true;
			}else{
				sender.sendMessage(ChatColor.RED + "This command must be run by a player.");
				return true;
			}
		}else if(command.getName().equalsIgnoreCase("pvpother")){
			if(!sender.hasPermission("individualpvp.command.pvpother")){
				return false;
			}

			if(args.length == 0){
				sendPvpOtherCommandInstructions(sender);
				return true;
			}

			Player player = this.getServer().getPlayer(args[0]);
			if(player == null){
				sender.sendMessage(ChatColor.RED + "There is no player using the username " + ChatColor.WHITE + args[0] + ChatColor.RED + ".");
				return true;
			}

			if(player.hasPermission("individualpvp.forcenopvp")){
				sender.sendMessage(ChatColor.RED + "PVP is forced to be disabled for " + ChatColor.WHITE + args[0] + ChatColor.RED + ".");

				return true;
			}

			if(player.hasPermission("individualpvp.forcepvp")){
				sender.sendMessage(ChatColor.RED + "PVP is forced to be enabled for " + ChatColor.WHITE + args[0] + ChatColor.RED + ".");

				return true;
			}

			if(args.length == 1){
				this.sendGetPvpMessage(sender,player);
				return true;
			}

			if(args.length == 2){
				PvpArg1 arg = PvpArg1.parse(args[1]);
				if(arg == null){
					sendPvpOtherCommandInstructions(sender);
					return true;
				}

				switch(arg){
					case Enable:
						this.setPvp(player,true);
						this.sendSetPvpOtherMessage(sender,player,true);
						break;
					case Disable:
						this.setPvp(player,false);
						this.sendSetPvpOtherMessage(sender,player,false);
						break;
					case Get:
						this.sendGetPvpMessage(sender,player);
						break;
					case Toggle:
						this.sendSetPvpOtherMessage(sender,player,this.togglePvp(player));
						break;
					default:
						break;
				}
				return true;
			}

			sendPvpOtherCommandInstructions(sender);
			return true;
		}else if(command.getName().equalsIgnoreCase("getpvp")){
			if(!sender.hasPermission("individualpvp.command.getpvp")){
				return false;
			}

			if(args.length == 0){
				if(sender instanceof Player){
					this.sendGetPvpMessage(sender,(Player)sender);
				}else{
					sender.sendMessage(ChatColor.RED + "This command with 0 parameters must be run by a player.");
					return true;
				}
				return true;
			}

			Player player = this.getServer().getPlayer(args[0]);
			if(player == null){
				sender.sendMessage(ChatColor.RED + "There is no player using the username " + ChatColor.WHITE + args[0] + ChatColor.RED + ".");
				return true;
			}

			if(player.hasPermission("individualpvp.forcenopvp")){
				sender.sendMessage(ChatColor.RED + "PVP is forced to be disabled for " + ChatColor.WHITE + args[0] + ChatColor.RED + ".");

				return true;
			}

			if(player.hasPermission("individualpvp.forcepvp")){
				sender.sendMessage(ChatColor.RED + "PVP is forced to be enabled for " + ChatColor.WHITE + args[0] + ChatColor.RED + ".");

				return true;
			}

			if(args.length == 1){
				this.sendGetPvpMessage(sender,player);
				return true;
			}

			sendGetPvpCommandInstructions(sender);
			return true;
		}

		return false;
	}

	public boolean performPvpSetDelayCheck(@NonNull Player sender){
		long time = System.currentTimeMillis();

		if(this.config.pvpHurtDisableWait > 0 && time < this.lastHurtTimestamp.getOrDefault((Player)sender,0l) + this.config.pvpHurtDisableWait){
			sender.sendMessage(ChatColor.RED + "PVP cannot be changed before " + ChatColor.WHITE + this.config.pvpHurtDisableWait + " ms" + ChatColor.RED + " after getting hurt.");
			return false;
		}else if(this.config.pvpToggleWait > 0 && time < this.lastToggleTimestamp.getOrDefault((Player)sender,0l) + this.config.pvpToggleWait){
			sender.sendMessage(ChatColor.RED + "PVP cannot be changed before " + ChatColor.WHITE + this.config.pvpToggleWait + " ms" + ChatColor.RED + " after the last change.");
			return false;
		}else{
			return true;
		}
	}

	public void recordHurtPvpSetDelay(@NonNull Player damaged){
		if(this.config.pvpHurtDisableWait > 0){
			this.lastHurtTimestamp.put(damaged,System.currentTimeMillis());
		}
	}
	
	public void recordTogglePvpSetDelay(@NonNull Player toggler){
		if(this.config.pvpToggleWait > 0){
			this.lastToggleTimestamp.put(toggler,System.currentTimeMillis());
		}
	}

	public void sendSetPvpMessage(@NonNull Player sender,boolean pvp){
		String message = ChatColor.WHITE + "PVP is now " + (pvp? ChatColor.RED + "enabled" : ChatColor.GRAY + "disabled");
		if(config.pvpToggleAnnounce){
			Bukkit.broadcastMessage(message + ChatColor.WHITE + " for " + ChatColor.WHITE + sender.getName());
		}else{
			sender.sendMessage(message);
		}
	}

	public void sendSetPvpOtherMessage(@NonNull CommandSender sender,@NonNull Player player,boolean pvp){
		String message = ChatColor.WHITE + "PVP is now " + (pvp? ChatColor.RED + "enabled" : ChatColor.GRAY + "disabled");
		String messageAppend = ChatColor.WHITE + " for " + ChatColor.WHITE + sender.getName();
		if(config.pvpToggleAnnounce){
			Bukkit.broadcastMessage(message + messageAppend);
		}else{
			sender.sendMessage(message + messageAppend);
			player.sendMessage(message);
		}
	}

	private void sendGetPvpMessage(@NonNull CommandSender sender,@NonNull Player player){
		sender.sendMessage(ChatColor.WHITE + "PVP is " + (this.getPvp(player)? ChatColor.RED + "enabled" : ChatColor.GRAY + "disabled") + (sender == player? "" : ChatColor.WHITE + " for " + ChatColor.WHITE + player.getName()));
	}

	private void sendPvpCommandInstructions(@NonNull CommandSender sender){
		sender.sendMessage(this.getServer().getPluginCommand("pvp").getUsage());
	}
	
	private void sendPvpOtherCommandInstructions(@NonNull CommandSender sender){
		sender.sendMessage(this.getServer().getPluginCommand("pvpother").getUsage());
	}

	private void sendGetPvpCommandInstructions(@NonNull CommandSender sender){
		sender.sendMessage(this.getServer().getPluginCommand("getpvp").getUsage());
	}

	/**
	 * Returns the actual PVP status of a player
	 * @param  player The player to get the PVP status out of
	 * @return        PVP status of the specified player
	 */
	public boolean getPvp(@NonNull final Player player){
		//TODO: Is it possible to write these using a single expression of only boolean operations using AND and OR?
		//TODO: Are these calls expensive?

		if(player.hasPermission("individualpvp.forcenopvp")){
			return false;
		}

		if(player.hasPermission("individualpvp.forcepvp")){
			return true;
		}

		Byte playerPvp = player.getPersistentDataContainer().get(this.pvpKey,PersistentDataType.BYTE);
		if(playerPvp != null){
			return playerPvp.byteValue() > 0;
		}else {
			return player.hasPermission("individualpvp.defaultpvp");
		}
	}

	public void setPvp(@NonNull final Player player,boolean setting){
		player.getPersistentDataContainer().set(this.pvpKey,PersistentDataType.BYTE,setting? (byte)1 : (byte)0);

		//this.pvpStatus.put(player,setting);
	}

	public boolean togglePvp(@NonNull final Player player){
		PersistentDataContainer container = player.getPersistentDataContainer();

		Byte playerPvp = player.getPersistentDataContainer().get(this.pvpKey,PersistentDataType.BYTE);
		boolean setting = !((playerPvp != null)? (playerPvp > 0) : player.hasPermission("individualpvp.defaultpvp"));
		container.set(this.pvpKey,PersistentDataType.BYTE,setting? (byte)1 : (byte)0);
		return setting;

		//return this.pvpStatus.compute(player , ((k,v) -> (v == null)? !player.hasPermission("individualpvp.defaultpvp") : !v));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player){
			Player damaged = (Player)e.getEntity();
			
			if(e.getDamager() instanceof Player){
				Player damager = (Player)e.getDamager();
				
				if(!this.getPvp(damaged) || !this.getPvp(damager)){
					e.setCancelled(true);
				}else{
					recordHurtPvpSetDelay(damaged);
				}
			}else if(e.getDamager() instanceof Projectile){
				Projectile projectile = (Projectile)e.getDamager();

				if(projectile.getShooter() instanceof Player){
					Player damager = (Player)projectile.getShooter();

					if(!this.getPvp(damaged) || !this.getPvp(damager)){
						e.setCancelled(true);
					}else{
						recordHurtPvpSetDelay(damaged);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent e){
		if(e.getEntity() instanceof Player){
			Player damaged = (Player)e.getEntity();
			
			if(e.getCombuster() instanceof Player){
				Player damager = (Player)e.getCombuster();
				
				if(!this.getPvp(damaged) || !this.getPvp(damager)){
					e.setCancelled(true);
				}else{
					recordHurtPvpSetDelay(damaged);
				}
			}else if(e.getCombuster() instanceof Projectile){
				Projectile projectile = (Projectile)e.getCombuster();

				if(projectile.getShooter() instanceof Player){
					Player damager = (Player)projectile.getShooter();

					if(!this.getPvp(damaged) || !this.getPvp(damager)){
						e.setCancelled(true);
					}else{
						recordHurtPvpSetDelay(damaged);
					}
				}
			}
		}
	}
}
