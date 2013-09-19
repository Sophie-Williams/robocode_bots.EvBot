// -*- java -*-
package eem.misc;

public class logger {
	// logger staff
	// debug levels
	public final static int dbg_important=0;
	public final static int dbg_rutine=5;
	public final static int dbg_debuging=6;
	public final static int dbg_noise=10;
	static int verbosity_level=6; // current level, smaller is less noisy

	public logger (int vLevel) {
		verbosity_level = vLevel;
	}

	public logger () {
	}

	public static void log_message(int level, String s) {
		if (level <= verbosity_level)
			System.out.println(s);
	}

	public static void noise(String s) {
		log_message(dbg_noise, s);
	}

	public static void dbg(String s) {
		log_message(dbg_debuging, s);
	}

	public static void rutine(String s) {
		log_message(dbg_rutine, s);
	}
}

