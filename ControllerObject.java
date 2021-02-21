
public class ControllerObject {

	private static int Tag;
	private static int Index;
	private static boolean Valid;
	
	
	public ControllerObject(int tag, int index, boolean valid) {
		super();
		Tag = tag;
		Index = index;
		Valid = valid;
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


	
	 
	
}//end of ControllerObject
