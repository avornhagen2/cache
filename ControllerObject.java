
public class ControllerObject {

	private int Tag;
	private int Index;
	private boolean Valid;
	private boolean Clean;
	private boolean busy;

	
	
	
	public ControllerObject(int tag, int index, boolean valid, boolean clean) {
		super();
		Tag = tag;
		Index = index;
		Valid = valid;
		Clean = clean;
		busy = false;
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


	public boolean getClean() {
		return Clean;
	}


	public void setClean(boolean clean) {
		Clean = clean;
	}


	 
	
}//end of ControllerObject
