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
	static VictimCacheForL1 victim = new VictimCacheForL1();
	static ArrayListQueue alq;
	WriteBuffersForL1AndL2 writeBuffer = new WriteBuffersForL1AndL2(alq);
	LRU lru;
	ArrayList<String> InstructionsL1 = new ArrayList<String>();
	ArrayList<Integer> busyAddresses;// = new ArrayList<Integer>();
	
	
	
	
	//static int[] fifoCounter = new int[NUMBER_SETS];
	public L1CacheController (ArrayListQueue alq, L1Data L1D, ArrayList<Integer> busyAddresses) {
		this.L1D = L1D;
		this.alq = alq;
		SET_SIZE = 4;
		NUMBER_SETS = 64;
		L1C = new ControllerObject[NUMBER_SETS][SET_SIZE];
		this.busyAddresses = busyAddresses;
		lru = new LRU(SET_SIZE);
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
				}else {
					message = InstructionsL1.get(i);
					String[] splitInstructions = message.trim().split(" ");
					Address = Integer.parseInt(splitInstructions[1].substring(0, 4));
					if(!busyAddresses.contains(Address))
					{
						busyAddresses.add(Address);
						messageAndWait.setMessage(message);
						messageAndWait.setWait(true);
						InstructionsL1.remove(i);
						flag = false;
						
						
						//String[] split = message.trim().split(" ");
						//int Address = Integer.parseInt(split[1].substring(0, 4));
						int Tag = Address / NUMBER_SETS;
						int Index = Address % NUMBER_SETS; 
						
						States currentState = check_StateL1(Index, Tag);
						
						if(writeBuffer.checkValue(Tag,Index))
						{
							writeToL1FromWriteBuffer(Tag, Index, currentState, writeBuffer.getWriteBufferValue(Tag,Index));
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
							L1C[Index][messageAndWait.getTransactionL1()].setValid(true);
							L1C[Index][messageAndWait.getTransactionL1()].setTag(Tag);
						}else if(currentState == States.MISSI)
						{
							messageAndWait.setTransactionL1(transaction);
							alq.enqueue(L1CtoL2, messageAndWait);//on a miss we enqueue to L2
						}else if(currentState == States.MISSD)
						{
							QueueObject writeBufferObject = new QueueObject();
							String writeBufferMessage = "WriteBuffer " + Tag + " " + Index;
							writeBufferObject.setMessage(writeBufferMessage);
							messageAndWait.setTransactionL1(transaction);
							alq.enqueue(L1CtoL1D, writeBufferObject);
							
							alq.enqueue(L1CtoL2, messageAndWait);//send request to L2 to get the data
						}else if(currentState == States.MISSC)
						{
							QueueObject victimCacheObject = new QueueObject();
							String victimCacheMessage = "VictimCache " + Tag + " " + Index;
							victimCacheObject.setMessage(victimCacheMessage);
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
				
			}
		
		}
		
		if(!alq.isSingleQueueEmpty(L2toL1C) && alq.getHeadOfQueueWait(L2toL1C) == false) //L2 to L1C
		{
			QueueObjectChild messageAndWait = (QueueObjectChild) alq.dequeue(L2toL1C);
			String input = messageAndWait.getMessage();
			messageAndWait.setWait(true);
			
			String[] split = input.trim().split(" ");
			int Address = Integer.parseInt(split[1].substring(0, 4));
			int Tag = Address / NUMBER_SETS;
			int Index = Address % NUMBER_SETS; 
			int byteSize = Integer.parseInt(split[2]);
			int Offset = Integer.parseInt(split[1].substring(4,6));
			
			L1C[Index][messageAndWait.getTransactionL1()].setValid(true);
			L1C[Index][messageAndWait.getTransactionL1()].setTag(Tag);
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
				newMessage.setMessage("UpdateReadL1 " + Address + "00 " + "32");
				newMessage.setWait(true);
				alq.enqueue(L1CtoL1D, newMessage);
				//send to CPU
			}else if(split[0].equals("CPUWrite"))
			{
				QueueObjectChild newQueueObject = messageAndWait;
				char[] c = split[3].toCharArray();
				newQueueObject.setMessage("UpdateWriteL1 " + Address + "00 " + "32" + " " + split[3]);
				for(int i = 0; i < byteSize; i++)
				{
					newQueueObject.block.setBlockValue(c[i], Offset + i);
				}
				alq.enqueue(L1CtoL1D, newQueueObject);
			}
					
			
		}

		
	}// end of readFromCPU
	
	
	public void writeToL1FromWriteBuffer(int Tag, int Index, States currentState, LineObject data)
	{
		if(currentState == States.HIT)
		{
			//do nothing
		}else if(currentState == States.MISSI)
		{
			L1D.getL1DLineObject(Index, transaction).setBlock(data.getBlock());
			L1C[Index][transaction].setTag(Tag);
			L1C[Index][transaction].setIndex(Index);
		}else if(currentState == States.MISSD)
		{
			writeBuffer.setWriteBufferValue(L1C[Index][transaction].getTag(), Index, L1D.getL1DLineObject(Index, transaction), "SendToL2");
			L1D.getL1DLineObject(Index, transaction).setBlock(data.getBlock());
			L1C[Index][transaction].setTag(Tag);
		}else if(currentState == States.MISSC)
		{
			victim.setVictimCacheValueDirectly(L1C[Index][transaction].getTag(), Index, L1D.getL1DLineObject(Index, transaction));
			L1D.getL1DLineObject(Index, transaction).setBlock(data.getBlock());
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
		}else if(currentState == States.MISSC)
		{
			victim.setVictimCacheValueDirectly(L1C[Index][transaction].getTag(), Index, L1D.getL1DLineObject(Index, transaction));
			L1D.getL1DLineObject(Index, transaction).setBlock(data.getBlock());
			L1C[Index][transaction].setTag(Tag);
		}
	}
//	public static void outputToCPU()
//	{
//		
//		//enqueue to stub
//		
//		
//	}//end of Write to CPU
//	
//	
//	public static void writeToL1Data(int index, int tag, int offset, int numberOfBytes, int[] tagAndIndex, String[] dataFromCPU, ArrayListQueue alq)
//	{
//		
//		//request a line from L1D
//		//get a state of that line
//		States state;
//		//check victim cache
//			//if in victim cache, then write to L1C and L1D
//			//else do nothing
//		victim.check(tag,index);
//		//check write buffer
//			//if in write buffer, then write to L1C
//			//else do nothing
//		writebuffer.check();
//		state = check_HitL1(index, tag);
//		//debug the inputs to make sure they are working as expected
//		//row column tag index
//		switch (state)
//		{
//			case HIT:
//				//enqueue to CPU L1
//				alq.enqueue(L1CtoL1D, input);
//				writeHit(index, lru[index].tail(), offset, numberOfBytes, dataFromCPU);
//				break;
//			default:
//				//enqueue to L2
//				alq.enqueue(L1CtoL2, input);
////			case MISSC:
////				//send current data to victim cache, enqueue to L1 to L2 the message
////				writeMISSC(index, lru[index].tail(), offset, numberOfBytes, tagAndIndex, dataFromCPU);//look into this
////				break;
////			case MISSD:
////				//send data to writebuffer, enqueue to L1 to L2 the message
////				writeMISSD(index, lru[index].tail(), offset, numberOfBytes, dataFromCPU);//look into this
////				break;
////			case MISSI:
////				//enqueue to L1 to L2 the message
////				writeMISSI(index, lru[index].tail(), offset, numberOfBytes, dataFromCPU);
////				break;
//		}
//		
//	}//end of writeToL1Data
//	
//	//transient states for L1 Controller
////	Rdwaitd		Waiting for data from L1 for Read
////	RdwaitL2d	Waiting for data from L2 for Read
////	Rdwait1d	Waiting for data from L1/L2 for Read 
////	Rdwait2d	Waiting for data from L1 and L2 for Read
////	Wrwaitd		Waiting for data from L2 for Write
////	Wrwait1d	Waiting for data from L1/L2 for Write
////	Wrwait2d	Waiting for data from L1 and L2 for Write
////	Wralloc		Write Allocation done
//	
//	//wait state
//	//finished state
//
//	
//	
//	public static void readFromL1Data( int index, int tag, int offset, int bytes, int[] tagAndIndex, ArrayListQueue alq)
//	{
//		//request a line from L1D
//		//ArrayList<ControllerObject> temp = new ArrayList<ControllerObject>();
//		//ControllerObject temp;
//		//String command 
//		States state;
//		victim.check(tag, index);
//		writebuffer.check();
//		state = check_HitL1(index, tag);//add way to look into write buffer and victim cache here
//		//debug the inputs to make sure they are working as expected
//		//row column tag index
//		switch (state)
//		{
//			case HIT:
//				alq.enqueue(L1CtoL1D,input);
//				//readHit(index, lru[index].tail(), offset, bytes, L1D);
//				break;
//			case MISSC:
//				readMISSC(index, lru[index].tail(), tagAndIndex, L2);//look into this
//				break;
//			case MISSI:
//				readMISSI(index, lru[index].tail(), offset, bytes);
//				break;
//			case MISSD:
//				readMISSD(index, lru[index].tail(), tagAndIndex, L2);//look into this
//				break;
//		}
//		
//
//		
//		//get a state of that line
//		//switch statement for what to do based on the state
//		
//		
//	}//readFromL1Data
//	
//	
//	public static void readHit(int row, int column, int offset, int numberOfBytes, L1Data L1D)
//	{
//		
//		String readResult = "";
//
//		for(int i = 0; i < numberOfBytes; i++) {
//			
//			readResult += L1D.getL1DValue(row, column, i + offset);
//			//readFinal += readFinal.valueOf(readResult);
//			//need to check if this still works the way we want
//		}
//		
//		outputToCPU();
//	}//end of readHit
//	
//	
//	
//	public static void readMISSI(int row, int column, int offset, int byteNumber)
//	{
//		String[] input = L2.readFromL2();//put inputs here (tag, index)
//		
//		for(int i = 0; i < L1D.getL1D().blockSize; i++) {
//			L1D.setL1DValue(input[i], row, column, i);
//		}
//		
//		L1C[row][column].setClean(true);//set to clean
//			
//		outputToCPU();
//	}
//	
//	public static void readMISSC(int row, int column, int[] tagAndIndex, L2Cache L2)
//	{
//		//victimize
//		int replace = fifoCounter[row];
//		int sets = 1;
//		int[] info = new int[] {row,column};	
//		
//		//send data from L1D to victim
//		String[] dataFromL1D = L1D.getL1DBlock(row, column);
//		writeToVictimCache(row, column, tagAndIndex, dataFromL1D, victim);
//		
//		String[] input = L2.readFromL2();
//	
//		for(int i = 0; i < L1D.getL1D().blockSize; i++) {
//			L1D.setL1DValue(input[i], row, column, i);
//		}
//				
//			
//		outputToCPU();
//	}
//	
//	public static void readMISSD(int row, int column) {
//		int replace = fifoCounter[row];
//		int sets = 1;
//		int[] info = new int[] {row,column};	
//		
//		//send data from L1D to write buffer
//		String[] dataFromL1D = L1D.getL1DBlock(row, column);
//		writeBuffer();
//		
//		String[] input = L2.readFromL2();
//	
//		for(int i = 0; i < L1D.getL1D().blockSize; i++) {
//			L1D.setL1DValue(input[i], row, column, i);
//		}
//				
//			
//		outputToCPU();
//	}
//	
//	public static void writeHit(int row, int column, int offset, int byteNumber, String[] input)
//	{
//		for(int i = 0; i < byteNumber; i++) {
//			L1D.setL1DValue(input[i], row, column,offset + i);
//		}
//		
//		L1C[row][column].setClean(false);//set to dirty
//	}
//	
//	public static void writeMISSD(int row, int column, int offset, int byteNumber, String[] input)
//	{
//		writeBuffer();
//		getDataFromL2toL1(row, column, offset, byteNumber);
//		
//	}
//	
//	public static void writeMISSC(int row, int column, int offset, int byteNumber, int[] tagAndIndex, String[] dataFromCPU) {
//		
//
//		//get tag and index and data from parsing CPU input
//		writeToVictimCache(row, column, tagAndIndex, dataFromCPU, victim);
//		getDataFromL2toL1(row, column, offset, byteNumber);
//		
//	}
//	
//	public static void writeMISSI(int row, int column, int offset, int byteNumber, String[] input) {
//		
//		getDataFromL2toL1(row, column, offset, byteNumber);
//		
//	}
//
//	
//	public static void getDataFromL2toL1(int row, int column, int offset, int byteNumber) {
//		String[] dataFromL2 = L2.readFromL2();
//		
//		for(int i = 0; i < byteNumber; i++) {
//			//writing data from L2 into L1D
//			L1D.setL1DValue(dataFromL2[i], row, column, offset + i);
//		}
//		
//		L1C[row][column].setClean(false);//set to dirty
//		
//	}//end of getDataFromL2toL1
//	
//	public static void writeToVictimCache(int row, int column, int[] tagAndIndex, String[] data, VictimCacheForL1 victim) {
//		
//		victim.setVictimInstructionValue(row, tagAndIndex);
//		victim.setVictimDataValue(row, data);
//
//		
//	}//end of victimize
//	
//	public static void writeBuffer() {
//		
//	}
	
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
					transaction = lru.LRUHit(i);
					break;
				}
			}
		}
		if(states != States.HIT)
		{
			if (numberValid != SET_SIZE) {
				states = States.MISSI;
				transaction = lru.LRUMissI();
			}
			else
			{
				transaction = lru.LRUMissCD();
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
	}
	

	
}// end of class L1CacheController



