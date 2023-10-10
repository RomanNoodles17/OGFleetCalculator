import java.io.File;
import java.util.Scanner;

public class Main {

	// Initializations
	static Combat sim;
	static int[] fin;
	static int fint;
	static int[] info;
	static boolean ogFile = false; // debug only: enabling allows manual input

	// Configurable program parameters
	static double[] ratio = { 1, 1.5, 3 }; // Resource value ratio (metal, crystal, deuterium)
	static int precision = 10; // 
	static int accuracy = 3; // size of the set to take the mean score from
	static int roundnessUnit = 2; // The factor by which simulation should be rounded
	static int roundness = 1; // Default roundness; # of times simplified = roundness-1
	static int passes = 50; // Number of overall attempts at achieving maximum gains
	static int recyclerMin = 300000; // Minimum amount of debris in orbit before accounting for collection of it
	static int simulations = 1;

	// Ship statistics (SC,LC,LF,HF,CR,BS,CS, R,PR, B, D, DS, BC, RP, PF)
	static int[] speeds = { 5000, 7500, 12500, 10000, 15000, 10000, 2500, 2000, 10000000, 4000, 5000, 100, 10000, 7000,
			12000 };
	static int[] basicCons = { 10, 50, 20, 75, 300, 500, 1000, 300, 1, 700, 1000, 1, 250, 1100, 300 };
	static int[] drives = { 1, 1, 1, 2, 2, 3, 2, 1, 1, 2, 3, 3, 3, 3, 3 };
	static int[] incs = new int[15];

	// Player statistic defaults (overwritten by input data)
	static int aWeapon = 0;
	static int aShield = 0;
	static int aArmour = 0;
	static int dWeapon = 0;
	static int dShield = 0;
	static int dArmour = 0;
	static int[] loot = { 0, 0, 0 };
	static int[] coords1 = { 1, 1, 1 };
	static int[] coords2 = { 1, 1, 1 };
	static int combustion = 0;
	static int impulse = 0;
	static int hyperdrive = 0;
	static int hyperspace = 0;

	// Default fleet ratio for attacker
	static int[] defaultRatio = { 0 // SC
			, 0 // LC
			, 1000 // LF
			, 0 // HF
			, 200 // CR
			, 100 // BS
			, 0 // CS
			, 0 // R
			, 0 // ESP
			, 20 // B
			, 20 // D
			, 0 // RIP
			, 50 // BC
			, 0 // RP
			, 0 // PF
	};

	// initialization of fleet/defense arrays
	static int[] aMax = new int[15];
	static int[] as = new int[15];
	static int[] ds = new int[15];
	static int[] def = new int[10];

	public static void main(String[] args) throws Exception {
		// Reading of user info from user.txt
		Scanner sc = new Scanner(new File("user.txt"));
		skip(sc);

		// SHIPS
		for (int i = 0; i < aMax.length; i++)
			aMax[i] = readInt(sc.nextLine());
		skip(sc);

		// TECH
		aWeapon = readInt(sc.nextLine());
		aShield = readInt(sc.nextLine());
		aArmour = readInt(sc.nextLine());
		combustion = readInt(sc.nextLine());
		impulse = readInt(sc.nextLine());
		hyperdrive = readInt(sc.nextLine());
		hyperspace = readInt(sc.nextLine());
		skip(sc);

		// SETTINGS
		String[] line = sc.nextLine().split(" ");
		line = line[1].split(":");
		for (int i = 0; i < coords1.length; i++)
			coords1[i] = Integer.parseInt(line[i]);
		recyclerMin = readInt(sc.nextLine());
		skip(sc);

		// ADVANCED
		line = sc.nextLine().split(" ");
		for (int i = 0; i < 3; i++)
			ratio[i] = Double.parseDouble(line[i + line.length - ratio.length]);
		simulations = readInt(sc.nextLine());
		precision = readInt(sc.nextLine());
		accuracy = readInt(sc.nextLine());
		roundnessUnit = readInt(sc.nextLine());
		roundness = readInt(sc.nextLine());
		passes = readInt(sc.nextLine());
		line = sc.nextLine().split(" ");
		for (int i = 0; i < defaultRatio.length; i++)
			defaultRatio[i] = Integer.parseInt(line[i + line.length - defaultRatio.length]);
		sc.close();
		
		// Read defender input
		if (ogFile) { // reads from defender.txt
			// Tech
			sc = new Scanner(new File("defender.txt"));
			skip(sc);
			dWeapon = readInt(sc.nextLine());
			dShield = readInt(sc.nextLine());
			dArmour = readInt(sc.nextLine());
			skip(sc);

			// Ships
			for (int i = 0; i < ds.length; i++)
				ds[i] = readInt(sc.nextLine());
			skip(sc);

			// Def
			for (int i = 0; i < def.length; i++)
				def[i] = readInt(sc.nextLine());
			skip(sc);

			// Info
			line = sc.nextLine().split(" ");
			for (int i = 0; i < loot.length; i++)
				loot[i] = Integer.parseInt(line[i + line.length - loot.length]);
			line = sc.nextLine().split(" ");
			line = line[1].split(":");
			for (int i = 0; i < coords2.length; i++)
				coords2[i] = Integer.parseInt(line[i]);
			sc.close();

		} else { // Reads from input.txt
			// Defender info
			sc = new Scanner(new File("input.txt"));

			// Tech
			String s = "";
			while (s.length() < 4 || !s.substring(0, 3).equals("Wea"))
				s = sc.nextLine();
			s = "";
			while (s.length() < 4 || !s.substring(0, 3).equals("Wea"))
				s = sc.nextLine();
			dWeapon = Integer.parseInt(sc.nextLine());
			sc.nextLine();
			dShield = Integer.parseInt(sc.nextLine());
			sc.nextLine();
			dArmour = Integer.parseInt(sc.nextLine());
			while (!s.equals("Ships"))
				s = sc.nextLine();

			// Ships
			sc.nextLine();
			for (int i = 0; i < 15; i++) {
				s = sc.nextLine();
				if (s.charAt(0) < 65) {
					ds[i] = Integer.parseInt(s);
					sc.nextLine();
				}
			}

			// Coords and loot
			for (int i = 0; i < 3; i++) {
				coords2[i] = Integer.parseInt(sc.nextLine());
				sc.nextLine();
				// System.out.println(i + " " + coords2[i]);
			}
			s = "";
			while (s.length() < 4 || !s.substring(0, 3).equals("Met"))
				s = sc.nextLine();
			for (int i = 0; i < 3; i++) {
				loot[i] = Integer.parseInt(sc.nextLine());
				sc.nextLine();
			}

			// Defense
			sc.nextLine();
			for (int i = 1; i <= 6; i++) {
				s = sc.nextLine();
				if (s.charAt(0) < 65) {
					def[i] = Integer.parseInt(s);
					sc.nextLine();
				}
			}
			while (s.length() < 4 || !s.substring(0, 3).equals("Sol"))
				s = sc.nextLine();
			s = sc.nextLine();
			if (s.charAt(0) < 65) {
				def[0] = Integer.parseInt(s);
				sc.nextLine();
			}
			s = sc.nextLine();
			if (s.charAt(0) < 65) {
				def[def.length - 1] = Integer.parseInt(s);
				sc.nextLine();
			}
			def[7] = 1;
			def[8] = 1;

			// Plunder
			while (s.length() == 0 || s.charAt(s.length() - 1) != '%')
				s = sc.nextLine();
			if (s.charAt(0) == '5')
				for (int i = 0; i < 3; i++)
					loot[i] = loot[i] * 2 / 3;

			// Last
			while (sc.hasNextLine())
				s = sc.nextLine();
			line = s.split(" ");
			if (line.length > 0 && line.length <= 3) {
				if (line[0].length() > 0 && (line[0].charAt(0) == 'g' || line[0].charAt(0) == 'G')) {
					dWeapon += 2;
					dShield += 2;
					dArmour += 2;
				}
				int x = 1;
				for (int i = line.length - 1; i >= 0 && x >= 0; i--) {
					if (line[i].length() > 0 && line[i].charAt(0) < 65)
						coords1[x--] = Integer.parseInt(line[i]);
				}

			}

			sc.close();
		}

		// Reduce loot by 25% to accurately represent plunder amount
		for (int i = 0; i < 3; i++)
			loot[i] = loot[i] * 3 / 4;
		
		// Make sure all values of aMax are positive
		for (int i = 0; i < aMax.length; i++)
			if (aMax[i] < 0)
				aMax[i] = Integer.MAX_VALUE;

		// multiply drive multiplier by each drive tech-level
		for (int i = 0; i < drives.length; i++)
			if (drives[i] == 1)
				drives[i] *= combustion;
			else if (drives[i] == 2)
				drives[i] *= impulse;
			else
				drives[i] *= hyperdrive;

		info = new int[7];
		sim = new Combat(aWeapon, aShield, aArmour, dWeapon, dShield, dArmour);
		fin = new int[15]; // Current maximum-scoring fleet
		fint = 0; // Current maximum score

		// System.out.println("Starting:");

		for (int i = 0; i < passes; i++) { // For each pass
			for (int j = 0; j < as.length; j++) // For each slip type in array as
				as[j] = 0;
			calculate(); // calculate results
			if (simulate() > fint) { // if score is greater than current max-score set new max score and max-scoring fleet
				int x = simulate();
				if (x > fint) {
					fint = x;
					for (int j = 0; j < fin.length; j++)
						fin[j] = as[j];
				}
			}
			System.out.printf("%3d out of %d - %d %s\n", i + 1, passes, fint, displayShipsLine()); //Display current best score/fleet
		}

		// Display ships that would have been included if attacker had them available
		for (int i = 0; i < incs.length; i++)
			if (incs[i] != 0)
				System.out.println(sim.names[i] + " - " + incs[i]);

		//Display final score, coords of combat and final fleet
		System.out.printf("\n\n\nDONE!\n%,d\n\n", fint);
		System.out.println(coords2[0] + ":" + coords2[1] + ":" + coords2[2]);
		for (int i = 0; i < fin.length; i++)
			fin[i] = as[i];
		System.out.println(displayShipsLine());

//////////////////////////////////////////////////////////////////////////////////////////////////////////

	}

	//Reads the final integer displayed on a line
	static int readInt(String s) {
		String[] line = s.split(" ");
		return Integer.parseInt(line[line.length - 1]);
	}

	// Skips all empty lines
	static void skip(Scanner sc) {
		while (sc.nextLine().length() < 1)
			continue;
	}

	// 
	static void calculate() {

		// Increase ratio for starting fleet
		as[1] = (loot[0] + loot[1] + loot[2]) / (40000 * (20 + hyperspace) / 20);
		int last = Integer.MIN_VALUE;
		for (int i = 0; i >= 0; i++) {
			for (int j = 0; j < as.length; j++)
				as[j] += defaultRatio[j];
			// System.out.println(i);
			int x = simulate();
			if (x != Integer.MIN_VALUE)
				i = -2;
			last = x;
		}

		while (true) {
			for (int j = 0; j < as.length; j++)
				as[j] += defaultRatio[j];
			int x = simulate();
			// System.out.println(x+" " + as[2]);
			if (last >= x) {
				for (int i = 0; i < as.length; i++)
					as[i] -= defaultRatio[i];
				break;
			}
			last = x;
		}

		// whittle
		for (int i = 0; i < as.length; i++)
			as[i] = Math.min(as[i], aMax[i]);

		// System.out.println("Found starting ratio");

		// Tuning fleet ratios
		for (int p = 0; p < precision; p++)
			for (int pj = 0; pj < accuracy; pj++) {
				// System.out.print("\n"+p+" "+pj+": ");
				int standard = simulate();
				for (int ship = 2; ship < 15; ship++) {
					if (defaultRatio[ship] == 0)
						continue;
					int delta = defaultRatio[ship] / (int) Math.pow(roundnessUnit, p);
					if (delta < roundness)
						delta = roundness;

					while (true) {
						as[ship] += delta;
						if (as[ship] > aMax[ship]) {
							incs[ship]++;
							as[ship] -= delta;
							break;
						}
						int score = simulate();
						if (score > standard)
							standard = score;
						else {
							as[ship] -= delta;
							break;
						}
					}
					while (true) {
						as[ship] -= delta;
						int score = simulate();
						if (score > standard)
							standard = score;
						else {
							as[ship] += delta;
							break;
						}
						if (as[ship] <= 0) {
							as[ship] = 0;
							break;
						}
					}
				}
			}
	}

	// calculate number of cargo ships to complement the fleet
	static int cargo(int loot) {
		return loot / (40000 * (20 + hyperspace) / 20) + 1;
	}

	// Create String displaying all ships in fleet
	static String displayShipsLine() {
		String s = "";
		for (int i = 0; i < fin.length; i++)
			if (fin[i] != 0)
				s += (sim.names[i] + ":" + fin[i] + " ");
		return s;
	}

	// Calculate fuel consumption of fleet
	static int consumption() {
		double distance = 0;
		if (coords1[0] == coords2[0] && coords1[1] == coords2[1])
			distance = 10;
		else if (coords1[0] == coords2[0])
			distance = 2700
					+ (95 * Math.max(Math.abs(coords1[1] - coords2[1]), 499 - Math.abs(coords1[1] - coords2[1])));
		else
			distance = 20000 * Math.max(Math.abs(coords1[0] - coords2[0]), 4 - Math.abs(coords1[0] - coords2[0]));
		double speed = Integer.MAX_VALUE;
		for (int i = 0; i < as.length; i++)
			if (as[i] != 0)
				speed = Math.min(speed, speeds[i] * (1.0 + drives[i] / 10.0));
		double duration = (35000 / 10 * Math.sqrt(distance * 10.0 / speed) + 10);
		double consumption = 0;
		for (int i = 0; i < as.length; i++) {
			double s = (35000 / (duration - 10) * Math.sqrt(distance * 10.0 / (speeds[i] * (10 + drives[i]) / 10)));
			consumption += (basicCons[i] * as[i] * distance) * (s / 10.0 + 1.0) * (s / 10.0 + 1.0) / 35000;
		}
		return (int) consumption;
	}

	static int simulate() {
		// System.out.println("\n\n\n\n\n\n");
		// displayShips(sim);

		int metal = 0;
		int crystal = 0;
		int deut = 0;

		deut -= consumption();
		int minspace = Integer.MAX_VALUE;

		long[] averages = new long[info.length];
		for (int i = 0; i < simulations; i++) {
			info = sim.run(as, ds, def);
			minspace = Math.min(minspace, info[3]);
			for (int j = 0; j < info.length; j++)
				averages[j] += info[j];
		}
		for (int i = 0; i < info.length; i++)
			averages[i] /= simulations;

		if (averages[1] + averages[2] > recyclerMin) {
			metal += averages[1];
			crystal += averages[2];
		}
		metal -= averages[4];
		crystal -= averages[5];
		deut -= averages[6];
		int space = (int) averages[3];
		space = space * (20 + hyperspace) / 20;
		minspace = minspace * (20 + hyperspace) / 20;
		if (averages[0] != 1) {
			return Integer.MIN_VALUE;
		}
		if (minspace < loot[0] + loot[1] + loot[2]) {
			// System.out.println("Adding LC: "+as[1]);
			as[1] += cargo(loot[0] + loot[1] + loot[2] - minspace) + 10;
			return simulate();
		} else if (minspace > loot[0] + loot[1] + loot[2] + 1000000 && as[1] > 0) {
			// System.out.println("Removing LC: "+as[1]);
			as[1] -= cargo(minspace - loot[0] - loot[1] - loot[2]);
			if (as[1] < 0)
				as[1] = 0;
			return simulate();
		}

		if (space > loot[0]) {
			space -= loot[0];
			metal += loot[0];
			if (space > loot[1]) {
				space -= loot[1];
				crystal += loot[1];
				if (space > loot[2])
					deut += loot[2];
				else
					deut += space;
			} else
				crystal += space;
		} else
			metal += space;
		return (int) (metal * ratio[0]) + (int) (crystal * ratio[1]) + (int) (deut * ratio[2]);
	}

}
