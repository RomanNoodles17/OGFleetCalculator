
public class Test {
	
	static int runs = 500;

	static int[] as = { 1000 // SC
			, 1000 // LC
			, 1000 // LF
			, 500 // HF
			, 300 // CR
			, 150 // BS
			, 100 // CS
			, 100 // R
			, 500 // ESP
			, 100 // B
			, 100 // D
			, 1 // RIP
			, 50 // BC
			, 100 // RP
			, 100 // PF
	};

	static int[] ds = { 0 // SC
			, 0 // LC
			, 0 // LF
			, 0 // HF
			, 0 // CR
			, 0 // BS
			, 0 // CS
			, 0 // R
			, 0 // ESP
			, 0 // B
			, 0 // D
			, 0 // RIP
			, 0 // BC
			, 0 // RP
			, 0 // PF
	};
	static int[] def = { 0 // SS
			, 0 // RL
			, 0 // LL
			, 7600 // HL
			, 0 // IC
			, 0 // GC
			, 0 // PT
			, 0 // SD
			, 0 // LD
			, 0 // CL
	};

	public static void main(String[] args) {
		Combat com = new Combat(0, 0, 0, 0, 0, 0);

		int[] ave = new int[as.length];
		int[] dave = new int[ds.length];
		int[] defave = new int[def.length];
		int[] aInfo = new int[7];

		for (int n = 0; n < runs; n++) {
			int[] a = new int[as.length];
			int[] d = new int[ds.length];
			int[] de = new int[def.length];
			for (int i = 0; i < as.length; i++)
				a[i] = as[i];
			for (int i = 0; i < ds.length; i++)
				d[i] = ds[i];
			for (int i = 0; i < def.length; i++)
				de[i] = def[i];
			int[] info = com.run(a, d, de);
			for (int i = 0; i < ave.length; i++)
				ave[i] += a[i];
			for (int i = 0; i < dave.length; i++)
				dave[i] += d[i];
			for (int i = 0; i < defave.length; i++)
				defave[i] += de[i];
			for (int i = 0; i < info.length; i++)
				aInfo[i] += info[i];
		}
		for (int i = 0; i < ave.length; i++)
			ave[i] /= runs;
		for (int i = 0; i < dave.length; i++)
			dave[i] /= runs;
		for (int i = 0; i < defave.length; i++)
			defave[i] /= runs;
		for (int i = 0; i < aInfo.length; i++)
			aInfo[i] /= runs;

		for (int i : aInfo)
			System.out.println(i);
		for (int i = 0; i < ave.length; i++)
			System.out.println(com.names[i] + " - " + ave[i]);
		System.out.println();
		for (int i = 0; i < dave.length; i++)
			System.out.println(com.names[i] + " - " + dave[i]);
		for (int i = 0; i < defave.length; i++)
			System.out.println(com.names[i+15] + " - " + defave[i]);

	}

}
