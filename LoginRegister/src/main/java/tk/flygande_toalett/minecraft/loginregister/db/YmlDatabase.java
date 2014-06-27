package tk.flygande_toalett.minecraft.loginregister.db;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tk.flygande_toalett.minecraft.loginregister.Plugin;
import tk.flygande_toalett.minecraft.loginregister.db.status.*;
import tk.flygande_toalett.minecraft.loginregister.string.*;

public class YmlDatabase implements Database{
	protected Plugin plugin;
	protected File file;
	protected FileConfiguration config;
	protected StringTransformer passwordTransformer;
	
	public YmlDatabase(Plugin plugin,String filepath){
		this(plugin,filepath,new DefaultStringTransformer());
	}
	
	public YmlDatabase(Plugin plugin,String filepath,StringTransformer passwordTransformer){
		this.plugin = plugin;
		this.file = new File((String)null,filepath);
		this.config = YamlConfiguration.loadConfiguration(file);
		this.passwordTransformer = passwordTransformer;
	}
	
	@Override
	public void finish(){
		try{
			config.save(file);
		}catch(IOException e){
			plugin.getLogger().log(Level.SEVERE,"Could not save config to " + file.getAbsolutePath(),e);
		}
	}
	
	@Override
	public boolean hasLogin(String username){
		return config.contains(username);
	}
	
	@Override
	public Login verifyLogin(String username,String password){
		String userPassword = config.getString(username);
		if(userPassword == null)
			return Login.NotRegistered;
		
		if(userPassword.equals(passwordTransformer.transform(password)))
			return Login.Success;

		return Login.InvalidLogin;
	}
	
	@Override
	public Register register(String username,String password){
		if(hasLogin(username))
			return Register.AlreadyRegistered;
		
		config.set(username,passwordTransformer.transform(password));
		return Register.Success;
	}
	
	@Override
	public Register unregister(String username,String password){
		switch(verifyLogin(username,password)){
			case NotRegistered:
				return Register.AlreadyRegistered;
			case Success:
				config.set(username,null);
				return Register.Success;
			default:
				return Register.Error;
		}
	}
}
