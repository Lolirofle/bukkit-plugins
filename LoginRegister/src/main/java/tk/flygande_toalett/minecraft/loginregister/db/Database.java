package tk.flygande_toalett.minecraft.loginregister.db;

import tk.flygande_toalett.minecraft.loginregister.db.status.Login;
import tk.flygande_toalett.minecraft.loginregister.db.status.Register;

public interface Database{
	public void finish();
	
	public boolean hasLogin(String username);
	public Login verifyLogin(String username,String password);
	public Register register(String username,String password);
	public Register unregister(String username,String password);
}
