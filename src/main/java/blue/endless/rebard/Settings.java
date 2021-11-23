package blue.endless.rebard;

// This class has been gutted until further notice, to prevent files from being stuffed into ~/.java/.userPrefs/
// My philosophy is much more of a portable-install mindset; this will probably migrate to local .cfg or .json 

public class Settings {
	
	//private static Preferences pref;
	
	public Settings() {
		try {
			//pref = Preferences.userNodeForPackage(Settings.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void SaveBool(String key, boolean value){
		try {
			//pref.putBoolean(key, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean LoadBool(String key){
		//if(pref != null) return pref.getBoolean(key, false);
		return false;
	}
	
	public static void SaveInt(String key, int value){
		try {
			//pref.putInt(key, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int LoadInt(String key){
		//if(pref != null) return pref.getInt(key, -1);
		return -1;
	}
	
	public static void SaveDouble(String key, double value){
		try {
			//pref.putDouble(key, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double LoadDouble(String key){
		//if(pref != null) return pref.getDouble(key, -1);
		return -1;
	}
	
}
