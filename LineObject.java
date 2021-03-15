
public class LineObject {

	final static int blockSize = 32;
	String[] block = new String[blockSize];
	int Address;
	
	//boolean clean = true;
	
	
	
//	public LineObject(String[] block) {
//		super();
//		this.block = block;
//	}


	

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

	public int getAddress()
	{
		return Address;
	}
	
	public int getIndex(int setSize)
	{
		int Index;
		Index = Address % setSize;
		return Index;
	}
	
	public int getTag(int setSize)
	{
		int Tag;
		Tag = Address / setSize;
		return Tag;
	}
	
	public void setAddress(int input)
	{
		Address = input;
	}

//	public static String getAllBlockValues() {
//		String temp = "";
//		for(int i = 0; i < blockSize; i++)
//		{
//			
//		}
//	}






	



	
	
	
}//end of blockobject class
