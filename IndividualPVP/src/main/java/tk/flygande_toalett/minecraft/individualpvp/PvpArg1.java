package tk.flygande_toalett.minecraft.individualpvp;

import java.util.Arrays;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public enum PvpArg1{
	Enable,
	Disable,
	Get,
	Toggle;

	@NonNull
	public static final String[] enableStrings = {
		"on",
		"1",
		"true",
		"enabled",
		"enable",
	};

	@NonNull
	public static final String[] disableStrings = {
		"off",
		"0",
		"false",
		"disabled",
		"disable",
	};

	@NonNull
	public static final String[] getStrings = {
		"get",
		"?",
	};

	@NonNull
	public static final String[] toggleStrings = {
		"toggle",
	};

	@NonNull
	public String[] strings(){
		switch(this){
			case Enable:
				return enableStrings;
			case Disable:
				return disableStrings;
			case Get:
				return getStrings;
			case Toggle:
				return toggleStrings;
			default:
				return null;
		}
	}
	
	@Nullable
	public static PvpArg1 parse(String str){
		if(Arrays.binarySearch(enableStrings,str) > 0){
			return PvpArg1.Enable;
		}

		if(Arrays.binarySearch(disableStrings,str) > 0){
			return PvpArg1.Disable;
		}
		
		if(Arrays.binarySearch(getStrings,str) > 0){
			return PvpArg1.Get;
		}
		
		if(Arrays.binarySearch(toggleStrings,str) > 0){
			return PvpArg1.Toggle;
		}
		
		return null;
	}
}
