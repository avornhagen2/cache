
public class WriteBuffersForL1AndL2 {

	final int row = 2;
	final int column = 2;
	final int setSize = 2;
	int[][] writeBufferInstruction = new int[row][column];
	LineObject[] writeBufferData = new LineObject[setSize];
	LRU lru = new LRU(row);
	//boolean notFull = false;
	int[] tracking = new int[] {-1,-1};
	final private static int L1DtoL1C = 6;
	
	public void setWriteBufferValue(int Tag, int Index, LineObject data, ArrayListQueue alq)
	{
		if(isFull())
		{
			QueueObjectChild messageAndWait = new QueueObjectChild();
			

			messageAndWait.setMessage("SendToL2 " + Tag + " " + Index );
			messageAndWait.setWait(true);
			int record = lru.LRUMissCD();//make sure this works with static
			messageAndWait.block.setBlock(writeBufferData[record].getBlock());
			alq.enqueue(L1DtoL1C, messageAndWait);	
			
			writeBufferInstruction[record][0] = Tag; 
			writeBufferInstruction[record][1] = Index;
			
			writeBufferData[record] = data;
			
			
			
		}else {
			
			int record = lru.LRUMissI();
			writeBufferInstruction[record][0] = Tag; 
			writeBufferInstruction[record][1] = Index;
			
			writeBufferData[record] = data;
		}
	}
	
	public void getWriteBufferValue()
	{
		
	}
	
	public boolean isFull()
	{
		boolean full = true;
		for(int i = 0; i < setSize; i++)
		{
			if(tracking[i] == -1)
			{
				full = false;
				break;
			}
		}
		return full;
	}
	
	
	
}//end of write buffers for l1 and l2
