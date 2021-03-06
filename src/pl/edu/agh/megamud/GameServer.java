/*******************************************************************************
 * Copyright (c) 2012, AGH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package pl.edu.agh.megamud;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.edu.agh.megamud.base.Command;
import pl.edu.agh.megamud.base.CommandCollector;
import pl.edu.agh.megamud.base.Controller;
import pl.edu.agh.megamud.base.Creature;
import pl.edu.agh.megamud.base.Location;
import pl.edu.agh.megamud.base.Module;

public class GameServer {
	/**
	 * Link to only instance.
	 */
	private static GameServer gameServer = null;

	/**
	 * List of all players, including NPCs.
	 */
	private List<Controller> players = new LinkedList<Controller>();

	/**
	 * List of all creatures.
	 */
	private List<Creature> creatures = new LinkedList<Creature>();

	/**
	 * List of all installed modules.
	 */
	private Map<String, Module> modules = new HashMap<String, Module>();

	/**
	 * Map of all locations - installed by modules.
	 */
	private Map<String, Location> locations = new HashMap<String, Location>();
	/**
	 * Map of all commands - installed by modules.
	 * 
	 * @param loc
	 */
	private CommandCollector commands = new CommandCollector();

	public CommandCollector getCommands() {
		synchronized (commands) {
			return commands;
		}
	}

	public List<Command> findCommands(String id) {
		synchronized (commands) {
			return commands.findCommands(id);
		}
	}

	public List<Command> findCommandsByModule(String module, String id) {
		synchronized (modules) {
			Module m = modules.get(module);
			if (m != null)
				return m.findCommands(id);
			return null;
		}
	}

	public void addLocation(Location loc) {
		synchronized (locations) {
			locations.put(loc.getId(), loc);
		}
	}

	public void removeLocation(String id) {
		synchronized (locations) {
			locations.remove(id);
		}
	}

	public void removeLocation(Location loc) {
		synchronized (locations) {
			locations.remove(loc.getId());
		}
	}

	public void addModule(Module m) {
		synchronized (modules) {
			modules.put(m.getId(), m);
		}
	}

	public void removeModule(Module m) {
		synchronized (modules) {
			modules.remove(m.getId());
		}
	}

	public void removeModule(String id) {
		synchronized (modules) {
			modules.remove(id);
		}
	}

	public Location getStartLocation() {
		synchronized (locations) {
			return locations.get("start");
		}
	}

	public Location getLocation(String id) {
		synchronized (locations) {
			return locations.get(id);
		}
	}

	public Location getLocation(pl.edu.agh.megamud.dao.Location location) {
		return this.getLocation(location.getName());
	}

	private GameServer() {
	}

	public static GameServer getInstance() {
		if (gameServer == null) {
			gameServer = new GameServer();
			gameServer.init();
		}
		return gameServer;
	}

	@SuppressWarnings("unchecked")
	private void init() {
		/*
		 * @todo Loading modules from configuration/db.
		 */

		List<pl.edu.agh.megamud.dao.Module> mod;
		try {
			mod = pl.edu.agh.megamud.dao.Module.getModules();
			for (Iterator<pl.edu.agh.megamud.dao.Module> i = mod.iterator(); i
					.hasNext();) {
				pl.edu.agh.megamud.dao.Module m = i.next();
				try {
					Class<Module> klazz = (Class<Module>) Class.forName(m
							.getJava_class());

					Module inst = klazz.newInstance();

					inst.install();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	public void initController(Controller user) {
		synchronized (players) {
			players.add(user);
			for (Module m : modules.values())
				m.onNewController(user);

			user.onConnect();
		}
	}

	public void killController(Controller user) {
		synchronized (players) {

			if (user.getCreature() != null) {
				user.getCreature().setLocation(null, null);
				user.getCreature().disconnect();
			}

			user.onDisconnect();

			for (Module m : modules.values())
				m.onKillController(user);
			players.remove(user);
		}

	}

	public void initCreature(Controller user, Creature c) {
		synchronized (players) {
			c.connect(user);
			creatures.add(c);
			for (Module m : modules.values())
				m.onNewCreature(c);
		}
	}

	public void killCreature(Creature c) {
		synchronized (players) {
			c.disconnect();
			creatures.remove(c);
			for (Module m : modules.values())
				m.onKillCreature(c);
		}
	}
}
