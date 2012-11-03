package pl.edu.agh.megamud.module;

import pl.edu.agh.megamud.base.Command;
import pl.edu.agh.megamud.base.Controller;
import pl.edu.agh.megamud.base.Location;
import pl.edu.agh.megamud.dao.Item;

public class CommandTake implements Command {
	public String getName(){
		return "take";
	}
	
	public boolean interprete(Controller user, String command) {
		if(user.getCreature()==null)
			return false;
		
		Location loc=user.getCreature().getLocation();
		Item it=loc.getItems().get(command);
		if(it==null){
			user.write("There is no such item as "+command+"!");
		}else{
			if(user.getCreature().getItems().containsKey(command)){
				user.write("You already have "+it.getName()+"!");
			}else{
				boolean b=it.giveTo(user.getCreature());
				if(!b){
					user.write("You cannot have this item!");
				}
			}
		}
		
		return true;
	}
	
}
