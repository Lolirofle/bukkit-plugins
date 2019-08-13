package tk.flygande_toalett.minecraft.usertools;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.bukkit.entity.Player;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Data of tpa (teleport ask) requests for a single player (E.g. players which requested this player).
 */
public class TpaRequests{
	/**
	 * The type of tpa request.
	 *
	 */
	public static enum Type{
		/**
		 * This player requests to be teleported to the other player.
		 */
		THERE,
		
		/**
		 * This player requests that the other player is teleported here.
		 */
		HERE,
	}
	
	/**
	 * The data of a single tpa request which was requested to a specific player.
	 */
	public static class Request{		
		@NonNull public final Type type;
		@NonNull public final Player requester;
		@NonNull public final Date timestamp;
		
		public Request(@NonNull Type type,@NonNull Player requester,@NonNull Date timestamp){
			this.type = type;
			this.requester = requester;
			this.timestamp = timestamp;
		}
	}

	public static class Response{		
		public final @NonNull Request request;
		public final @NonNull Player target;
		public final boolean status;
		
		public Response(@NonNull Request request,@NonNull Player target,boolean status){
			this.request = request;
			this.target = target;
			this.status = status;
		}
	}
	
	@NonNull protected LinkedList<Request> requests = new LinkedList<Request>();
	@NonNull public HashSet<Player> block_list = new HashSet<Player>();
	public boolean block_all = false;

	public TpaRequests(){}
	
	/**
	 * Returns whether this player is blocking the requester's tpa requests.
	 * @param requester
	 * @return
	 */
	public boolean is_blocking(@NonNull Player requester){
		return block_all || block_list.contains(requester);
	}
	
	/**
	 * 
	 * @param type
	 * @param requester
	 * @return False when the request was unsuccessful because of the receiver blocking requests.
	 */
	public boolean request(@NonNull Type type,@NonNull Player requester){
		if(this.is_blocking(requester)){
			return false;
		}

		requests.push(new Request(type,requester,new Date()));
		return true;
	}

	@Nullable public Response accept(@NonNull Player player){
		try{
			Request request = requests.pop();
			boolean possible = player.isOnline() && request.requester.isOnline(); 
			switch(request.type){
				case THERE:
					return new Response(request,player,possible && request.requester.teleport(player));
				case HERE:
					return new Response(request,player,possible && player.teleport(request.requester));
				default:
					return null;
			}
		}catch(NoSuchElementException e){
			return null;
		}
	}
	
	@Nullable public Response deny(@NonNull Player player){
		try{
			return new Response(requests.pop(),player,false);
		}catch(NoSuchElementException e){
			return null;
		}
	}
}
