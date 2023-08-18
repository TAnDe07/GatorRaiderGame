package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.Actor;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.util.ArrayList;
import java.util.List;

public final class StudentAttackerController implements AttackerController
{
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int update(Game game,long timeDue) {
		int action = game.getAttacker().getNextDir(game.getAttacker().getTargetNode(game.getPillList(), true), true); // default action, attacker eats closest pill
		int minimumVulnerableDefenderDistance =  Integer.MAX_VALUE; // minimum distance of vulnerable defender from attacker
		Node minimumVulnerableDefenderLocation = null; // closest vulnerable defender location from attacker
		Defender minimumVulnerableDefender = null; // closest vulnerable defender from attacker
		Defender minimumDefender = null; // closest normal defender from attacker
		int minimumDefenderDistance = Integer.MAX_VALUE; // minimum distance of normal defender from attacker
		Node minimumDefenderLocation = null; // closest normal defender location from attacker
		int minimumDefenderNum = 0; // number of normal defenders
		int minimumVulnerableDefenderNum = 0; // number of vulnerable defenders

		// checks for closest vulnerable defender
		for (int i = 0; i < game.getDefenders().size(); i++) {
			// executes if defender is vulnerable
			if (game.getDefenders().get(i).isVulnerable()) {
				// checks for minimum distance of vulnerable defender
				if (game.getDefenders().get(i).getLocation().getPathDistance(game.getAttacker().getLocation()) < minimumVulnerableDefenderDistance) {
					minimumVulnerableDefenderDistance = game.getDefenders().get(i).getLocation().getPathDistance(game.getAttacker().getLocation());
					minimumVulnerableDefenderLocation = game.getDefenders().get(i).getLocation();
					minimumVulnerableDefender = game.getDefenders().get(i);
					minimumVulnerableDefenderNum ++;
				}
			}
			// executes if defender is not vulnerable
			else {
				// checks for minimum distance of normal defender
				if (game.getDefenders().get(i).getLocation().getPathDistance(game.getAttacker().getLocation()) < minimumDefenderDistance) {
					minimumDefenderDistance = game.getDefenders().get(i).getLocation().getPathDistance(game.getAttacker().getLocation());
					minimumDefenderLocation = game.getDefenders().get(i).getLocation();
					minimumDefender = game.getDefenders().get(i);
					minimumDefenderNum ++;
				}
			}
		}

		// executes if defender any defender is vulnerable
		if (minimumVulnerableDefender != null) {
			// executes if not all the defenders are vulnerable and if path to the closest vulnerable defender > path to the closest normal defender location
			if ((minimumDefenderNum != 0) && game.getAttacker().getLocation().getPathDistance(minimumVulnerableDefenderLocation) > minimumDefenderLocation.getPathDistance(game.getAttacker().getLocation())) {
				int temp = minimumDefenderLocation.getPathDistance(game.getAttacker().getLocation());
				// if closest normal defender is close to attacker, attacker flees
				if (temp <= 30 && temp > -1) {
					return action = game.getAttacker().getNextDir(minimumDefenderLocation, false);
				}
				// if closest normal defender is far from attacker, attacker goes to closest vulnerable defender
				else if (minimumDefenderLocation.getPathDistance(game.getAttacker().getLocation()) > 30 && minimumVulnerableDefender.getVulnerableTime() > 1){
					return action = game.getAttacker().getNextDir(minimumVulnerableDefenderLocation, true);
				}
			}
			// executes if all defenders are vulnerable
			else {
				// executes if closest vulnerable defender distance <= 5 from attacker, and vulnerable time <= 1, attacker goes to the closest vulnerable defender
				if (minimumVulnerableDefenderLocation.getPathDistance(game.getAttacker().getLocation()) <= 5 && minimumVulnerableDefender.getVulnerableTime() <= 1) {
					return action = game.getAttacker().getNextDir(minimumVulnerableDefenderLocation, true);
				}
				// executes if closest vulnerable defender is far from attacker, and vulnerable time <= 1, attacker flees from closest normal defender
				else if (minimumVulnerableDefenderLocation.getPathDistance(game.getAttacker().getLocation()) > 5 && minimumVulnerableDefender.getVulnerableTime() <= 1) {
					return action = game.getAttacker().getNextDir(minimumDefenderLocation, false);
				}
				// executes if vulnerable time > 1, attacker goes to closest vulnerable defender
				else if (minimumVulnerableDefender.getVulnerableTime() > 1) {
					return action = game.getAttacker().getNextDir(minimumVulnerableDefenderLocation, true);
				}
			}
		}

			// Checks if any defenders are not vulnerable
		if ((!game.getDefender(0).isVulnerable()) || (!game.getDefender(1).isVulnerable()) || (!game.getDefender(2).isVulnerable()) || (!game.getDefender(3).isVulnerable())) {

				int minimumPPDistance = Integer.MAX_VALUE; // minimum distance of attacker from powerPill
				Node minimumPP = null; // location of closest powerPill

				// Checks for closest powerPill
				for (int i = 0; i < game.getPowerPillList().size(); i++) {
					if (game.getPowerPillList().get(i).getPathDistance(game.getAttacker().getLocation()) < minimumPPDistance) {
						minimumPPDistance = game.getPowerPillList().get(i).getPathDistance(game.getAttacker().getLocation());
						minimumPP = game.getPowerPillList().get(i);
					}
				}

				// if any powerPill exists
			if (minimumPP != null) {
				// Checks if any defender is close to attacker, if defender is close eats powerPill
				if ((game.getAttacker().getLocation().getPathDistance(game.getDefender(0).getLocation()) <= 10 && game.getAttacker().getLocation().getPathDistance(game.getDefender(0).getLocation()) > -1)  || (game.getAttacker().getLocation().getPathDistance(game.getDefender(1).getLocation()) <= 10 && game.getAttacker().getLocation().getPathDistance(game.getDefender(1).getLocation()) > -1) || (game.getAttacker().getLocation().getPathDistance(game.getDefender(2).getLocation()) <= 10 && game.getAttacker().getLocation().getPathDistance(game.getDefender(2).getLocation()) > -1) || (game.getAttacker().getLocation().getPathDistance(game.getDefender(3).getLocation()) <= 10 && game.getAttacker().getLocation().getPathDistance(game.getDefender(3).getLocation()) > -1)) {
					return action = game.getAttacker().getNextDir(minimumPP, true);
				}
				else {
					// when close to powerPill and defender is not close or vulnerable, waits for defender to get close
					if (minimumPP.getPathDistance(game.getAttacker().getLocation()) <= 5 && minimumVulnerableDefenderNum == 0) {
						return action = game.getAttacker().getReverse(); // got idea from https://www.youtube.com/watch?v=DVWI1FEacKw
					}
					// when close to powerPill and defender is vulnerable, goes to the closest vulnerable defender
					else if (minimumPP.getPathDistance(game.getAttacker().getLocation()) <= 5 || minimumVulnerableDefenderNum > 0) {
						return action = game.getAttacker().getNextDir(minimumVulnerableDefenderLocation, true);
					}
					// default action, if any of the above criteria are false, goes to closest powerPill
					else {
						return action = game.getAttacker().getNextDir(minimumPP, true);
					}
				}
			}

			// if no powerPill exist
			else if (minimumPP == null) {

				// if powerPills are not available, attacker eats pills
				if (minimumVulnerableDefenderNum == 0 && minimumVulnerableDefenderDistance >= 30) {
					return action = game.getAttacker().getNextDir(game.getAttacker().getTargetNode(game.getPillList(), true), true);
				}
				// if powerPills are not available, and defender is close, attacker runs away from the closest defender
				else if (minimumVulnerableDefenderNum == 0 ) {
					if (game.getDefender(0).getLocation().getPathDistance(game.getAttacker().getLocation()) <= 20 || game.getDefender(1).getLocation().getPathDistance(game.getAttacker().getLocation()) <= 20 || game.getDefender(2).getLocation().getPathDistance(game.getAttacker().getLocation()) <= 20 || game.getDefender(3).getLocation().getPathDistance(game.getAttacker().getLocation()) <= 20) {
						minimumVulnerableDefenderDistance = Integer.MAX_VALUE;
						minimumDefenderLocation = null;

						// Checks for closest defender
						for (int i = 0; i < game.getDefenders().size(); i++) {
							if (game.getDefenders().get(i).getLocation().getPathDistance(game.getAttacker().getLocation()) < minimumVulnerableDefenderDistance) {
								minimumVulnerableDefenderDistance = game.getDefenders().get(i).getLocation().getPathDistance(game.getAttacker().getLocation());
								minimumDefenderLocation = game.getDefenders().get(i).getLocation();
							}
						}
						return action = game.getAttacker().getNextDir(minimumDefenderLocation, false);
					}
				}
			}
		}
		return action;
	}



}