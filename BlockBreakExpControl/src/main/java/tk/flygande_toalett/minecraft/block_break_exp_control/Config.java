package tk.flygande_toalett.minecraft.block_break_exp_control;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

public class Config{
	//Initialize default config
	static final Configuration defaultConfig;
	static{
		defaultConfig = new MemoryConfiguration();
		ConfigurationSection materials = defaultConfig.createSection("materials");
		{
			ConfigurationSection section = materials.createSection("WHEAT");
			section.set("exp",1);
			section.set("prob",0.25);
		}
		{
			ConfigurationSection section = materials.createSection("POTATO");
			section.set("exp",1);
			section.set("prob",0.25);
		}
		{
			ConfigurationSection section = materials.createSection("CARROT");
			section.set("exp",1);
			section.set("prob",0.25);
		}
		{
			ConfigurationSection section = materials.createSection("NETHER_WART");
			section.set("exp",1);
			section.set("prob",0.25);
		}
		{
			ConfigurationSection section = materials.createSection("BEETROOT");
			section.set("exp",1);
			section.set("prob",0.25);
		}
		{
			ConfigurationSection section = materials.createSection("CACTUS");
			section.set("exp",1);
			section.set("prob",0.5);
		}
		{
			ConfigurationSection section = materials.createSection("SUGAR_CANE");
			section.set("exp",1);
			section.set("prob",0.25);
		}
	}

	public class Data{
		public int exp;
		public double prob;

		public Data(ConfigurationSection section){
			this.exp  = section.contains("exp") ? section.getInt("exp")     : 0;
			this.prob = section.contains("prob")? section.getDouble("prob") : 0.0;
		}

		public void export(ConfigurationSection section){
			section.set("exp" ,exp);
			section.set("prob",prob);
		}
	}

	protected Configuration config;
	protected HashMap<Material,Data> materials;
	protected Random rng;

	public Config(Configuration config){
		this.config    = config;
		this.materials = new HashMap<Material,Data>();
		this.rng       = new Random();

		//Set default config
		config.setDefaults(defaultConfig);

		//Load world specific config
		ConfigurationSection materialsSection = config.getConfigurationSection("materials");
		if(materialsSection!=null){
			for(String materialName : materialsSection.getKeys(false)){
				//Bukkit.getServer().getLogger().log(Level.WARNING,": " + materialName);
				if(materialsSection.isConfigurationSection(materialName)){
					try{
						materials.put(Material.valueOf(materialName),new Data(materialsSection.getConfigurationSection(materialName)));
					}catch(IllegalArgumentException e){
						Bukkit.getServer().getLogger().log(Level.WARNING,"Material \"" + materialName +"\" is invalid in config");
					}
				}
			}
		}
	}
	
	public void finish(){		
		for(Entry<Material,Data> entry : materials.entrySet()){
			String materialName = entry.getKey().toString();
			entry.getValue().export(config.isConfigurationSection(materialName)? config.getConfigurationSection(materialName) : config.createSection(materialName));
		}
	}
	
	public int getExp(Material material){
		Data data = materials.get(material);
		if(data!=null){
			return this.rng.nextDouble() <= data.prob? data.exp : 0;
		}else{
			return -1;
		}
	}
}
