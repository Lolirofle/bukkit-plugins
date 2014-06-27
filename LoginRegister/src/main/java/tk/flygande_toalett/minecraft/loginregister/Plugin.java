package tk.flygande_toalett.minecraft.loginregister;

import java.io.File;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import tk.flygande_toalett.minecraft.loginregister.db.*;

public final class Plugin extends JavaPlugin{
	protected HashSet<String> playersNotLoggedIn;
	protected Database database = NoCheckDatabase.instance;
	
	public Plugin(){
		playersNotLoggedIn = new HashSet<String>();
	}
	
	@Override
	public void onEnable(){
		database = new YmlDatabase(this,this.getDataFolder().getPath() + File.separator + "login.yml");
		this.getServer().getPluginManager().registerEvents(new PlayerEventsHandler(this),this);
	}
	
	@Override
	public void onDisable(){
		database.finish();
		database = NoCheckDatabase.instance;
	}
	
	@Override
	public boolean onCommand(final CommandSender sender,final Command command,final String label,final String[] args){
		if(sender instanceof Player){
			if(command.getName().equalsIgnoreCase("login")){
				if(!playersNotLoggedIn.contains(sender.getName()))
					sender.sendMessage(ChatColor.RED + "Already logged in!");
				else if(args.length==1)
					switch(database.verifyLogin(sender.getName(),args[0])){
						case NotRegistered:
							sender.sendMessage(ChatColor.RED + "Requires registration first.");
							break;
						case Success:
							playersNotLoggedIn.remove(sender.getName());
							sender.sendMessage(ChatColor.RED + "Successful login.");
							this.getLogger().log(Level.INFO,"User " + sender.getName() + " successfully logged in.");
							break;
						case InvalidLogin:
							sender.sendMessage(ChatColor.RED + "Invalid login!");
							this.getLogger().log(Level.INFO,"User " + sender.getName() + " tried to log in with invalid password.");
							break;
						case Error:
							sender.sendMessage(ChatColor.RED + "Server Error.");
							break;
					}
				else
					sender.sendMessage(ChatColor.RED + command.getUsage());
			}else if(command.getName().equalsIgnoreCase("register")){
					if(args.length==1)
						switch(database.register(sender.getName(),args[0])){
							case AlreadyRegistered:
								sender.sendMessage(ChatColor.RED + "Already registered.");
								break;
							case Success:
								sender.sendMessage(ChatColor.RED + "Successful registration!");
								this.getLogger().log(Level.INFO,"User " + sender.getName() + " registered.");
								break;
							case Error:
								sender.sendMessage(ChatColor.RED + "Server Error.");
								break;
						}
					else
						sender.sendMessage(ChatColor.RED + command.getUsage());
			}else if(command.getName().equalsIgnoreCase("unregister")){
				if(args.length==1)
					switch(database.unregister(sender.getName(),args[0])){
						case AlreadyRegistered:
							sender.sendMessage(ChatColor.RED + "Not registered.");
							break;
						case Success:
							sender.sendMessage(ChatColor.RED + "Successful unregistration!");
							break;
						case Error:
							sender.sendMessage(ChatColor.RED + "Server Error.");
							break;
					}
				else
					sender.sendMessage(ChatColor.RED + command.getUsage());
			}else
				return false;
			return true;
		}
		return false;
	}
}
