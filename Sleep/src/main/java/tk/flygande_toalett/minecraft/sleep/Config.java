package tk.flygande_toalett.minecraft.sleep;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

public class Config{
	//Initialize default config
	static final Configuration defaultConfig;
	static{
		defaultConfig = new MemoryConfiguration();
		ConfigurationSection global = defaultConfig.createSection("global");
		global.addDefault("depth",50);
		global.addDefault("downMessage","test");
		global.addDefault("upMessage","test");
		
		defaultConfig.createSection("worlds");
	}
	
	public class Data{
		public int depth;
		public String downMessage;
		public String upMessage;
		
		public Data(ConfigurationSection section){
			depth       = section.getInt("depth");
			downMessage = section.getString("downMessage");
			upMessage   = section.getString("upMessage");
		}
		
		public void export(ConfigurationSection section){
			section.set("depth"      ,depth);
			section.set("downMessage",downMessage);
			section.set("upMessage"  ,upMessage);
		}
	}

	protected Configuration config;
	protected Data global;
	protected HashMap<String,Data> worldSpecific;
	
	public Config(Configuration config){
		this.config = config;
		this.worldSpecific = new HashMap<String,Data>();
		
		//Set default config
		config.setDefaults(defaultConfig);
		
		//Load global config
		global = new Data(config.getConfigurationSection("global"));
		
		//Load world specific config
		ConfigurationSection worldsSection = config.getConfigurationSection("worlds");
		if(worldsSection!=null){
			for(String worldName : worldsSection.getKeys(false)){
				if(worldsSection.isConfigurationSection(worldName))
					worldSpecific.put(worldName,new Data(worldsSection.getConfigurationSection(worldName)));
			}
		}
	}
	
	public void finish(){
		global.export(config.isConfigurationSection("global")? config.getConfigurationSection("global") : config.createSection("global"));
		
		for(Entry<String,Data> entry : worldSpecific.entrySet())
			entry.getValue().export(config.isConfigurationSection(entry.getKey())? config.getConfigurationSection(entry.getKey()) : config.createSection(entry.getKey()));
	}
	
	public int getDepth(String worldName){
		Data data = worldSpecific.get(worldName);
		return data==null || data.depth==0? global.depth : data.depth;
	}
	
	public String getDownMessage(String worldName){
		Data data = worldSpecific.get(worldName);
		return data==null || data.downMessage==null? global.downMessage : data.downMessage;
	}
	
	public String getUpMessage(String worldName){
		Data data = worldSpecific.get(worldName);
		return data==null || data.upMessage==null? global.upMessage : data.upMessage;
	}
}
