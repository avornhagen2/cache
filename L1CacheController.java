import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class L1CacheController
{

	//we are going to be using FIFO for replacing data
	public ControllerObject Address;
	final public static int SET_SIZE = 4;
	final public int INDEX = 6;
	final public static int NUMBER_SETS = 64;
	final public int TAG = 6;
	static ControllerObject L1C[][] = new ControllerObject[NUMBER_SETS][SET_SIZE];
	//static L1Data L1D[][] = new L1Data[NUMBER_SETS][SET_SIZE];
	static int[] fifoCounter = new int[NUMBER_SETS];

	
	//OFFSET = log2(width of block/smallest number of bytes accessed) = log2(32b/1b) = 5
			
	//INDEX = log2(cache size/(block size * number of ways)) = log2(8kb/(32*4)) = log2(2^13/2^7) = 6
	
	//TAG = main memory size - (index + offset) = 17 - (5 + 6) = 6
	
	//lines = cache size/block size = 8KB/32B = 256B
	
	//Sets = lines/ways = 64B
	
	
	public static void inputFromCPU()
	{
		
		//dequeue from stub
		//Queue<ControllerObject> q = new LinkedList<>();
		
		//contents of L1C array
		//state, valid, 
		
		
	}// end of readFromCPU
	
	
	public static void outputToCPU()
	{
		
		//enqueue to stub
		
	}//end of Write to CPU
	
	
	public static void writeToL1Data(int tag, int index, int offset, int bytes)
	{
		
		//request a line from L1D
		//get a state of that line
		States state;
		state = check_State(tag, index);
		switch (state)
		{
			case HIT:
				writeHit(tag, index, offset, bytes);
				break;
			case MISSC:
				writeMISSC();
				break;
			case MISSD:
				writeMISSD();
				break;
			case MISSI:
				writeMISSI();
				break;
		}
		
	}//end of writeToL1Data
	
	//transient states for L1 Controller
//	Rdwaitd		Waiting for data from L1 for Read
//	RdwaitL2d	Waiting for data from L2 for Read
//	Rdwait1d	Waiting for data from L1/L2 for Read 
//	Rdwait2d	Waiting for data from L1 and L2 for Read
//	Wrwaitd		Waiting for data from L2 for Write
//	Wrwait1d	Waiting for data from L1/L2 for Write
//	Wrwait2d	Waiting for data from L1 and L2 for Write
//	Wralloc		Write Allocation done
	
	//wait state
	//finished state

	
	
	public static void readFromL1Data( int tag, int index, int offset, int bytes)
	{
		//request a line from L1D
		//ArrayList<ControllerObject> temp = new ArrayList<ControllerObject>();
		//ControllerObject temp;
		States state;
		state = check_State(tag, index);
		switch (state)
		{
			case HIT:
				readHit(tag, index, offset, bytes);
				break;
			case MISSC:
				readMISSC();
				break;
			case MISSD:
				readMISSD();
				break;
			case MISSI:
				readMISSI();
				break;
		}
		

		
		//get a state of that line
		//switch statement for what to do based on the state
		
		
	}//readFromL1Data
	
	
	public static void readHit(int row, int column, int offset, int numberOfBytes, L1Data L1D)
	{
		
		String readResult = "";

		
		for(int i = 0; i < numberOfBytes; i++) {
			
			readResult += L1D.getL1DValue(row, column, i + offset);
			//readFinal += readFinal.valueOf(readResult);
			//need to check if this still works the way we want
		}
		
		outputToCPU();
	}//end of readHit
	
	public static void writeHit(int tag, int index, int offset, int bytes)
	{
		
	}
	
	public static String readMISSC(int tag, int index)
	{
		//go to L2C
		return null;
	}
	
	public static String readMISSD()
	{
		//Victimize
		return null;
	}
	
	public static String readMISSI()
	{
		//go to L2C
		return null;
	}
	
	public static String writeMISSC()
	{
		return null;
		
	}
	
	public static String writeMISSD() {
		return null;
	}
	
	public static String writeMISSI() {
		return null;
	}

	// Get memory address ( tag and index) and get the block State
	//		is it valid? --> what is stage of control?
	//		Hit = valid is true && requested address found in cache
	//		Missc = valid is true && requested address not found in cache && line is clean state
	//		Missd = valid is true && requested address not found in cache && line is dirty state
	//		Missi = valid is false && requested address not found in cache
	public static States check_State(int tag, int index)
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
					break;
				}
			}
		}
		if(states != States.HIT)
		{
			if (numberValid != SET_SIZE) {
				states = States.MISSI;
			}
			else
				{

				if (L1C[index][fifoCounter[index]].getClean())
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
	//Whenever called add one to the counter in the respective row
	// if the value of the counter is bigger than setSIZE ste counter to zero
	public static void fifoStepper(int row)
	{
		fifoCounter[row] += 1;
		if (fifoCounter[row] >= SET_SIZE)
		{
			fifoCounter[row] = 0;
		}
	}
}// end of class L1CacheController



