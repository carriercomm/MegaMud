package pl.edu.agh.megamud.base;

import pl.edu.agh.megamud.dao.CreatureItem;
import pl.edu.agh.megamud.dao.PlayerCreature;

/**
 * Physical, existing item.
 * @author Tomasz
 *
 */
public class Item {
	/**
	 * In-database representation of this item.
	 */
	protected CreatureItem creatureItem=null;
	
	/**
	 * In-game owner of this item.
	 */
	protected ItemHolder owner=null;
	
	protected String name;
	protected String description;
	
	public Item(CreatureItem it){
		this.creatureItem=it;
		
		this.name=it.getItem().getName();
		this.description=it.getItem().getDescription();
	}
	
	public Item(String name,String description){
		this.name=name;
		this.description=description;
	}
	
	public ItemHolder getOwner() {
		return owner;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	/**
	 * Use this to move this item to other item holder (creature or location) - especially from/to null, when item is magically (dis)appearing. 
	 * @todo Write this to database.
	 * @param owner
	 * @return True, if given.
	 */	
	public boolean giveTo(ItemHolder newOwner){
		ItemHolder oldOwner=owner;
		
		if(!canBeGivenTo(newOwner))
			return false;
		
		if(oldOwner!=null){
			oldOwner.removeItem(this);
			oldOwner.onItemDisappear(this,newOwner);
		}
	
		if(creatureItem!=null){
			creatureItem.setCreature(null);
			// @todo
			//creatureItem.setLocation(null);
		}
		
		this.owner=newOwner;
		if(newOwner!=null){
			newOwner.addItem(this);
			newOwner.onItemAppear(this,oldOwner);
			
			if(creatureItem!=null){
				if(newOwner instanceof Location){
					// @todo No location in dao.
					//creatureItem.setLocation((Location)newOwner);
				}else{
					// @todo Import it
					PlayerCreature pc=((Creature)newOwner).getDbCreature();
					if(pc!=null){
						creatureItem.setCreature(pc);
					}
				}
			}
		}
		

		// @todo commit
		
		return true;
	}	
	
	/**
	 * Internal check, whether this item can be held by a creature. It can depend on its stats (class, some attribute's value) or other held items.
	 * @param owner
	 */
	protected boolean canBeGivenTo(ItemHolder owner){
		return false; // TODO Add some logic.
	}
}
