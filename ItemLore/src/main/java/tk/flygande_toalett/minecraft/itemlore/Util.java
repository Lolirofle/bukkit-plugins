package tk.flygande_toalett.minecraft.itemlore;

import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public final class Util{
	private Util(){}

	@NonNull
	public static <T> T nullorDefault(@Nullable T val,@NonNull T def){
		return val==null? def : val;
	}
	
	@NonNull
	public static StringBuilder stringJoin(Iterator<String> strs,String delim){
		StringBuilder out = new StringBuilder();
		if(strs.hasNext()){
			out.append(strs.next());

			while(strs.hasNext()){
				out.append(delim);
				out.append(strs.next());
			}
		}
		return out;
	}
	
	@NonNull
	public static <T,I extends Iterator<T>> I iteratorSkip(@NonNull I iter,int n){
		while(n>0 && iter.hasNext()){
			iter.next();
			n-= 1;
		}
		return iter;
	}
	
	public static final class IteratorIterable<T> implements Iterable<T>{
		public final Iterator<T> iter;
		
		public IteratorIterable(@NonNull Iterator<T> iter){
			this.iter = iter;
		} 
		
		@Override
		public Iterator<T> iterator(){
			return this.iter;
		}
		
	}
}
