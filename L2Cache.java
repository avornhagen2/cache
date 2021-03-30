import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

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
	WriteBuffersForL1AndL2 writeBufferL2;
	boolean mutualInclusion;
	boolean[] busyCheckL2 = new boolean[setSize];
	ArrayList<QueueObject> InstructionsL2 = new ArrayList<QueueObject>();
	
	public L2Cache(ArrayListQueue alq, boolean mutualInclusion)
	{
		this.alq = alq;
		this.mutualInclusion = mutualInclusion;
		this.writeBufferL2  = new WriteBuffersForL1AndL2(alq);
	}
	
	public void run()
	{
		if(!alq.isSingleQueueEmpty(L1CtoL2) && alq.getHeadOfQueueWait(L1CtoL2) == false)
		{
			QueueObject instructionObject = alq.dequeue(L1CtoL2);
			instructionObject.setWait(false);
			InstructionsL2.add(instructionObject);
		}
			
		if(!InstructionsL2.isEmpty())
		{
			boolean flag = true;
			int i = 0;
			while(flag && i < 8)
			{
				if(InstructionsL2.size()-1 < i)
				{
					flag = false;
				}else {
					QueueObject messageAndWait = InstructionsL2.get(i);
					String input = messageAndWait.getMessage();
					messageAndWait.setWait(true);

					String[] split = input.trim().split(" ");
					int Address = Integer.parseInt(split[1].substring(0, 4));
					int Tag = Address / setSize;
					int Index = Address % setSize; 

					if(!busyCheckL2[Index])
					{
						busyCheckL2[Index] = true;
						InstructionsL2.remove(i);
						messageAndWait.setWait(true);
						flag = false;

					

						if(split[0].contentEquals("SendToL2"))
						{
							if(!Arrays.equals(L2[Index].getBlock(), ((QueueObjectChild) messageAndWait).block.getBlock()))
							{
								L2C[Index].setClean(false);
							}else {
								L2C[Index].setClean(true);
							}
							L2[Index].setBlock(((QueueObjectChild)messageAndWait).block.getBlock());
							L2[Index].setAddress(Address);
							busyCheckL2[Index] = false;

						}else if(split[0].equals("CPURead")  || split[0].equals("CPUWrite"))
						{
							States currentState = check_StateL2(Tag, Index);

							if(writeBufferL2.checkValue(Tag,Index))
							{
								writeToL2FromWriteBuffer(Tag, Index, currentState, writeBufferL2.getWriteBufferValue(Tag,Index));
								currentState = check_StateL2(Tag, Index);
							}

							if(currentState == States.HIT)
							{
								QueueObjectChild qoc = new QueueObjectChild(32);
								qoc.setMessage(messageAndWait.getMessage());
								qoc.setTransactionL1(messageAndWait.getTransactionL1());
								qoc.setWait(messageAndWait.getWait());
								qoc.block.setBlock(L2[Index].getBlock());
								alq.enqueue(L2toL1C, qoc);//on a hit we enqueue to L1D
								busyCheckL2[Index] = false;
							}else if(currentState == States.MISSI)
							{
								sendRequestToDRAM(input,messageAndWait.getTransactionL1());
								//alq.enqueue(L2toDRAM, messageAndWait);//on a miss we enqueue to DRAM
							}else if(currentState == States.MISSD)
							{
								QueueObjectBus writeBufferObject = new QueueObjectBus();
								writeBufferL2.setWriteBufferValue(L2[Index].getTag(setSize), Index, L2[Index], "SendToDRAM");
								sendRequestToDRAM(input,messageAndWait.getTransactionL1());
							}else if(currentState == States.MISSC)
							{
								
								QueueObjectChild head = new QueueObjectChild(32);
								String replaceAddress = String.format("%04d",L2C[Index].getIndex() + (L2C[Index].getTag() * setSize));
								head.setMessage("MutualInclusionCheckClean " + replaceAddress + "00 0");
								head.block.setBlock(L2[Index].getBlock());
								sendRequestToDestination(head,L2toL1C,alq);
								sendRequestToDRAM(input,messageAndWait.getTransactionL1());//on a miss we enqueue to DRAM
								L2C[Index].setValid(false);
							}
						}else if(split[0].contentEquals("MutualInclusionChecktoDRAM"))
						{
							LineObject data = new LineObject(32);
							data.setBlock(((QueueObjectChild)messageAndWait).block.getBlock());
							data.setAddress(Address);
							sendDataToDRAM(data,((QueueObjectChild)messageAndWait));
						}
						
					}


				}
				i++;
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
				L2[Index].setAddress(Address);
				L2C[Index].setValid(true);
				L2C[Index].setTag(Tag);
				L2C[Index].setIndex(Index);
				L2C[Index].setClean(true);
				QueueObjectChild finishedBus = new QueueObjectChild(32);
				finishedBus.setMessage(messageAndWait.getMessage());
				finishedBus.setTransactionL1(messageAndWait.getTransactionL1());
				finishedBus.block.setBlock(L2[Index].getBlock());
				busyCheckL2[Index] = false;
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
		busyCheckL2[Index] = false;
	}
	
	public void busSwitchCase(int Index, int busNumber, char[] busData)
	{
		switch(busNumber)
		{
			case 0 :
				writeBusToL2(Index,0,busData);
				break;
			case 1 :
				writeBusToL2(Index,4,busData);
				break;
			case 2 :
				writeBusToL2(Index,8,busData);
				break;
			case 3 :
				writeBusToL2(Index,12,busData);
				break;
			case 4 :
				writeBusToL2(Index,16,busData);
				break;
			case 5 :
				writeBusToL2(Index,20,busData);
				break;
			case 6 :
				writeBusToL2(Index,24,busData);
				break;
			case 7 :
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
		}else if(currentState == States.MISSI)
		{
			L2[Index] = data;
			L2C[Index].setTag(Tag);
			L2C[Index].setIndex(Index);
			busyCheckL2[Index] = false;
		}else if(currentState == States.MISSC)
		{
			QueueObjectChild head = new QueueObjectChild(32);
			String newAddress = String.format("%04d",L2[Index].getAddress());
			head.block.setBlock(L2[Index].getBlock());
			head.setMessage("MutualInclusionCheckClean " + newAddress + "00 0");
			sendRequestToDestination(head, L2toL1C, alq);
			
			L2[Index] = data;
			L2C[Index].setTag(Tag);
			L2C[Index].setIndex(Index);
			busyCheckL2[Index] = false;
			//L2.getL1DLineObject(Index, transaction).setBlock(data.block);
		}else if(currentState == States.MISSD)
		{
			writeBufferL2.setWriteBufferValue(L2C[Index].getTag(), Index, L2[Index], "SendToDRAM");
			L2[Index] = data;
			L2C[Index].setTag(Tag);
			L2C[Index].setIndex(Index);
			busyCheckL2[Index] = false;
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

	public void sendDataToDRAM(LineObject data,QueueObjectChild messageAndWait)
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
	
	public void populateL2() {
		
		for(int i = 0; i < setSize; i++)
		{
			LineObject temp = new LineObject(32);
			ControllerObject temp2 = new ControllerObject(0, i, false, true);
			temp.populateLineObject();
			L2C[i] = temp2;
			L2[i] = temp;
		}
		
		for(int i = 0; i < 2; i++)
		{
			LineObject temp = new LineObject(32);
			writeBufferL2.writeBufferData[i] = temp;
		}
	}
	
}//end of L2CacheController
