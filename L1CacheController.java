import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class L1CacheController extends Cache
{	
	
	
	public int transaction = -1;
	//we are going to be using FIFO for replacing data
	public ControllerObject Address;
	final private static int CPUtoL1C = 0;
	final private static int L1CtoL1D = 1;
	final private static int L1CtoL2 = 2;
	final private static int L2toL1C = 5;
	final private static int L1DtoL1C = 6;
	final private static int L1CtoCPU = 7;
	//final public int INDEX = 6; 
	//final public int TAG = 6;
	//static int COLUMN;
	static ControllerObject L1C[][];// = new ControllerObject[NUMBER_SETS][SET_SIZE];
	static L1Data L1D;// = new L1Data(NUMBER_SETS,SET_SIZE);//put inside the constructor
	static VictimCacheForL1 victim;// = new VictimCacheForL1();
	static ArrayListQueue alq;
	static WriteBuffersForL1AndL2 writeBuffer;// = new WriteBuffersForL1AndL2(alq);
	LRU[] lru;
	ArrayList<String> InstructionsL1 = new ArrayList<String>();
	ArrayList<Integer> busyAddresses;// = new ArrayList<Integer>();
	boolean mutualInclusion;
	boolean[][] busyCheckL1; 
	
	
	
	//static int[] fifoCounter = new int[NUMBER_SETS];
	public L1CacheController (ArrayListQueue alq, L1Data L1D, ArrayList<Integer> busyAddresses, boolean mutualInclusion, boolean[][] busyCheckL1) {
		this.L1D = L1D;
		this.alq = alq;
		SET_SIZE = 4;
		NUMBER_SETS = 64;
		L1C = new ControllerObject[NUMBER_SETS][SET_SIZE];
		this.busyAddresses = busyAddresses;
		lru = new LRU[NUMBER_SETS];
		this.writeBuffer = L1D.writeBuffer;
		this.victim = L1D.victim;
		this.mutualInclusion = mutualInclusion;
		this.busyCheckL1 = busyCheckL1;
		populateLRU();
	}

	
	//OFFSET = log2(width of block/smallest number of bytes accessed) = log2(32b/1b) = 5
			
	//INDEX = log2(cache size/(block size * number of ways)) = log2(8kb/(32*4)) = log2(2^13/2^7) = 6
	
	//TAG = main memory size - (index + offset) = 17 - (5 + 6) = 6
	
	//lines = cache size/block size = 8KB/32B = 256B
	
	//Sets = lines/ways = 64B
	
	//sample input:
	//CPURead TAGindexOFFSET byteSize
	//CPUWrite TAGindexOFFSET byteSize input.input.input 
	//CPUWrite TAGindexOFFSET byteSize 1.2.3
	//CPUWrite TAGindexOFFSET byteSize 19.20.21
	
	//CPURead 000040950005
	//CPURead 4095
	
	//CPURead 026005 8 1.2.3.4.5.6.7.8
	//WriteBuffer 025 993 
	//VictimCache 037 274
	
	//SendToL2 024 023
	
	/*
	 * KEY FOR INDEXES OF QUEUES
	 * 0. CPU to L1C
	 * 1. L1C to L1D
	 * 2. L1C to L2
	 * 3. L2 to DRAM
	 * 4. DRAM to L2
	 * 5. L2 to L1C
	 * 6. L1D to L1C
	 * 7. L1C to CPU
	 */
	
	
	public void run()
	{
		if(!alq.isSingleQueueEmpty(CPUtoL1C)  && alq.getHeadOfQueueWait(CPUtoL1C) == false) {
			
			InstructionsL1.add(alq.dequeue(CPUtoL1C).getMessage());
		}
		
		
		
		
		if(!InstructionsL1.isEmpty())//CPU to L1C
		{
			
			boolean flag = true;
			int i = 0;
			int Address = 0;
			String message = "";
			QueueObject messageAndWait = new QueueObject();
			
			while(flag && i < 8)
			{
				if(InstructionsL1.size()-1 < i)
				{
					flag = false;
				}else{ 

					message = InstructionsL1.get(i);
					String[] splitInstructions = message.trim().split(" ");
					Address = Integer.parseInt(splitInstructions[1].substring(0, 4));
					int Tag = Address / NUMBER_SETS;
					int Index = Address % NUMBER_SETS; 
					int col = 0;
					

					for(int j=0; j < L1C[Index].length;j++)
					{
						if(L1C[Index][j].getValid() == false)
						{
							col = j;
							break;
						}else {
							col = lru[Index].head();
						}
					}
						
					if(!busyAddresses.contains(Address) && !busyCheckL1[Index][col])
					{
						busyAddresses.add(Address);
						messageAndWait.setMessage(message);
						messageAndWait.setWait(true);
						InstructionsL1.remove(i);
						flag = false;
						
						
						//String[] split = message.trim().split(" ");
						//int Address = Integer.parseInt(split[1].substring(0, 4));
						//int Tag = Address / NUMBER_SETS;
						//int Index = Address % NUMBER_SETS; 
						
						
						States currentState = check_StateL1(Index, Tag);
						
						busyCheckL1[Index][transaction] = true;
						
						L1C[Index][transaction].setValid(true);
						
						if(splitInstructions[0].contentEquals("CPUWrite"))
						{
							L1C[Index][transaction].setClean(false);
							//System.out.println("MISSD");
						}
						
						if(writeBuffer.checkValue(Tag,Index))
						{
							
							writeToL1FromWriteBuffer(Tag, Index, currentState, writeBuffer.getWriteBufferValue(Tag,Index).getBlock());
							currentState = check_StateL1(Index, Tag);
						}
						
						if(victim.checkValue(Tag,Index))
						{
							victim.getVictimCacheValue(Tag, Index);
							currentState = check_StateL1(Index, Tag);
						}
					
						
						//currentState = check_StateL1(Index, Tag);
						messageAndWait.setTransactionL1(transaction);

						if(currentState == States.HIT)
						{
							alq.enqueue(L1CtoL1D, messageAndWait);//on a hit we enqueue to L1D
//							L1C[Index][messageAndWait.getTransactionL1()].setValid(true);
							L1C[Index][messageAndWait.getTransactionL1()].setTag(Tag);
						}else if(currentState == States.MISSI)
						{
							messageAndWait.setTransactionL1(transaction);
							alq.enqueue(L1CtoL2, messageAndWait);//on a miss we enqueue to L2
						}else if(currentState == States.MISSD)
						{
							QueueObject writeBufferObject = new QueueObject();
							String newAddress = String.format("%04d",L1C[Index][transaction].getTag()*64 + L1C[Index][transaction].getIndex());
							String writeBufferMessage = "WriteBuffer " + newAddress + "00 0";
							writeBufferObject.setMessage(writeBufferMessage);
							writeBufferObject.setTransactionL1(transaction);
							writeBufferObject.setWait(true);
							L1C[Index][transaction].setValid(false);
							messageAndWait.setTransactionL1(transaction);
							
							alq.enqueue(L1CtoL1D, writeBufferObject);
							
							alq.enqueue(L1CtoL2, messageAndWait);//send request to L2 to get the data
						}else if(currentState == States.MISSC)
						{
							QueueObject victimCacheObject = new QueueObject();
							String newAddress = String.format("%04d",L1C[Index][transaction].getTag()*64 + L1C[Index][transaction].getIndex());
							String victimCacheMessage = "VictimCache " + newAddress + "00 0";
							victimCacheObject.setMessage(victimCacheMessage);
							victimCacheObject.setTransactionL1(transaction);
							victimCacheObject.setWait(true);
							L1C[Index][transaction].setValid(false);
							messageAndWait.setTransactionL1(transaction);
							
							alq.enqueue(L1CtoL1D, victimCacheObject);
						
							alq.enqueue(L1CtoL2, messageAndWait);
						}	
					}
					
					i++;
				}
				
			}
			
			
			
			
			
//			if(split.length == 3)//this is Read
//			{
//				readFromL1Data(Tag, Index, Offset, Integer.parseInt(split[2]), tagAndIndex);
//			}else if(split.length == 4)//this is a write
//			{
//				String[] data = split[3].trim().split(".");
//				writeToL1Data(Tag, Index, Offset, Integer.parseInt(split[2]), tagAndIndex, data);
//			}else {
//				throw new Exception("invalid input");
//			}
			
		}
		
		if(!alq.isSingleQueueEmpty(L1DtoL1C) && alq.getHeadOfQueueWait(L1DtoL1C) == false) //L1D to L1C
		{
			QueueObject messageAndWait = alq.dequeue(L1DtoL1C);
			String input = messageAndWait.getMessage();
			messageAndWait.setWait(true);
			
			String[] split = input.trim().split(" ");
			//int Address = Integer.parseInt(split[1].substring(0, 4));
			//int Tag = Address / NUMBER_SETS;
			//int Index = Address % NUMBER_SETS; 
			
			if(split[0].equals("SendToL2"))
			{
				alq.enqueue(L1CtoL2, messageAndWait);
			}else if(split[0].equals("CPURead"))
			{
				alq.enqueue(L1CtoCPU, messageAndWait);
			}else if(split[0].equals("UpdateWriteL1"))
			{
				//alq.enqueue(L1CtoCPU, messageAndWait);
				
			}else if(split[0].equals("MutualInclusionChecktoDRAM"))
			{
				sendRequestToDestination(messageAndWait, L1CtoL2, alq);
			}
		
		}
		
		if(!alq.isSingleQueueEmpty(L2toL1C) && alq.getHeadOfQueueWait(L2toL1C) == false) //L2 to L1C
		{
			QueueObjectChild messageAndWait = (QueueObjectChild) alq.dequeue(L2toL1C);
			String input = messageAndWait.getMessage();
			messageAndWait.setWait(true);
			
			String[] split = input.trim().split(" ");
			String wordAddress = split[1].substring(0, 4);
			int Address = Integer.parseInt(split[1].substring(0, 4));
			int Tag = Address / NUMBER_SETS;
			int Index = Address % NUMBER_SETS; 
			int byteSize = Integer.parseInt(split[2]);
			int Offset = Integer.parseInt(split[1].substring(4,6));
			
			if(!split[0].equals("MutualInclusionCheckClean") && !split[0].equals("MutualInclusionCheckDirty"))
			{
				L1C[Index][messageAndWait.getTransactionL1()].setValid(true);
				L1C[Index][messageAndWait.getTransactionL1()].setTag(Tag);
			}
			
			if(split[0].equals("CPURead"))
			{

				QueueObjectChild qocCPU = new QueueObjectChild(byteSize);
				
				
				for(int i = Offset; i < byteSize + Offset;i++)
				{
					qocCPU.block.setBlockValue(messageAndWait.block.getBlockValue(i-Offset), i-Offset);
					//output[i-Offset] = messageAndWait.block.getBlockValue(i);
				}
				
				qocCPU.setMessage(messageAndWait.getMessage());
				qocCPU.setWait(true);
				alq.enqueue(L1CtoCPU, qocCPU);
				
				QueueObjectChild newMessage = messageAndWait;
				newMessage.setMessage("UpdateReadL1 " + wordAddress + "00 " + "32");
				newMessage.setWait(true);
				alq.enqueue(L1CtoL1D, newMessage);
				//send to CPU
			}else if(split[0].equals("CPUWrite"))
			{
				QueueObjectChild newQueueObject = messageAndWait;
				char[] c = split[3].toCharArray();
				newQueueObject.setMessage("UpdateWriteL1 " + wordAddress + "00 " + "32" + " " + split[3]);
				for(int i = 0; i < byteSize; i++)
				{
					newQueueObject.block.setBlockValue(c[i], Offset + i);
				}
				alq.enqueue(L1CtoL1D, newQueueObject);
			}else if(split[0].equals("MutualInclusionCheckClean"))
			{
				QueueObjectChild head = new QueueObjectChild(32);
				boolean inL1 = false;
				
			
				
				for(int i = 0; i < 2; i++)
				{
					String newAddress = String.format("%04d",Address);
					
					if(writeBuffer.writeBufferInstruction[i][0] == Tag && writeBuffer.writeBufferInstruction[i][1] == Index)
					{
						inL1 = true;
						writeBuffer.valid[i] = -1;
						if(!Arrays.equals(messageAndWait.block.getBlock(), writeBuffer.writeBufferData[i].getBlock()))
						{
							head.block.setBlock(writeBuffer.writeBufferData[i].getBlock());
							head.setMessage("MutualInclusionChecktoDRAM " + newAddress + "00 0");
							sendRequestToDestination(head, L1CtoL2, alq);
						}
						
					}else if(victim.victimInstruction[i][0] == Tag && victim.victimInstruction[i][1] == Index)
					{
						inL1 = true;
						victim.valid[i] = -1;
						if(!Arrays.equals(messageAndWait.block.getBlock(), victim.victimData[i].getBlock()))
						{
							head.block.setBlock(victim.victimData[i].getBlock());
							head.setMessage("MutualInclusionChecktoDRAM " + newAddress + "00 0");
							sendRequestToDestination(head, L1CtoL2, alq);
						}
					}
				}
				
				for(int i = 0; i < SET_SIZE; i++)
				{
					if(L1C[Index][i].getTag() == Tag)
					{
						head.setMessage(messageAndWait.getMessage());
						head.block.setBlock(messageAndWait.block.getBlock());
						head.setTransactionL1(i);
						sendRequestToDestination(head, L1CtoL1D, alq);
						inL1 = true;
						L1C[Index][i].setValid(false);
					}	
				}
				
				if(inL1 == false)
				{
					head.setMessage("MutualInclusionPass " + "000000 0");
					sendRequestToDestination(head, L1CtoL2, alq);
				}
			}else if(split[0].equals("MutualInclusionCheckDirty"))
			{
				QueueObjectChild head = new QueueObjectChild(32);
				String newAddress = String.format("%04d",Address);
				boolean inL1 = false;
				
				for(int i = 0; i < 2; i++)
				{
					if(writeBuffer.writeBufferInstruction[i][0] == Tag && writeBuffer.writeBufferInstruction[i][1] == Index)
					{
						inL1 = true;
						writeBuffer.valid[i] = -1;
						if(!Arrays.equals(messageAndWait.block.getBlock(), writeBuffer.writeBufferData[i].getBlock()))
						{
							head.block.setBlock(writeBuffer.writeBufferData[i].getBlock());
							head.setMessage("MutualInclusionChecktoDRAM " + newAddress + "00 0");
							sendRequestToDestination(head, L1CtoL2, alq);
						}
						
					}else if(victim.victimInstruction[i][0] == Tag && victim.victimInstruction[i][1] == Index)
					{
						inL1 = true;
						victim.valid[i] = -1;
						if(!Arrays.equals(messageAndWait.block.getBlock(), victim.victimData[i].getBlock()))
						{
							head.block.setBlock(victim.victimData[i].getBlock());
							head.setMessage("MutualInclusionChecktoDRAM " + newAddress + "00 0");
							sendRequestToDestination(head, L1CtoL2, alq);
						}
					}
				}
				
				for(int i = 0; i < SET_SIZE; i++)
				{
					if(L1C[Index][i].getTag() == Tag)
					{
						head.setMessage(messageAndWait.getMessage());
						head.block.setBlock(messageAndWait.block.getBlock());
						head.setTransactionL1(i);
						sendRequestToDestination(head, L1CtoL1D, alq);
						inL1 = true;
						L1C[Index][i].setValid(false);
					}	
				}
				
				if(inL1 == false)
				{
					head.setMessage("MutualInclusionChecktoDRAM " + newAddress + "00 0");
					head.block.setBlock(messageAndWait.block.getBlock());
					sendRequestToDestination(head, L1CtoL2, alq);
				}
			}
		}

		
	}// end of readFromCPU
	
	
	public void writeToL1FromWriteBuffer(int Tag, int Index, States currentState, char[] data)
	{
		if(currentState == States.HIT)
		{
			//do nothing
		}else if(currentState == States.MISSI)
		{
			L1D.getL1DLineObject(Index, transaction).setBlock(data);
			L1C[Index][transaction].setTag(Tag);
			L1C[Index][transaction].setIndex(Index);
		}else if(currentState == States.MISSD)
		{
			
			writeBuffer.setWriteBufferValue(L1C[Index][transaction].getTag(), Index, L1D.getL1DLineObject(Index, transaction), "SendToL2");
			L1D.getL1DLineObject(Index, transaction).setBlock(data);
			L1C[Index][transaction].setTag(Tag);
			L1C[Index][transaction].setValid(true);
		}else if(currentState == States.MISSC)
		{
			victim.setVictimCacheValueDirectly(L1C[Index][transaction].getTag(), Index, L1D.getL1DLineObject(Index, transaction));
			L1D.getL1DLineObject(Index, transaction).setBlock(data);
			L1C[Index][transaction].setTag(Tag);
		}
	}
	
	public void writeToL1FromVictimCache(int Tag, int Index, States currentState, LineObject data)
	{
		if(currentState == States.HIT)
		{
			//do nothing
		}else if(currentState == States.MISSI)
		{
			L1D.getL1DLineObject(Index, transaction).setBlock(data.getBlock());
		}else if(currentState == States.MISSD)
		{
			writeBuffer.setWriteBufferValue(L1C[Index][transaction].getTag(), Index, L1D.getL1DLineObject(Index, transaction), "SendToL2");
			L1D.getL1DLineObject(Index, transaction).setBlock(data.getBlock());
			L1C[Index][transaction].setTag(Tag);
			L1C[Index][transaction].setValid(false);
		}else if(currentState == States.MISSC)
		{
			victim.setVictimCacheValueDirectly(L1C[Index][transaction].getTag(), Index, L1D.getL1DLineObject(Index, transaction));
			L1D.getL1DLineObject(Index, transaction).setBlock(data.getBlock());
			L1C[Index][transaction].setTag(Tag);
		}
	}
	
	public States check_StateL1(int index, int tag)
	{
		int numberValid = 0;
		States states = null;

		for(int i=0; i < L1C[index].length;i++)
		{
			if(L1C[index][i].getValid() == true)
			{
				//store objects in L1D
				//temp.add(L1C[index][i]);
				numberValid++;

				if(tag == L1C[index][i].getTag())
				{
					//check if it is a hit
					states = States.HIT;
					//COLUMN = i;
					transaction = lru[index].LRUHit(i);
					break;
				}
			}
		}
		if(states != States.HIT)
		{
			if (numberValid != SET_SIZE) {
				states = States.MISSI;
				transaction = lru[index].LRUMissI();
			}
			else
			{
				transaction = lru[index].LRUMissCD();
				if (L1C[index][transaction].getClean())//change fifocounter[index]
				{
					states = States.MISSC;
					
				}
				else
				{
					states = States.MISSD;
				}
			//COLUMN = temp;
			}
			
		}
		return states;
	}//end of check state


	public void populateL1C() {
		for(int i = 0; i < 64; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				ControllerObject temp = new ControllerObject(j, i, false, true);
				L1C[i][j] = temp;
				//L1C[i][j].setValid(true);
			}
			
		}
		
		for(int i = 0; i < 2; i++)
		{
			LineObject temp = new LineObject(32);
			victim.victimData[i] = temp;
			writeBuffer.writeBufferData[i] = temp;
		}
		
		
	}
	
	public void populateLRU()
	{
		for(int i = 0; i < 64; i++)
		{
			lru[i] = new LRU(4);
		}
			
	}
	
}// end of class L1CacheController



