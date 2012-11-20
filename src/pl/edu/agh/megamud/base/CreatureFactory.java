package pl.edu.agh.megamud.base;

import pl.edu.agh.megamud.mechanix.FightBehaviour;

public class CreatureFactory {

	public static Creature getRat(){
		Creature rat = new Creature("rat")
			.setLevel(1)
			.setHp(34);

                rat.initAtribute(Attribute.findByName(Attribute.STRENGTH));
                rat.setAttribute(Attribute.STRENGTH, 5L);	

		rat.addBehaviour(new FightBehaviour(rat));
		return rat;
	}
}