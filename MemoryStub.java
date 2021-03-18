
public class MemoryStub {

	final int setSize = 4096;//change this to exact amount
	final public int TAG = 64; //since index = 2^6, so the ideia is 64*tag + index// need to check
	private LineObject[] DRAM = new LineObject[setSize];
	final private static int L2toDRAM = 3;
	final private static int DRAMtoL2 = 4;
	//private LineObject current;
	private LineObject updateValue;
	ArrayListQueue alq;
	
	public MemoryStub(ArrayListQueue alq)
	{
		this.alq = alq;
	}

	public void run()
	{
		if(!alq.isSingleQueueEmpty(L2toDRAM))
		{
			QueueObjectBus messageAndWait = (QueueObjectBus) alq.dequeue(L2toDRAM);//make sure that there are no nulls coming through here
			String input = messageAndWait.getMessage();
			messageAndWait.setWait(true);
			
			String[] split = input.trim().split(" ");
			int Address = Integer.parseInt(split[1].substring(0, 4));
			int busNumber = messageAndWait.getBusNumber();

			if(split[0] == "SendToDRAM")
			{
				busSwitchCase(Address,busNumber,messageAndWait.getBusData());
			}else if(split[0] == "CPURead" || split[0] == "CPUWrite")
			{
				writeBusToL2(Address,busNumber,messageAndWait.getMessage());
			}
		}
	}
	
	public void writeBusToL2(int Index, int busNumber, String message)
	{
		QueueObjectBus bus = new QueueObjectBus();
		String[] block = new String[4];
		for(int j = 0; j < 4; j++)
		{
			block[j] = DRAM[Index].getBlockValue(j + busNumber * 4);
		}
		bus.setBusData(block);
		bus.setMessage(message);
		alq.enqueue(DRAMtoL2, bus);
	}
	
	public void busSwitchCase(int Index, int busNumber, String[] busData)
	{
		switch(busNumber)
		{
			case 1 :
				writeBusToDRAM(Index,0,busData);
				break;
			case 2 :
				writeBusToDRAM(Index,4,busData);
				break;
			case 3 :
				writeBusToDRAM(Index,8,busData);
				break;
			case 4 :
				writeBusToDRAM(Index,12,busData);
				break;
			case 5 :
				writeBusToDRAM(Index,16,busData);
				break;
			case 6 :
				writeBusToDRAM(Index,20,busData);
				break;
			case 7 :
				writeBusToDRAM(Index,24,busData);
				break;
			case 8 :
				writeBusToDRAM(Index,28,busData);
				break;
		}
	}

	public void writeBusToDRAM(int Index, int offset, String[] busData)
	{
		
		for(int i = offset; i < offset + busData.length; i++)
		{
			DRAM[Index].setBlockValue(busData[i - offset], i);
		}
	}
	
	
//	public LineObject getBlock(int index, int tag)
//	{
//		int row = tag * TAG + index;
//		return DRAM[row];
//	}
//
//	//The idea of set and get valuesBus is that the Scheduler is going to ask for the values in parts
//	//giving the initial value position and the total size of the bus. The input and output are Strings
//	//that need to be splited and trim before used.
//	// if whanted we can change the communication to be instead of string use arrays.
//	public void setValuesBus(int index, int tag, int startValue, int busSize, String input)
//	{
//		int row = tag * TAG + index;
//		String[] split = input.trim().split(" ");
//		for (int i = 0; i < busSize; i++)
//		{
//			DRAM[row].setBlockValue(split[i], startValue + i); //set value for the array string once at a time
//		}
//
//	}
//
//	public String getValuesBus (int index, int tag, int startValue, int busSize) //can change the output to an array if needed
//	{
//		LineObject current = getBlock(index, tag);
//		String output = "";
//		for (int i = 0; i < busSize; i++)
//		{
//			output += current.getBlockValue(i) + " "; //each value is going to be separated by an space, need to remember to separate the string latter
//		}
//		return output;
//	}


}//end of memory stub
