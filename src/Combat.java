import java.util.*;

public class Combat { // SC,LC,LF,HF,CR,BS,CS, R,PR, B, D, DS, BC, RP, PF, SS, RL, LL, HL, IC, GC, PT,
						// SD, LD, CL
	private int[] order = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 };
	private ArrayList<Unit> attackers;
	private ArrayList<Unit> defenders;

	int aWeapon;
	int aShield;
	int aArmour;
	int dWeapon;
	int dShield;
	int dArmour;
	
	public Combat(int aw, int as, int aa, int dw, int ds, int da) {
		aWeapon = aw;
		aShield = as;
		aArmour = aa;
		dWeapon = dw;
		dShield = ds;
		dArmour = da;
	}

	// Simulate one combat
	public int[] run(int[] attackerShips, int[] defenderShips, int[] defense) {
		int[] info = new int[7];
		attackers = new ArrayList<Unit>();
		defenders = new ArrayList<Unit>();
		
		for (int i = 0; i < attackerShips.length; i++)
			for (int j = 0; j < attackerShips[order[i]]; j++)
				attackers.add(new Unit(order[i], true, this));

		for (int i = 0; i < defenderShips.length; i++)
			for (int j = 0; j < defenderShips[order[i]]; j++)
				defenders.add(new Unit(order[i], false, this));

		for (int i = 0; i < defense.length; i++)
			for (int j = 0; j < defense[order[i + 15] - 15]; j++)
				defenders.add(new Unit(order[i + 15], false, this));

		if (attackers.size() <= 0) {
			info[0] = -1;
			return info;
		}
		if(defenders.size() <=0) {
			info[0] = 1;
			return info;
		}

		int metalDebris = 0;
		int crystDebris = 0;

		Random r = new Random();

		// System.out.println(attackers.size()+" "+defenders.size());

		for (int round = 1; round <= 6; round++) {
			// System.out.println("\n\nRound " + round);

			// Attackers attack
			// System.out.println("Attackers fire at defenders");
			for (int i = 0; i < attackers.size(); i++) {
				Unit unit = attackers.get(i);
//				System.out.print(
//						names[unit.type] + " with " + unit.hull + ":" + unit.shield + ":" + unit.weapon + " fires at ");
				int x = r.nextInt(defenders.size());
				Unit target = defenders.get(x);
//				System.out.print(
//						names[target.type] + " with " + target.hull + ":" + target.shield + ":" + target.weapon + "; ");
				if (unit.weapon * 100 >= target.shield) {
					target.shield -= unit.weapon;
					if (target.shield < 0) {
						target.hull += target.shield;
						target.shield = 0;
					}
					double ratio = target.hull * 1.0 / target.oHull;
					if (target.hull > 0 && (ratio <= 0.7)) {
						double p = r.nextDouble();
						if (p >= ratio)
							target.hull = 0;
					}
//					System.out.println("result is " + names[target.type] + " with " + target.hull + ":" + target.shield
//							+ ":" + target.weapon);
				}
				int fire = shipRapidFire[unit.type][target.type];
				if (fire > 0) {
					int y = r.nextInt(fire);
					if (y != 0)
						i--;
				}
			}

			// Defenders attack
			// System.out.println("Defenders fire at attackers");
			for (int i = 0; i < defenders.size(); i++) {
				Unit unit = defenders.get(i);
//				System.out.print(
//						names[unit.type] + " with " + unit.hull + ":" + unit.shield + ":" + unit.weapon + " fires at ");
				int x = r.nextInt(attackers.size());
				Unit target = attackers.get(x);
//				System.out.print(
//						names[target.type] + " with " + target.hull + ":" + target.shield + ":" + target.weapon + "; ");
				if (unit.weapon * 100 >= target.shield) {
					target.shield -= unit.weapon;
					if (target.shield < 0) {
						target.hull += target.shield;
						target.shield = 0;
					}
					double ratio = target.hull * 1.0 / target.oHull;
					if (target.hull > 0 && (ratio <= 0.7)) {
						double p = r.nextDouble();
						if (p >= ratio)
							target.hull = 0;
					}
//					System.out.println("result is " + names[target.type] + " with " + target.hull + ":" + target.shield
//							+ ":" + target.weapon);
				}

				if (!unit.defense) {
					int fire = shipRapidFire[unit.type][target.type];
					if (fire > 0) {
						int y = r.nextInt(fire);
						if (y != 0)
							i--;
					}
				}
			}

			// Round cleanup
			for (int i = 0; i < attackers.size(); i++) {
				if (attackers.get(i).hull <= 0) {
					metalDebris += attackers.get(i).metalDebris();
					crystDebris += attackers.get(i).crystalDebris();
					info[4]+=Unit.ships[0][attackers.get(i).type];
					info[5]+=Unit.ships[1][attackers.get(i).type];
					info[6]+=Unit.ships[8][attackers.get(i).type];
					//attackerShips[attackers.get(i).type]--;
					attackers.remove(i);
					i--;
				} else
					attackers.get(i).heal();
			}
			for (int i = 0; i < defenders.size(); i++) {
				if (defenders.get(i).hull <= 0) {
					metalDebris += defenders.get(i).metalDebris();
					crystDebris += defenders.get(i).crystalDebris();
//					try {
//					defenderShips[defenders.get(i).type]--;
//					} catch(Exception e) {
//						defense[defenders.get(i).type-15]--;
//					}
					defenders.remove(i);
					i--;
				} else
					defenders.get(i).heal();
			}

			info[1] = metalDebris;
			info[2] = crystDebris;

			// System.out.println(attackers.size());

			if (attackers.size() == 0) {
				info[0] = -1;
				return info;
			}
			if (defenders.size() == 0) {
				info[0] = 1;

				// Loot
				info[3] = 0;
				for (Unit u : attackers)
					info[3] += u.cargo;

				return info;
			}

		}
		info[0] = 0;
		return info;
	}

	private static int[][] shipRapidFire = {
			{ 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 3, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 6, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 10, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 20, 20, 10, 10, 5, 5, 0, 0, 5 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 2, 0, 0, 5, 0, 10, 0, 0, 0, 0, 0, 0, 5 },
			{ 250, 250, 200, 100, 33, 30, 250, 250, 1250, 25, 5, 0, 15, 10, 30, 1250, 200, 200, 100, 100, 50, 0, 0, 0,
					1250 },
			{ 3, 3, 0, 4, 4, 7, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 0, 0, 0, 7, 0, 0, 5, 4, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5 } };

	public String[] names = { "Small Cargo", "Large Cargo", "Light Fighter", "Heavy Fighter", "Cruiser", "Battleship",
			"Colony Ship", "Recycler", "Espionoge Probe", "Bomber", "Destroyer", "Deathstar", "Battlecruiser", "Reaper",
			"Pathfinder", "Solar Sattelite", "Rocket Launcher", "Light Laser", "Heavy Laser", "Ion Cannon",
			"Gause Cannon", "Plasma Turret", "Small Shield Dome", "Large Shield Dome", "Crawler" };
}
