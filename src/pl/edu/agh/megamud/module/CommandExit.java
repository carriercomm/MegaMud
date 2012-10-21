package pl.edu.agh.megamud.module;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.megamud.base.Command;
import pl.edu.agh.megamud.base.Controller;

public class CommandExit implements Command {
	public String getName(){
		return "exit";
	}
	public boolean interprete(Controller user, String command) {
		user.disconnect();
		return true;
	}

}
