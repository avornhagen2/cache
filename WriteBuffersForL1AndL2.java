import java.util.ArrayList;
import java.util.Arrays;


public class WriteBuffersForL1AndL2 {

	final int row = 2;
	final int column = 2;
	final int setSize = 2;
	int[][] writeBufferInstruction = new int[row][column];
	LineObject[] writeBufferData = new LineObject[setSize];
	LRU lru = new LRU(row);
	//boolean notFull = false;
	int[] valid = new int[] {-1,-1};
	final private static int L1DtoL1C = 6;
	final private static int L2toDRAM = 3;
	final private static int L2toL1C = 5;
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
			messageAndWait.setWait(true);
			int record = lru.LRUMissCD();//make sure this works with static
			String newAddress = String.format("%04d",writeBufferInstruction[record][0]*64 + writeBufferInstruction[record][1]);
			messageAndWait.setMessage(destination + " " + newAddress + " 0");
			messageAndWait.block.setBlock(writeBufferData[record].getBlock());
			if(destination == "SendToL2")//make sure this works when debugging
			{
				valid[record] = 1;
				alq.enqueue(L1DtoL1C, messageAndWait);
//				lru.LRU[record] = -1;
//				valid[record] = -1;
			}else if(destination == "SendToDRAM")
			{
				messageAndWait.setMessage("MutualInclusionCheckDirty " + newAddress + " 0");
				sendRequestToDestination(messageAndWait, L2toL1C, alq);
//				lru.LRU[record] = -1;
//				valid[record] = -1;
//				for(int i = 0; i < 8; i++)
//				{
//					QueueObjectBus bus = new QueueObjectBus();
//					bus.setBusNumber(i);
//					char[] block = new char[4];
//					for(int j = 0; j < 4; j++)
//					{
//						block[j] = data.getBlockValue(j + i * 4);
//					}
//					bus.setBusData(block);
//					bus.setMessage(messageAndWait.getMessage());
//					alq.enqueue(L2toDRAM, bus);
//				}
				
			}

			writeBufferInstruction[record][0] = Tag; 
			writeBufferInstruction[record][1] = Index;			
			writeBufferData[record].setBlock(data.getBlock());
			writeBufferData[record].setAddress(data.getAddress());

		}else {
			
			int record = lru.LRUMissI();
			writeBufferInstruction[record][0] = Tag; 
			writeBufferInstruction[record][1] = Index;
			valid[record] = 1;
			writeBufferData[record].setBlock(data.getBlock());
			writeBufferData[record].setAddress(data.getAddress());
			System.out.println("Set Write Buffer Success");
		}
	}
	

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
				valid[i] = -1;
				lru.LRU[i] = -1;
			}
		}
		System.out.println("Read from Write Buffer Success");
		return output;
	}
	
	public boolean isFull()
	{
		boolean full = true;
		for(int i = 0; i < setSize; i++)
		{
			if(valid[i] == -1)
			{
				full = false;
				break;
			}
		}
		return full;
	}
	
	public void sendRequestToDestination(QueueObject newHead, int destination, ArrayListQueue alq) 
	{
		ArrayList<QueueObject> oldQueueObjectsHolder = new ArrayList<QueueObject>();
		//mutualInclusion = true;
		while(!alq.isSingleQueueEmpty(destination))
		{
			oldQueueObjectsHolder.add(alq.dequeue(destination));
		}
		
		alq.enqueue(destination, newHead);
		
		for(int i = 0; i < oldQueueObjectsHolder.size(); i++)
		{
			alq.enqueue(destination, oldQueueObjectsHolder.get(i));
		}
	}

	
}//end of write buffers for l1 and l2
