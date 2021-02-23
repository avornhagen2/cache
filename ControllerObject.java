
public class ControllerObject {

	private static int Tag;
	private static int Index;
	private static boolean Valid;
	private static boolean Clean;
	
	
	public ControllerObject(int tag, int index, boolean valid, boolean clean) {
		super();
		Tag = tag;
		Index = index;
		Valid = valid;
		Clean = clean;
	}//end of constructor


	public int getTag() {
		return Tag;
	}


	public void setTag(int tag) {
		Tag = tag;
	}


	public int getIndex() {
		return Index;
	}


	public void setIndex(int index) {
		Index = index;
	}


	public boolean getValid() {
		return Valid;
	}


	public void setValid(boolean valid) {
		Valid = valid;
	}


	public static boolean getClean() {
		return Clean;
	}


	public static void setClean(boolean clean) {
		ControllerObject.Clean = clean;
	}

	
	
	 
	
}//end of ControllerObject
