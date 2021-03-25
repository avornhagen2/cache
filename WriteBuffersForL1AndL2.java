import java.util.Arrays;


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
	final private static int L2toDRAM = 3;
	ArrayListQueue alq;
	
	public WriteBuffersForL1AndL2(ArrayListQueue alq)
	{
		this.alq = alq;
	}
	
	public void setWriteBufferValue(int Tag, int Index, LineObject data, String destination)
	{
		
		if(isFull())
		{
			QueueObjectChild messageAndWait = new QueueObjectChild(32);
			

			messageAndWait.setMessage(destination + " " + Tag + " " + Index );
			messageAndWait.setWait(true);
			int record = lru.LRUMissCD();//make sure this works with static
			messageAndWait.block.setBlock(writeBufferData[record].getBlock());
			
			if(destination == "SendToL2")//make sure this works when debugging
			{
				alq.enqueue(L1DtoL1C, messageAndWait);
			}else if(destination == "SendToDRAM")
			{
				for(int i = 0; i < 8; i++)
				{
					QueueObjectBus bus = new QueueObjectBus();
					bus.setBusNumber(i);
					char[] block = new char[4];
					for(int j = 0; j < 4; j++)
					{
						block[j] = data.getBlockValue(j + i * 4);
					}
					bus.setBusData(block);
					bus.setMessage(messageAndWait.getMessage());
					alq.enqueue(L2toDRAM, bus);
				}
			}
				
			
			
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
	
//	public void setWriteBufferDirectly(int Tag, int Index, LineObject data)
//	{
//		if(isFull())
//		{
//			
//			int record = lru.LRUMissCD();//make sure this works with static
//
//			writeBufferInstruction[record][0] = Tag; 
//			writeBufferInstruction[record][1] = Index;
//			
//			writeBufferData[record] = data;
//
//		}else {
//			
//			int record = lru.LRUMissI();
//			
//			writeBufferInstruction[record][0] = Tag; 
//			writeBufferInstruction[record][1] = Index;
//			
//			writeBufferData[record] = data;
//		}
//	}
	
	public boolean checkValue(int Tag, int Index)
	{
		boolean exists = false;
		for(int i = 0; i < row; i++)
		{
			if(writeBufferInstruction[i][0] == Tag && writeBufferInstruction[i][1] == Index)
			{
				exists = true;
			}
		}
		return exists;
	}
	
	public LineObject getWriteBufferValue(int Tag, int Index)
	{
		LineObject output = null;
		for(int i = 0; i < row; i++)
		{
			if(writeBufferInstruction[i][0] == Tag && writeBufferInstruction[i][1] == Index)
			{
				output = writeBufferData[i];
				writeBufferData[i] = new LineObject(32);
				writeBufferInstruction[i][0] = -1;
				writeBufferInstruction[i][1] = -1;
				lru.LRUMissI();
			}
		}
		return output;
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
