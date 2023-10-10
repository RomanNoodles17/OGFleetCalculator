
public class Unit {
	public int metal;
	public int crystal;
	public int hull;
	public int shield;
	public int weapon;
	public int oHull;
	public int oShield;
	public int cargo;
	public int speed;
	public int fuel;
	
	public int type;
	boolean attacker;
	boolean defense;
	Combat sim;

	public void heal() {
		shield = oShield;
	}
	
	public int metalDebris() {
		int d = metal*3/10;
		if(defense && !(type==15 || type == 24))
			d = d*3/10;
		return d;
	}
	
	public int crystalDebris() {
		int d = crystal*3/10;
		if(defense && !(type==15 || type == 24))
			d = d*3/10;
			
		return d;
	}
	
	public Unit(int t, boolean b, Combat s) {
		type = t;
		attacker = b;
		defense = (type >= 15);
		sim = s;
		
		if(defense) {
			metal = def[0][type-15];
			crystal = def[1][type-15];
			hull = def[2][type-15];
			shield = def[3][type-15];
			weapon = def[4][type-15];
		}
		else {
			metal = ships[0][type];
			crystal = ships[1][type];
			hull = ships[2][type];
			shield = ships[3][type];
			weapon = ships[4][type];
			cargo = ships[5][type];
			speed = ships[6][type];
			fuel = ships[7][type];
		}
		
		if(attacker) {
			hull = hull * (10+s.aArmour)/100;
			shield = shield * (10+s.aShield)/10;
			weapon = weapon * (10+s.aWeapon)/10;
		}
		else {
			hull = hull * (10+s.dArmour)/100;
			shield = shield * (10+s.dShield)/10;
			weapon = weapon * (10+s.dWeapon)/10;
		}
		oHull = hull;
		oShield = shield;
	}
	
	
	public static int[][] ships = {
			// SC, LC, LF, HF, CR, BS, CS, R, Esp, B, D, RIP, BC, ReP, PF
			{ 2000, 6000, 3000, 6000, 20000, 45000, 10000, 10000, 0, 50000, 60000, 5000000, 30000, 85000, 8000 }, // 0 - Metal Cost
			{ 2000, 6000, 1000, 4000, 7000, 15000, 20000, 6000, 1000, 25000, 50000, 4000000, 40000, 55000, 15000 }, // 1 - Crystal Cost
			{ 4000, 12000, 4000, 10000, 27000, 60000, 30000, 16000, 1000, 75000, 110000, 9000000, 70000, 140000, 23000 }, // 2 - Structural Integrity
			{ 10, 25, 10, 25, 50, 200, 100, 10, 0, 500, 500, 50000, 400, 700, 100 }, // 3 - Shield Power
			{ 5, 5, 50, 150, 400, 1000, 50, 1, 0, 1000, 2000, 200000, 700, 2800, 200 }, // 4 - Weapon Power
			{ 5000, 25000, 50, 100, 800, 1500, 7500, 20000, 5, 500, 2000, 1000000, 750, 10000, 10000 }, // 5 - Cargo Capacity
			{ 5000, 7500, 12500, 10000, 15000, 10000, 2500, 2000, 100000000, 4000, 5000, 100, 10000, 7000, 12000 }, // 6 - Base Speed
			{ 10, 50, 20, 75, 300, 500, 1000, 300, 1, 700, 1000, 1, 250, 1100, 300 }, // 7 - Fuel Consumption
			{ 0, 0, 0, 0, 2000, 0, 10000, 2000, 0, 15000, 15000, 1000000, 15000, 20000, 8000} // 8 - Deut Cost
	};
	
	private static int[][] def = {
			// Sat, RL, LL, HL, IC, GC, PT, SS, LS, CL
			{ 0, 2000, 1500, 6000, 20000, 5000, 50000, 10000, 10000 , 2000}, // 0 - Metal Cost
			{ 2000, 0, 500, 2000, 15000, 3000, 50000, 50000, 50000 , 2000}, // 1 - Crystal Cost
			{ 2000, 2000, 2000, 8000, 8000, 35000, 100000, 20000, 100000 , 4000}, // 2 - Structural Integrity
			{ 0, 20, 25, 100, 500, 200, 300, 2000, 10000 , 1}, // 3 - Shield Power
			{ 0, 80, 100, 250, 150, 1100, 3000, 1, 1 , 1} }; // 4 - Weapon Power
	
}
