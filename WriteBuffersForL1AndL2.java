
public class WriteBuffersForL1AndL2 {

	final int row = 2;
	final int column = 2;
	final int setSize = 2;
	int[][] writeBufferInstruction = new int[row][column];
	LineObject[] writeBufferData = new LineObject[setSize];
	LRU[] lru = new LRU[row];
	
	public void setWriteBufferValue(int Tag, int Index, String[] data)
	{
		
	}
	
}//end of write buffers for l1 and l2
