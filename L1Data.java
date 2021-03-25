import java.util.ArrayList;

public class L1Data {

	final public static int NUMBER_SETS = 64;
	final public static int SET_SIZE = 4;
	//L1CacheController L1C = new L1CacheController();
	private LineObject[][] L1D = new LineObject[NUMBER_SETS][SET_SIZE];
	private WriteBuffersForL1AndL2 writeBuffer;
	private VictimCacheForL1 victim;
	final private static int L1CtoL1D = 1;
	final private static int L1DtoL1C = 6;
	ArrayListQueue alq;
	ArrayList<Integer> busyAddresses;

	//String[][][] L1D = new String[L1C.NUMBER_SETS][L1C.SET_SIZE][blockSize];
	
	public L1Data(ArrayListQueue alq, ArrayList<Integer> busyAddresses)
	{
		this.alq = alq;
		this.busyAddresses = busyAddresses;
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
			
			int Address = Integer.parseInt(split[1].substring(0, 4));
			int Tag = Address / NUMBER_SETS;
			int Index = Address % NUMBER_SETS; 
			int Offset = Integer.parseInt(split[1].substring(4,6));
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
				//alq.enqueue(L1DtoL1C, messageAndWait);//???
				//System.out.println("Write to L1D Success");
				//we are not enqueueing on a write
				
			}else if(operation.equals("WriteBuffer"))
			{
				writeBuffer.setWriteBufferValue(Tag, Index, L1D[Index][Tag], "SendToL2");
			}else if(operation.equals("VictimCache"))
			{	
				victim.setVictimCacheValueDirectly(Tag, Index, L1D[Index][Tag]);
			}else if(operation.equals("UpdateReadL1"))
			{
				
				
				for(int i = Offset; i < Offset + byteSize; i++)
				{
					//output = "Write Success";
					L1D[Index][messageAndWait.getTransactionL1()].setBlockValue(((QueueObjectChild)messageAndWait).block.getBlockValue(i-Offset), i);
				}
				
				busyAddresses.remove(busyAddresses.indexOf(Address));
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
