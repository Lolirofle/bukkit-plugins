package tk.flygande_toalett.minecraft.usertools;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class TpaRequests{
	public static enum Type{
		THERE,
		HERE,
		SPAWN,
	}
	
	public static class Request{		
		final Type type;
		final String requesterPlayerName;
		
		public Request(Type type,String targetPlayerName){
			this.type = type;
			this.requesterPlayerName=targetPlayerName;
		}
	}
	
	public static class Response{		
		final Type type;
		final String requesterPlayerName;
		final Player requesterPlayer;
		
		public Response(Type type,String requesterPlayerName,Player requesterPlayer){
			this.type = type;
			this.requesterPlayerName=requesterPlayerName;
			this.requesterPlayer=requesterPlayer;
		}
	}
	
	protected LinkedList<Request> requests;
	protected boolean blocked;
	
	public TpaRequests(){
		this(false);
	}
	
	public TpaRequests(boolean blocked){
		this.requests = new LinkedList<Request>();
		this.blocked = blocked;
	}
	
	public boolean request(Type type,Player target){
		if(blocked)
			return false;
		requests.push(new Request(type,target.getName()));
		return true;
	}
	
	public Response accept(Player player,Server server){
		try{
			Request request = requests.pop();
			
			Player targetPlayer = server.getPlayer(request.requesterPlayerName);
	
			if(targetPlayer!=null)
				switch(request.type){
					case THERE:
						targetPlayer.teleport(player);
						break;
					case HERE:
						player.teleport(targetPlayer);
						break;
					case SPAWN:
						player.teleport(targetPlayer.getBedSpawnLocation());
						break;
				}
			
			return new Response(request.type,request.requesterPlayerName,targetPlayer);
		}catch(NoSuchElementException e){
			return null;
		}
	}
	
	public Response deny(Player player,Server server){
		try{
			Request request = requests.pop();
			
			Player targetPlayer = server.getPlayer(request.requesterPlayerName);
			
			return new Response(request.type,request.requesterPlayerName,targetPlayer);
		}catch(NoSuchElementException e){
			return null;
		}
	}
}
