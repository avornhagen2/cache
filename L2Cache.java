
public class L2Cache extends Cache {

	final int TAG = 0;
	final static int setSize = 512;
	ArrayListQueue alq;
	final private static int L1CtoL2 = 2;
	final private static int L2toL1C = 5;
	final private static int L2toDRAM = 3;
	final private int DRAMtoL2 = 4;
	LineObject[] L2 = new LineObject[setSize];
	ControllerObject[] L2C = new ControllerObject[setSize];
	WriteBuffersForL1AndL2 writeBufferL2 = new WriteBuffersForL1AndL2(alq);
	
	public L2Cache(ArrayListQueue alq)
	{
		this.alq = alq;
	}
	
	public void run()
	{
		if(!alq.isSingleQueueEmpty(L1CtoL2))
		{
			QueueObjectChild messageAndWait = (QueueObjectChild) alq.dequeue(L1CtoL2);//make sure that there are no nulls coming through here
			String input = messageAndWait.getMessage();
			messageAndWait.setWait(true);
			
			String[] split = input.trim().split(" ");
			int Address = Integer.parseInt(split[1].substring(0, 4));
			int Tag = Address / setSize;
			int Index = Address % setSize; 
			
			
			if(split[0] == "SendToL2")
			{
				 //Index % 512;
				if(L2[Index].getAddress() == Address )
				{
					L2[Index].setBlock(messageAndWait.block.getBlock());
				}else
				{
					System.out.println("Error: L2 Cache write buffer");
				}
			}else if(split[0] == "CPURead" || split[0] == "CPUWrite")
			{
				States currentState = check_StateL2(Index, Tag);
				
				if(writeBufferL2.checkValue(Tag,Index))
				{
					writeToL2FromWriteBuffer(Tag, Index, currentState, writeBufferL2.getWriteBufferValue(Tag,Index));
					currentState = check_StateL2(Index, Tag);
				}
				
				if(currentState == States.HIT)
				{
					messageAndWait.block.setBlock(L2[Index].getBlock());
					alq.enqueue(L2toL1C, messageAndWait);//on a hit we enqueue to L1D
				}else if(currentState == States.MISSI)
				{
					sendRequestToDRAM(input);
					//alq.enqueue(L2toDRAM, messageAndWait);//on a miss we enqueue to DRAM
				}else if(currentState == States.MISSD)
				{
					
					//COME BACK TO HERE
					QueueObjectBus writeBufferObject = new QueueObjectBus();
					writeBufferL2.setWriteBufferValue(Tag, Index, L2[Index], "SendToDRAM");
					sendRequestToDRAM(input);//send request to L2 to get the data
				}else if(currentState == States.MISSC)
				{
					sendRequestToDRAM(input);//on a miss we enqueue to DRAM
				}
			}
		}
		
		//CPUWrite TAGindexOFFSET byteSize input.input.input 

		
		if(!alq.isSingleQueueEmpty(DRAMtoL2))
		{
			QueueObjectBus messageAndWait = (QueueObjectBus) alq.dequeue(DRAMtoL2);
			String input = messageAndWait.getMessage();
			int busNumber = messageAndWait.getBusNumber();
			messageAndWait.setWait(true);
			
			String[] split = input.trim().split(" ");
			int Address = Integer.parseInt(split[1].substring(0, 4));
			int Tag = Address / setSize;
			int Index = Address % setSize; 
			
			busSwitchCase(Index,busNumber,messageAndWait.getBusData());
			
			if(busNumber == 8)
			{
				QueueObjectChild finishedBus = new QueueObjectChild();
				finishedBus.setMessage(messageAndWait.getMessage());
				finishedBus.block.setBlock(L2[Index].getBlock());
				alq.enqueue(L2toL1C, finishedBus);
			}
			
			
		}
	}//end of run
	
	public void writeBusToL2(int Index, int offset, String[] busData)
	{
		
		for(int i = offset; i < offset + busData.length; i++)
		{
			L2[Index].setBlockValue(busData[i - offset], i);
		}
	}
	
	public void busSwitchCase(int Index, int busNumber, String[] busData)
	{
		switch(busNumber)
		{
			case 1 :
				writeBusToL2(Index,0,busData);
				break;
			case 2 :
				writeBusToL2(Index,4,busData);
				break;
			case 3 :
				writeBusToL2(Index,8,busData);
				break;
			case 4 :
				writeBusToL2(Index,12,busData);
				break;
			case 5 :
				writeBusToL2(Index,16,busData);
				break;
			case 6 :
				writeBusToL2(Index,20,busData);
				break;
			case 7 :
				writeBusToL2(Index,24,busData);
				break;
			case 8 :
				writeBusToL2(Index,28,busData);
				break;
		}
			
				
		
	}
	
	
	public States check_StateL2(int Tag, int Index)
	{
		States states = null;

		if(L2C[Index].getValid() == true)
		{

			if(Tag == L2C[Index].getTag())
			{
				//check if it is a hit
				states = States.HIT;
			}
		}
			
		if(states != States.HIT)
		{
			if (L2C[Index].getValid() == false) {
				states = States.MISSI;
			}
			else
			{
				if (L2C[Index].getClean())
				{
					states = States.MISSC;
					
				}
				else
				{
					states = States.MISSD;
				}
			}
			
		}
		return states;
	}
	
	public void writeToL2FromWriteBuffer(int Tag, int Index, States currentState, LineObject data)
	{
		if(currentState == States.HIT)
		{
			//do nothing
		}else if(currentState == States.MISSI || currentState == States.MISSC)
		{
			L2[Index] = data;
			L2C[Index].setTag(Tag);
			L2C[Index].setIndex(Index);
			//L2.getL1DLineObject(Index, transaction).setBlock(data.block);
		}else if(currentState == States.MISSD)
		{
			writeBufferL2.setWriteBufferValue(L2C[Index].getTag(), Index, L2[Index], "SendToDRAM");
			L2[Index] = data;
			L2C[Index].setTag(Tag);
			L2C[Index].setIndex(Index);
		}
	}
	
	public void sendRequestToDRAM(String message)
	{
		for(int i = 0; i < 8; i++)
		{
			QueueObjectBus bus = new QueueObjectBus();
			bus.setBusNumber(i);
			bus.setMessage(message);
			alq.enqueue(L2toDRAM, bus);
		}
	}
	
}//end of L2CacheController
