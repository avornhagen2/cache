import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class L1CacheController extends Cache
{	
	
	

	//we are going to be using FIFO for replacing data
	public ControllerObject Address;
	//final public static int SET_SIZE = 4;
	//final public int INDEX = 6;
	//final public static int NUMBER_SETS = 64;
	//final public int TAG = 6;
	static ControllerObject L1C[][] = new ControllerObject[NUMBER_SETS][SET_SIZE];
	static CPUStub CPU;
	static L1Data L1D = new L1Data(NUMBER_SETS,SET_SIZE);
	static VictimCacheForL1 victim = new VictimCacheForL1();
	static L2Cache L2;
	
	//static int[] fifoCounter = new int[NUMBER_SETS];
	public L1CacheController (CPUStub CPUStub, L2Cache L2Cache) {
		CPU = CPUStub;
		L2 = L2Cache;
	}

	
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
	
	
	public static void writeToL1Data(int row, int column, int offset, int numberOfBytes, int[] tagAndIndex, String[] dataFromCPU)
	{
		
		//request a line from L1D
		//get a state of that line
		States state;
		//check victim cache
			//if in victim cache, then write to L1C
			//else do nothing
		//check write buffer
			//if in write buffer, then write to L1C
			//else do nothing
		state = check_State(row, column, L1C);
		switch (state)
		{
			case HIT:
				writeHit(row, column, offset, numberOfBytes, dataFromCPU);
				break;
			case MISSC:
				writeMISSC(row, column, offset, numberOfBytes, dataFromCPU);
				break;
			case MISSD:
				writeMISSD(row, column, offset, numberOfBytes, tagAndIndex,  dataFromCPU);
				break;
			case MISSI:
				writeMISSI(row, column, offset, numberOfBytes, dataFromCPU);
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

	
	
	public static void readFromL1Data( int row, int column, int offset, int bytes, int[] tagAndIndex)
	{
		//request a line from L1D
		//ArrayList<ControllerObject> temp = new ArrayList<ControllerObject>();
		//ControllerObject temp;
		States state;
		
		state = check_State(row, column, L1C);//add way to look into write buffer and victim cache here
		switch (state)
		{
			case HIT:
				readHit(row, column, offset, bytes, L1D);
				break;
			case MISSC:
				readMISSC();
				break;
			case MISSI:
				readMISSI(row, column, offset, bytes);
				break;
			case MISSD:
				readMISSD(row, column, tagAndIndex, L2);
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
	
	
	
	public static void readMISSI(int row, int column, int offset, int byteNumber)
	{
		String[] input = L2.readFromL2();//put inputs here (tag, index)
		
		for(int i = 0; i < L1D.getL1D().blockSize; i++) {
			L1D.setL1DValue(input[i], row, column, i);
		}
		
		L1C[row][column].setClean(true);//set to clean
			
		outputToCPU();
	}
	
	public static void readMISSC(int row, int column, int[] tagAndIndex, L2Cache L2)
	{
		//victimize
		int replace = fifoCounter[row];
		int sets = 1;
		int[] info = new int[] {row,column};	
		
		//send data from L1D to victim
		String[] dataFromL1D = L1D.getL1DBlock(row, column);
		writeToVictimCache(row, column, tagAndIndex, dataFromL1D, victim);
		
		String[] input = L2.readFromL2();
	
		for(int i = 0; i < L1D.getL1D().blockSize; i++) {
			L1D.setL1DValue(input[i], row, column, i);
		}
				
			
		outputToCPU();
	}
	
	public static void readMISSD(int row, int column) {
		int replace = fifoCounter[row];
		int sets = 1;
		int[] info = new int[] {row,column};	
		
		//send data from L1D to write buffer
		String[] dataFromL1D = L1D.getL1DBlock(row, column);
		writeBuffer();
		
		String[] input = L2.readFromL2();
	
		for(int i = 0; i < L1D.getL1D().blockSize; i++) {
			L1D.setL1DValue(input[i], row, column, i);
		}
				
			
		outputToCPU();
	}
	
	public static void writeHit(int row, int column, int offset, int byteNumber, String[] input)
	{
		for(int i = 0; i < byteNumber; i++) {
			L1D.setL1DValue(input[i], row, column,offset + i);
		}
		
		L1C[row][column].setClean(false);//set to dirty
	}
	
	public static void writeMISSD(int row, int column, int offset, int byteNumber, String[] input)
	{
		writeBuffer();
		getDataFromL2toL1(row, column, offset, byteNumber);
		
	}
	
	public static void writeMISSC(int row, int column, int offset, int byteNumber, int[] tagAndIndex, String[] dataFromCPU) {
		

		//get tag and index and data from parsing CPU input
		writeToVictimCache(row, column, tagAndIndex, dataFromCPU, victim);
		getDataFromL2toL1(row, column, offset, byteNumber);
		
	}
	
	public static void writeMISSI(int row, int column, int offset, int byteNumber, String[] input) {
		
		getDataFromL2toL1(row, column, offset, byteNumber);
		
	}

	
	public static void getDataFromL2toL1(int row, int column, int offset, int byteNumber) {
		String[] dataFromL2 = L2.readFromL2();
		
		for(int i = 0; i < byteNumber; i++) {
			//writing data from L2 into L1D
			L1D.setL1DValue(dataFromL2[i], row, column, offset + i);
		}
		
		L1C[row][column].setClean(false);//set to dirty
		
	}//end of getDataFromL2toL1
	
	public static void writeToVictimCache(int row, int column, int[] tagAndIndex, String[] data, VictimCacheForL1 victim) {
		
		victim.setVictimInstructionValue(row, victim.fifoCounter[0], tagAndIndex);
		victim.setVictimDataValue(row, data);

		
	}//end of victimize
	
	public static void writeBuffer() {
		
	}
	
}// end of class L1CacheController



