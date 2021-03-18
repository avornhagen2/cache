
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
	//String[][][] L1D = new String[L1C.NUMBER_SETS][L1C.SET_SIZE][blockSize];
	
	public L1Data(ArrayListQueue alq)
	{
		this.alq = alq;
	}
	
	public void run()
	{
		if(!alq.isSingleQueueEmpty(L1CtoL1D))
		{
			QueueObject messageAndWait = alq.dequeue(L1CtoL1D);
			String input = messageAndWait.getMessage();
			messageAndWait.setWait(true);
			
			String[] split = input.trim().split(" ");
			String operation = split[0];
			String output = "";
			
			if(operation == "CPURead")
			{
				int Address = Integer.parseInt(split[1].substring(0, 4));
				int Tag = Address / NUMBER_SETS;
				int Index = Address % NUMBER_SETS; 
				int Offset = Integer.parseInt(split[1].substring(4,6));
				int byteSize = Integer.parseInt(split[2]);
				

				
				
				for(int i = Offset; i < byteSize + Offset;i++)
				{
					output = output + L1D[Index][Tag].getBlockValue(i);
				}
				
				messageAndWait.setMessage("SendToCPU " + output);
				alq.enqueue(L1DtoL1C, messageAndWait);
				
			}else if(operation == "CPUWrite")
			{
				int Tag = Integer.parseInt(split[1].substring(0, 2));
				int Index = Integer.parseInt(split[1].substring(2, 4));
				int Offset = Integer.parseInt(split[1].substring(4,6));
				int byteSize = Integer.parseInt(split[2]);
				String[] data = split[3].trim().split(".");
				
				for(int i = Offset; i < data.length + Offset; i++)
				{
					//output = "Write Success";
					L1D[Index][Tag].setBlockValue(data[i-Offset], i);
				}
				
				System.out.println("Write to L1D Success");
				//we are not enqueueing on a write
				
			}else if(operation == "WriteBuffer")
			{
				int Tag = Integer.parseInt(split[1]);
				int Index = Integer.parseInt(split[2]);
				
				writeBuffer.setWriteBufferValue(Tag, Index, L1D[Index][Tag], "SendToL2");
				//set block to temp block
				//move temp block to write buffer
			}else if(operation == "VictimCache")
			{
				int Tag = Integer.parseInt(split[1]);
				int Index = Integer.parseInt(split[2]);
				
				victim.setVictimCacheValueDirectly(Tag, Index, L1D[Index][Tag]);
			}
			
			
		}
	}
	
	public L1Data(int numberOfSets, int setSize) {
		super();
		L1D = new LineObject[numberOfSets][setSize];
	}
	



	public void setL1DValue(String input, int row, int column,int offset) {
		
		L1D[row][column].setBlockValue(input,offset);
	}


	public LineObject getL1DLineObject(int row, int column) {
		return L1D[row][column];
	}




	public String getL1DValue (int row, int column, int byteIndex){
		return L1D[row][column].getBlock()[byteIndex];
	}

	public String[] getL1DBlock(int row, int column) {
		
		return L1D[row][column].getBlock();
	}
	

//	public void setBlock()
//	{
//		
//	}
	
	
}//end of L1Data
