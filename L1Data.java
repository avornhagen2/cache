import java.util.ArrayList;
import java.util.Arrays;

public class L1Data extends Cache{

	final public static int NUMBER_SETS = 64;
	final public static int SET_SIZE = 4;
	//L1CacheController L1C = new L1CacheController();
	private LineObject[][] L1D = new LineObject[NUMBER_SETS][SET_SIZE];
	ArrayListQueue alq;
	public WriteBuffersForL1AndL2 writeBuffer = new WriteBuffersForL1AndL2(alq);
	public VictimCacheForL1 victim = new VictimCacheForL1();
	final private static int L1CtoL1D = 1;
	final private static int L1DtoL1C = 6;
	boolean[][] busyCheckL1;
	ArrayList<Integer> busyAddresses;

	//String[][][] L1D = new String[L1C.NUMBER_SETS][L1C.SET_SIZE][blockSize];
	
	public L1Data(ArrayListQueue alq, ArrayList<Integer> busyAddresses, boolean[][] busyCheckL1)
	{
		this.alq = alq;
		this.busyAddresses = busyAddresses;
		this.busyCheckL1 = busyCheckL1;
	}
	
	public void run()
	{
		if(!alq.isSingleQueueEmpty(L1CtoL1D) && alq.getHeadOfQueueWait(L1CtoL1D) == false)
		{
			QueueObject messageAndWait = alq.dequeue(L1CtoL1D);
			String input = messageAndWait.getMessage();
			messageAndWait.setWait(true);
			
			String[] split = input.trim().split(" ");
			String operation = split[0];
			String output = "";
			int Address = 0;
			int Offset = 0;
			
			if(split[0].equals("VictimCache") || split[0].equals("WriteBuffer"))
			{
				Address = Integer.parseInt(split[1].substring(0, 4));
			}else {
				Address = Integer.parseInt(split[1].substring(0, 4));
				Offset = Integer.parseInt(split[1].substring(4,6));
			}
			
			
			int Tag = Address / NUMBER_SETS;
			int Index = Address % NUMBER_SETS; 
			int byteSize = Integer.parseInt(split[2]);
			
			if(operation.equals("CPURead"))
			{
				QueueObjectChild qoc = new QueueObjectChild(byteSize);
				
				for(int i = Offset; i < byteSize + Offset;i++)
				{
					//output = output + L1D[Index][messageAndWait.getTransactionL1()].getBlockValue(i);
					qoc.block.setBlockValue(L1D[Index][messageAndWait.getTransactionL1()].getBlockValue(i), i - Offset);
				}
				
				qoc.setMessage(messageAndWait.getMessage());
				alq.enqueue(L1DtoL1C, qoc);
				busyAddresses.remove(busyAddresses.indexOf(Address));
				busyCheckL1[Index][messageAndWait.getTransactionL1()] = false;
				
			}else if(operation.equals("CPUWrite"))
			{
				
				if(messageAndWait instanceof QueueObjectChild)
				{
					for(int i = Offset; i < Offset + byteSize; i++)
					{
						//output = "Write Success";
						L1D[Index][messageAndWait.getTransactionL1()].setBlockValue(((QueueObjectChild) messageAndWait).block.getBlockValue(i-Offset), i);
						
					}
				}else {
					char[] data = split[3].toCharArray(); 
					for(int i = Offset; i < Offset + byteSize; i++)
					{
						//output = "Write Success";
						L1D[Index][messageAndWait.getTransactionL1()].setBlockValue(data[i - Offset], i);
						
					}
				}
				
				busyAddresses.remove(busyAddresses.indexOf(Address));
				busyCheckL1[Index][messageAndWait.getTransactionL1()] = false;
				//alq.enqueue(L1DtoL1C, messageAndWait);//???
				//System.out.println("Write to L1D Success");
				//we are not enqueueing on a write
				
			}else if(operation.equals("WriteBuffer"))
			{
				writeBuffer.setWriteBufferValue(Tag, Index, L1D[Index][messageAndWait.getTransactionL1()], "SendToL2");
			}else if(operation.equals("VictimCache"))
			{	
				victim.setVictimCacheValueDirectly(Tag, Index, L1D[Index][messageAndWait.getTransactionL1()]);
			}else if(operation.equals("UpdateReadL1"))
			{
				
				
				for(int i = Offset; i < Offset + byteSize; i++)
				{
					//output = "Write Success";
					L1D[Index][messageAndWait.getTransactionL1()].setBlockValue(((QueueObjectChild)messageAndWait).block.getBlockValue(i-Offset), i);
				}
				
				busyAddresses.remove(busyAddresses.indexOf(Address));
				busyCheckL1[Index][messageAndWait.getTransactionL1()] = false;
				//System.out.println("Write to L1D Success");
				//we are not enqueueing on a write
				
			}else if(operation.equals("UpdateWriteL1"))
			{
				
				
				for(int i = Offset; i < Offset + byteSize; i++)
				{
					//output = "Write Success";
					L1D[Index][messageAndWait.getTransactionL1()].setBlockValue(((QueueObjectChild)messageAndWait).block.getBlockValue(i-Offset), i);
				}
				
				//alq.enqueue(L1DtoL1C, messageAndWait);//???
				//System.out.println("Write to L1D Success");
				//we are not enqueueing on a write
				busyAddresses.remove(busyAddresses.indexOf(Address));
				busyCheckL1[Index][messageAndWait.getTransactionL1()] = false;
			}else if(operation.equals("MutualInclusionCheckClean"))
			{
				if(!Arrays.equals(((QueueObjectChild)messageAndWait).block.getBlock(), L1D[Index][messageAndWait.getTransactionL1()].getBlock()))
				{
					QueueObjectChild head = new QueueObjectChild(32);
					head.block.setBlock(L1D[Index][messageAndWait.getTransactionL1()].getBlock());
					head.setMessage("MutualInclusionChecktoDRAM " + Address + "00 0");
					sendRequestToDestination(head, L1DtoL1C, alq);
				}
			}else if(operation.equals("MutualInclusionCheckDirty"))
			{
				QueueObjectChild head = new QueueObjectChild(32);
				head.block.setBlock(L1D[Index][messageAndWait.getTransactionL1()].getBlock());
				head.setMessage("MutualInclusionChecktoDRAM " + Address + "00 0");
				sendRequestToDestination(head, L1DtoL1C, alq);
			}
		}
	}
	
//	public L1Data(int numberOfSets, int setSize) {
//		super();
//		L1D = new LineObject[numberOfSets][setSize];
//	}
	



	public void setL1DValue(char input, int row, int column,int offset) {
		
		L1D[row][column].setBlockValue(input,offset);
	}


	public LineObject getL1DLineObject(int row, int column) {
		return L1D[row][column];
	}




	public char getL1DValue (int row, int column, int byteIndex){
		return L1D[row][column].getBlock()[byteIndex];
	}

	public char[] getL1DBlock(int row, int column) {
		
		return L1D[row][column].getBlock();
	}

	public void populateL1D() {
		for(int i = 0; i < NUMBER_SETS; i++)
		{
			for(int j = 0; j < SET_SIZE; j++)
			{
				LineObject temp = new LineObject(32);
				temp.populateLineObject();
				L1D[i][j] = temp;
			}
		}
	}
	

//	public void setBlock()
//	{
//		
//	}
	
	
}//end of L1Data
