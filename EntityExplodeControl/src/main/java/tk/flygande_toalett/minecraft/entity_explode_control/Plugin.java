package tk.flygande_toalett.minecraft.entity_explode_control;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin{
	public Config config;

	@Override
	public void onEnable(){
		//Config
		if(!new File(this.getDataFolder(),"config.yml").exists()){
			this.getLogger().warning("Configuration file does not exist, it will be created.");
			this.saveDefaultConfig();
		}
		this.reloadConfig();
		config = new Config(this,this.getConfig());
		
		//Listener
		this.getServer().getPluginManager().registerEvents(new EntityExplodeEventListener(this),this);
	}
}
