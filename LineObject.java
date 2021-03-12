
public class LineObject {

	final static int blockSize = 32;
	String[] block = new String[blockSize];

	//boolean clean = true;
	
	
	
	public LineObject(String[] block) {
		super();
		this.block = block;
	}


	

	public String[] getBlock() {
		return block;
	}

	public String getBlockValue(int byteNumber) {
		return block[byteNumber];
	}

	public void setBlock(String[] bytes) {
		this.block = block;
	}

	public void setBlockValue(String input, int value) {
		 block[value] = input;
	}


//	public static String getAllBlockValues() {
//		String temp = "";
//		for(int i = 0; i < blockSize; i++)
//		{
//			
//		}
//	}






	



	
	
	
}//end of blockobject class
