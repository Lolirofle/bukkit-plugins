package tk.flygande_toalett.minecraft.block_break_exp_control;

import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin{
	public Config config;

	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(new BlockBreakEventListener(this),this);
		config = new Config(this.getConfig());
	}
}
