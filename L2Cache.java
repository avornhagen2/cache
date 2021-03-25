
public class L2Cache extends Cache {

	final int TAG = 0;
	final static int setSize = 512;
	ArrayListQueue alq;
	final private static int L1CtoL2 = 2;
	final private static int L2toL1C = 5;
	final private static int L2toDRAM = 3;
	final private static int DRAMtoL2 = 4;
	LineObject[] L2 = new LineObject[setSize];
	ControllerObject[] L2C = new ControllerObject[setSize];
	WriteBuffersForL1AndL2 writeBufferL2 = new WriteBuffersForL1AndL2(alq);
	
	public L2Cache(ArrayListQueue alq)
	{
		this.alq = alq;
	}
	
	public void run()
	{
		if(!alq.isSingleQueueEmpty(L1CtoL2) && alq.getHeadOfQueueWait(L1CtoL2) == false)
		{
			QueueObject messageAndWait = alq.dequeue(L1CtoL2);//make sure that there are no nulls coming through here
			String input = messageAndWait.getMessage();
			messageAndWait.setWait(true);
			
			String[] split = input.trim().split(" ");
			int Address = Integer.parseInt(split[1].substring(0, 4));
			int Tag = Address / setSize;
			int Index = Address % setSize; 
			
			
			if(split[0].contentEquals("SendToL2"))
			{
				 //Index % 512;
				if(L2[Index].getAddress() == Address )
				{
					L2[Index].setBlock(((QueueObjectChild)messageAndWait).block.getBlock());
				}else
				{
					System.out.println("Error: L2 Cache write buffer");
				}
			}else if(split[0].equals("CPURead")  || split[0].equals("CPUWrite"))
			{
				States currentState = check_StateL2(Index, Tag);
				
				if(writeBufferL2.checkValue(Tag,Index))
				{
					writeToL2FromWriteBuffer(Tag, Index, currentState, writeBufferL2.getWriteBufferValue(Tag,Index));
					currentState = check_StateL2(Index, Tag);
				}
				
				if(currentState == States.HIT)
				{
					((QueueObjectChild)messageAndWait).block.setBlock(L2[Index].getBlock());
					alq.enqueue(L2toL1C, messageAndWait);//on a hit we enqueue to L1D
				}else if(currentState == States.MISSI)
				{
					sendRequestToDRAM(input,messageAndWait.getTransactionL1());
					//alq.enqueue(L2toDRAM, messageAndWait);//on a miss we enqueue to DRAM
				}else if(currentState == States.MISSD)
				{
					
					//COME BACK TO HERE
					QueueObjectBus writeBufferObject = new QueueObjectBus();
					writeBufferL2.setWriteBufferValue(Tag, Index, L2[Index], "SendToDRAM");
					sendRequestToDRAM(input,messageAndWait.getTransactionL1());//send request to L2 to get the data
				}else if(currentState == States.MISSC)
				{
					sendRequestToDRAM(input,messageAndWait.getTransactionL1());//on a miss we enqueue to DRAM
				}
			}
		}
		
		//CPUWrite TAGindexOFFSET byteSize input.input.input 

		
		if(!alq.isSingleQueueEmpty(DRAMtoL2) && alq.getHeadOfQueueWait(DRAMtoL2) == false)
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
			
			if(busNumber == 7)
			{
				QueueObjectChild finishedBus = new QueueObjectChild(32);
				finishedBus.setMessage(messageAndWait.getMessage());
				finishedBus.setTransactionL1(messageAndWait.getTransactionL1());
				finishedBus.block.setBlock(L2[Index].getBlock());
				alq.enqueue(L2toL1C, finishedBus);
			}
			
			
		}
	}//end of run
	
	public void writeBusToL2(int Index, int offset, char[] busData)
	{
		
		for(int i = offset; i < offset + busData.length; i++)
		{
			L2[Index].setBlockValue(busData[i - offset], i);
		}
	}
	
	public void busSwitchCase(int Index, int busNumber, char[] busData)
	{
		switch(busNumber)
		{
			case 0 ://COME BACK AND CHANGE THIS TO BUSDATA
				char[] c = {'r','n','w','p'};
				writeBusToL2(Index,0,c);
				break;
			case 1 :
				char[] v = {'r','n','w','p'};
				writeBusToL2(Index,4,v);
				break;
			case 2 :
				char[] b = {'r','n','w','p'};
				writeBusToL2(Index,8,b);
				break;
			case 3 :
				char[] n = {'r','n','w','p'};
				writeBusToL2(Index,12,n);
				break;
			case 4 :
				char[] m = {'r','n','w','p'};
				writeBusToL2(Index,16,m);
				break;
			case 5 :
				char[] a = {'r','n','w','p'};
				writeBusToL2(Index,20,a);
				break;
			case 6 :
				char[] s = {'r','n','w','p'};
				writeBusToL2(Index,24,s);
				break;
			case 7 :
				char[] d = {'r','n','w','p'};
				writeBusToL2(Index,28,d);
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
	
	public void sendRequestToDRAM(String message, int transactionL1)
	{
		for(int i = 0; i < 8; i++)
		{
			QueueObjectBus bus = new QueueObjectBus();
			bus.setTransactionL1(transactionL1);
			bus.setBusNumber(i);
			bus.setMessage(message);
			bus.setWait(true);
			alq.enqueue(L2toDRAM, bus);
		}
	}

	public void populateL2() {
		
		for(int i = 0; i < setSize; i++)
		{
			LineObject temp = new LineObject(32);
			ControllerObject temp2 = new ControllerObject(i, 0, false, true);
			temp.populateLineObject();
			L2C[i] = temp2;
			L2[i] = temp;
		}
	}
	
}//end of L2CacheController
