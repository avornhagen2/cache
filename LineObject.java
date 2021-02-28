
public class LineObject {

	final int blockSize = 32;
	String[] block = new String[blockSize];
	//boolean clean = true;
	
	
	
	public LineObject(String[] block) {
		super();
		this.block = block;
	}


	

	public String[] getBlock() {
		return block;
	}



	public void setBlock(String[] bytes) {
		this.block = block;
	}

	public void setBlockValue(String input, int value) {
		 block[value] = input;
	}


	public static void CheckForBits() {
		
	}

	


//	public boolean getClean() {
//		return clean;
//	}
//
//
//
//
//	public void setClean(boolean clean) {
//		this.clean = clean;
//	}
//	
	
	
	
}//end of blockobject class
