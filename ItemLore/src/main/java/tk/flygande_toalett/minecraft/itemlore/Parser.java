package tk.flygande_toalett.minecraft.itemlore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.IntStream;

import org.eclipse.jdt.annotation.Nullable;

public interface Parser<Out>{
	@Nullable
	default Out parse(CharSequence str){
		return parse(str.codePoints());
	}

	@Nullable
	default Out parse(IntStream str){
		Iterator<Integer> iter = str.iterator();
		Integer c = iter.next();
		if(c != null) {
			return parse(c,iter);
		}else{
			return null;
		}
	}

	@Nullable
	Out parse(int current,Iterator<Integer> str);

	final class CommandArgs implements Parser<ArrayList<String>>{
		ArrayList<String> newArgs;
		StringBuilder newArg = new StringBuilder();

		CommandArgs(){
			newArgs = new ArrayList<String>();
		}

		CommandArgs(int n){
			newArgs = new ArrayList<String>(n);
		}

		private void push(){
			if(newArg.length() != 0){
				newArgs.add(newArg.toString());
				newArg.setLength(0);
			}
		}
		
		@Override
		public @Nullable ArrayList<String> parse(int current, Iterator<Integer> str){
			newArgs.clear();
			newArg.appendCodePoint(current);

			while(str.hasNext()){
				int c = str.next();
			
				switch(c){
					case (int)'\\':{
						Integer result = new Escape().parse(c,str);
						if(result != null) {
							newArg.appendCodePoint(result);
						}
						break;
					}
					case (int)'"':{
						this.push();
						
						String result = new Str().parse(c,str);
						if(result != null){
							newArgs.add(result);
						}
						break;
					}
					case (int)' ':
						this.push();
						break;
					default:
						newArg.appendCodePoint(c);
						break;
				}
			}

			return newArgs;
		}
	}
	
	final class Escape implements Parser<Integer>{
		@Override
		public @Nullable Integer parse(int current,Iterator<Integer> str) {
			if(str.hasNext()){
				switch(str.next()){
					case (int)'n':
						return (int)'\n';
					case (int)'"':
						return (int)'"';
					case (int)'\\':
						return (int)'\\';
					case (int)' ':
						return (int)' ';
				}
			}
			return null;
		}

	}
	
	final class Str implements Parser<String>{
		StringBuilder string = new StringBuilder();

		@Override
		public @Nullable String parse(int current, Iterator<Integer> str){
			string.setLength(0);
			
			while(str.hasNext()){
				int c = str.next();
			
				switch(c){
					case (int)'\\':{
						Integer result = new Escape().parse(c,str);
						if(result != null) {
							string.appendCodePoint(result);
						}
						break;
					}
					case (int)'"':
						return string.toString();
					default:
						string.appendCodePoint(c);
						break;
				}
			};

			return string.toString();
		}
	}
}
