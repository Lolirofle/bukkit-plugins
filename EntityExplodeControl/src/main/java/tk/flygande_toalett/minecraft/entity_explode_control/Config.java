package tk.flygande_toalett.minecraft.entity_explode_control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityExplodeEvent;

public class Config{
	//Initialize default config
	static final Configuration defaultConfig;
	static{
		defaultConfig = new MemoryConfiguration();
		ConfigurationSection global = defaultConfig.createSection("global");
		global.addDefault("blockBlacklist",null);
		
		defaultConfig.createSection("entities");
	}

	public interface Rule{
		public void onEntityExplode(final EntityExplodeEvent event);
	}

	public class BlockRule implements Rule{
		public Integer blockMinDepthFilter  = null;
		public Integer blockMaxDepthFilter  = null;
		public boolean blockSunlightFilter  = false;
		public boolean blockUndestroyable   = false;
		public Float   blockYield           = null;

		public BlockRule(ConfigurationSection section){
			blockMinDepthFilter = section.getInt("when_height_at_least");
			blockMaxDepthFilter = section.getInt("when_height_at_most");
		}
		
		protected void handleYield(final EntityExplodeEvent event){
			if(blockYield != null){
				event.setYield(blockYield);
			}
		}
		
		protected boolean handleDepthFilter(final EntityExplodeEvent event){
			return
				(blockMinDepthFilter != null && event.getLocation().getY() <= blockMinDepthFilter) ||
				(blockMaxDepthFilter != null && event.getLocation().getY() >= blockMinDepthFilter)
			;
		}
		
		protected boolean handleLightFilter(final EntityExplodeEvent event){
			return blockSunlightFilter && event.getLocation().getWorld().getBlockAt(event.getLocation()).getLightFromSky() == 0;
		}
		
		protected void handleUndestroyable(final EntityExplodeEvent event){
			if(blockUndestroyable){
				event.blockList().clear();
			}
		}
		
		public void onEntityExplode(final EntityExplodeEvent event){
			handleYield(event);
			if(handleDepthFilter(event)){return;}
			if(handleLightFilter(event)){return;}
			handleUndestroyable(event);
		}
	}

	public class SpecificBlockRule extends BlockRule{
		public HashSet<Material> blocksFilter = new HashSet<>();

		public SpecificBlockRule(Plugin plugin,ConfigurationSection section){
			super(section);
			for(String blockName : section.getStringList("for_blocks")){
				Material type = Material.valueOf(blockName);
				if(type != null){
					blocksFilter.add(type);
				}else{
					plugin.getLogger().warning("Unknown material \"" + blockName + "\" in config. Ignoring.");
				}
			}
		}

		protected void handleUndestroyable(final EntityExplodeEvent event){
			if(blockUndestroyable){
				//Remove every block from event.blockList() that has a material mentioned in blocksFilter 
				Iterator<Block> i = event.blockList().iterator();
				while(i.hasNext()){
					Block block = i.next();
					Material blockType = block.getType(); 
					if(blocksFilter.contains(blockType)){
						i.remove();
					}
				}
			}
		}
	}

	private void processConfigRule(Plugin plugin,ConfigurationSection ruleSection){
		List<String> whenEntities = ruleSection.getStringList("when_entities");
		if(whenEntities != null){
			for(String whenEntity : whenEntities){
				addRuleFromEntityName(plugin,whenEntity,ruleSection);
			}
		}else{
			String whenEntity = ruleSection.getString("when_entity");
			if(whenEntity != null){
				addRuleFromEntityName(plugin,whenEntity,ruleSection);
			}else{
				global.add(ruleFromRuleConfigSection(plugin,ruleSection));
			}
		}
	}
	
	private void addRuleFromEntityName(Plugin plugin,String whenEntity,ConfigurationSection ruleSection){
		EntityType type = EntityType.valueOf(whenEntity);
		if(type != null){
			ArrayList<Rule> entityRules = entitySpecific.get(type);
			if(entityRules == null){entityRules = new ArrayList<>();}
			entityRules.add(ruleFromRuleConfigSection(plugin,ruleSection));
			entitySpecific.put(type,entityRules);
		}else{
			plugin.getLogger().warning("Unknown entity type \"" + whenEntity + "\" in config. Ignoring.");
		}
	}
	
	private Rule ruleFromRuleConfigSection(Plugin plugin,ConfigurationSection ruleSection){
		//TODO
		return new SpecificBlockRule(plugin,ruleSection);
	}
	
	protected Configuration config;
	protected ArrayList<Rule> global;
	protected HashMap<EntityType,ArrayList<Rule>> entitySpecific;
	
	public Config(Plugin plugin,Configuration config){
		this.config = config;
		this.global = new ArrayList<>();
		this.entitySpecific = new HashMap<>();

		//Set default config
		config.setDefaults(defaultConfig);

		//Load world specific config
		List<?> rulesSection = config.getList("rules");
		if(rulesSection!=null){
			for(Object ruleSection : rulesSection){
				if(ruleSection instanceof ConfigurationSection){
					processConfigRule(plugin,(ConfigurationSection)ruleSection);
				}else{
					plugin.getLogger().warning("Invalid rule in config. Must be a record. Ignoring.");
				}
			}
		}
	}
}
