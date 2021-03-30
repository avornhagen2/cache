
public class LineObject {

	int blockSize;
	private char[] block;
	int Address;
	
	//boolean clean = true;
	
	public LineObject(int blockSize)
	{
		this.blockSize = blockSize;
		block = new char[blockSize];
	}
	
//	public LineObject(String[] block) {
//		super();
//		this.block = block;
//	}



	public char[] getBlock() {
		return block;
	}

	public char getBlockValue(int byteNumber) {
		return block[byteNumber];
	}

	public void setBlock(char[] block) {
		for(int i = 0; i < block.length; i++)
		{
			this.block[i] = block[i];
		}
	}

	public void setBlockValue(char input, int value) {
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


	public void populateLineObject()
	{
		for(int i = 0; i < blockSize; i++)
		{
			block[i] = '1';
		}
	}





	



	
	
	
}//end of blockobject class
