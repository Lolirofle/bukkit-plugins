package tk.flygande_toalett.minecraft.loginregister.db;

import tk.flygande_toalett.minecraft.loginregister.db.status.Login;
import tk.flygande_toalett.minecraft.loginregister.db.status.Register;

public class NoCheckDatabase implements Database{
	public final static NoCheckDatabase instance = new NoCheckDatabase();
	
	private NoCheckDatabase(){}
	
	@Override
	public void finish(){}
	
	@Override
	public boolean hasLogin(String username){
		return false;
	}
	
	@Override
	public Login verifyLogin(String username,String password){
		return Login.Success;
	}
	
	@Override
	public Register register(String username,String password){
		return Register.Error;
	}
	
	@Override
	public Register unregister(String username,String password){
		return Register.Error;
	}
}
