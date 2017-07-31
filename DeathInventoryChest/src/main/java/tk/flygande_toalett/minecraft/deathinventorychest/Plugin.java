package tk.flygande_toalett.minecraft.deathinventorychest;

import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin{
	public Plugin(){}

	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(new PlayerDeathListener(this),this);
	}
}
