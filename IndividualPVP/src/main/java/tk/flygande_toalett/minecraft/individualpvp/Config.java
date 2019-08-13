package tk.flygande_toalett.minecraft.individualpvp;

import org.bukkit.configuration.Configuration;

public class Config {
	public long pvpToggleWait;
	public long pvpHurtDisableWait;
	public boolean pvpToggleAnnounce;
	
	public Config(){
		this.pvpToggleWait = 1000;
		this.pvpHurtDisableWait   = 10000;
		this.pvpToggleAnnounce    = true;
	}
	
	public Config(Configuration config){
		this.pvpToggleWait = config.getInt("pvp_enable_disable_wait");
		this.pvpHurtDisableWait   = config.getInt("pvp_hurt_disable_wait");
		this.pvpToggleAnnounce    = config.getBoolean("pvp_toggle_announce");//AnnounceOption.parse(config.getString("pvp_toggle_announce"));
	}

	public void finish(){}

}
